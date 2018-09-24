package mr.kostua.learningpro.allCoursesPage

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

    override fun takeView(view: AllCoursesContract.View) {
        this.view = view
    }

    override fun populateCourses() {
        disposables.add(db.getAllCourses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<List<CourseDo>>() {
                    override fun onComplete() {}

                    override fun onNext(coursesList: List<CourseDo>) {
                        ShowLogs.log(TAG, "populateCourses")
                        view.setPBVisibility(false)
                        if (view.isCourseListInitialized()) {
                            addNewListToMainCourses(coursesList)
                            view.updateCourseList()
                        } else {
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
            override fun onSuccess(updatedItemsAmount: Int) {
                if (updatedItemsAmount > 0) {
                    disposables.add(DBObserverHelper.setCourseDoneQuestionsAmount(db, object : DisposableSingleObserver<Int>() {
                        override fun onSuccess(updateItems: Int) {
                            if (updateItems == 1) {
                                view.startPracticeCardsActivity(courseId)
                            }
                            view.setPBVisibility(false)

                        }

                        override fun onError(e: Throwable) {
                            ShowLogs.log(TAG, "startLearningCourseAgain() setCourseDoneQuestionsAmount error : ${e.message}")
                            view.setPBVisibility(false)
                        }

                    }, courseId))
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

    override fun disposeAll() {
        disposables.clear()
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