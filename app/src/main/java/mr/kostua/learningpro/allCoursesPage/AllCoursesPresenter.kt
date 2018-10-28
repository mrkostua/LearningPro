package mr.kostua.learningpro.allCoursesPage

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.tools.DBObserverHelper
import mr.kostua.learningpro.tools.ShowLogs
import java.util.ArrayList
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
class AllCoursesPresenter @Inject constructor(private val db: DBHelper) : AllCoursesContract.Presenter {
    private val TAG = this.javaClass.simpleName
    override lateinit var view: AllCoursesContract.View
    private val disposables = CompositeDisposable()
    private var mainCoursesList = ArrayList<CourseDo>()
    private var checkedButton = CourseFinishedButtonsEnum.NONE

    override fun takeView(view: AllCoursesContract.View) {
        this.view = view
    }

    override fun subscribeCourseItemClick(observable: Observable<CourseDo>) {
        disposables.add(observable.subscribe {
            saveLastOpenedCourseId(it.id!!)
            if (it.reviewed) {
                if (it.questionsAmount == it.doneQuestionsAmount) {
                    view.createCourseFinishedDialog(it.id!!)
                } else {
                    view.startPracticeCardsActivity(it.id!!)
                }
            } else {
                view.startPreviewActivity(it.id!!)
            }
        })
    }

    override fun populateCourses() {
        disposables.add(db.getAllCourses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<List<CourseDo>>() {
                    override fun onComplete() {}

                    override fun onNext(coursesList: List<CourseDo>) {
                        view.setPBVisibility(false)
                        if (view.isCourseListInitialized()) {
                            ShowLogs.log(TAG, "populateCourses onNext isCourseListInitialized true")
                            addNewListToMainCourses(coursesList)
                            view.updateCourseList()
                        } else {
                            ShowLogs.log(TAG, "populateCourses onNext isCourseListInitialized false")
                            view.initializeRecycleView(addNewListToMainCourses(coursesList))
                        }
                    }

                    override fun onError(t: Throwable) {
                        view.setPBVisibility(true)
                        view.showFailedPopulationDialog()
                    }

                }))
    }

    override fun startLearningCourseAgain(courseId: Int) {
        view.setPBVisibility(true)
        disposables.add(DBObserverHelper.setCourseQuestionsToNotLearned(db, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(effectedCardsAmount: Int) {
                if (effectedCardsAmount > 0) {
                    resetDoneQuestionsAmountToZero(courseId)
                } else {
                    view.setPBVisibility(false)
                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "startLearningCourseAgain error : ${e.message}")
                view.setPBVisibility(false)
            }
        }, courseId))
    }

    override fun saveLastOpenedCourseId(courseId: Int) {
        db.saveLastOpenedCourseId(courseId)
    }

    override fun dialogButtonPracticeCourseAgainClickListener() {
        checkedButton = CourseFinishedButtonsEnum.PRACTICE_COURSE_AGAIN
    }

    override fun dialogButtonShowAllCardsClickListener() {
        checkedButton = CourseFinishedButtonsEnum.SHOW_ALL_CARDS
    }

    override fun dialogButtonDo(courseId: Int) {
        when (checkedButton) {
            CourseFinishedButtonsEnum.PRACTICE_COURSE_AGAIN -> {
                startLearningCourseAgain(courseId)
                view.dismissFinishedCourseDialog()
            }
            CourseFinishedButtonsEnum.SHOW_ALL_CARDS -> {
                view.startActivityToShowAllCard(courseId)
                view.dismissFinishedCourseDialog()
            }
            CourseFinishedButtonsEnum.NONE -> {
                view.showToast("No actions chosen, please " +
                        "choose action and press \"Do\"")
            }
        }
        checkedButton = CourseFinishedButtonsEnum.NONE
    }

    override fun disposeAll() {
        disposables.clear()
    }

    private fun resetDoneQuestionsAmountToZero(courseId: Int) {
        disposables.add(DBObserverHelper.resetDoneQuestionsAmountToZero(db, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(updateItems: Int) {
                if (updateItems == 1) {
                    view.startPracticeCardsActivity(courseId)
                }
                view.setPBVisibility(false)
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "startLearningCourseAgain() resetDoneQuestionsAmountToZero error : ${e.message}")
                view.setPBVisibility(false)
            }
        }, courseId))
    }

    private fun addNewListToMainCourses(courses: List<CourseDo>) =
            with(mainCoursesList) {
                if (isEmpty()) {
                    addAll(courses)
                } else {
                    clear()
                    addAll(courses)
                }
                mainCoursesList
            }
}