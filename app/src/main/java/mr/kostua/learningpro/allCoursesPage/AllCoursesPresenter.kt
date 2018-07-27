package mr.kostua.learningpro.allCoursesPage

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.DisposableSubscriber
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.CourseDo
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
                        view.setPBVisibility(false)
                        if (view.isCourseListInitialized()) {
                            view.updateCourseList(addNewListToMainCourses(coursesList))
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