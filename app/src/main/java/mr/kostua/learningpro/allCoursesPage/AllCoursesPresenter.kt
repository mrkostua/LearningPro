package mr.kostua.learningpro.allCoursesPage

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.tools.ShowLogs
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
class AllCoursesPresenter @Inject constructor(private val db: DBHelper) : AllCoursesContract.Presenter {
    private val TAG = this.javaClass.simpleName
    override lateinit var view: AllCoursesContract.View
    private val disposables = CompositeDisposable()

    override fun takeView(view: AllCoursesContract.View) {
        this.view = view
    }

    override fun populateCourses() {
        disposables.add(db.getAllCourses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSubscriber<List<CourseDo>>() {
                    override fun onComplete() {
                        ShowLogs.log(TAG, "populateCourses onComplete")
                    }

                    override fun onNext(coursesList: List<CourseDo>) {
                        if (view.isCourseListInitialized()) {
                            view.updateCourseList(coursesList)
                            ShowLogs.log(TAG, "populateCourses onNext updateCourseList")

                        } else {
                            view.initializeRecycleView(coursesList)
                            ShowLogs.log(TAG, "populateCourses onNext initializeRecycleView")

                        }

                    }

                    override fun onError(t: Throwable) {
                        ShowLogs.log(TAG, "populateCourses onError ${t.message} ")
                    }

                }
                ))
    }

    override fun disposeAll() {
        disposables.clear()
    }
}