package mr.kostua.learningpro.allCoursesPage

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.CourseDo
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
class AllCoursesPresenter @Inject constructor(private val db: DBHelper) : AllCoursesContract.Presenter {
    override lateinit var view: AllCoursesContract.View
    private val disposables = CompositeDisposable()

    override fun start() {
    }

    override fun takeView(view: AllCoursesContract.View) {
        this.view = view
    }

    override fun populateCourses() {
        disposables.add(db.getAllCourses()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<CourseDo>>() {
                    override fun onSuccess(coursesList: List<CourseDo>) {
                        view.initializeRecycleView(coursesList)

                    }

                    override fun onError(e: Throwable) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                }))


    }

    override fun disposeAll() {
        disposables.clear()
    }
}