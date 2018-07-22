package mr.kostua.learningpro.mainPage

import android.net.Uri
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.CourseDo
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
class MainPagePresenter @Inject constructor(private val db: DBHelper) : MainPageContract.Presenter {
    private val TAG = this.javaClass.simpleName
    override lateinit var view: MainPageContract.View
    private val disposables = CompositeDisposable()
    override fun start() {

    }

    override fun takeView(view: MainPageContract.View) {
        this.view = view
    }


    override fun processData(data: Uri, course: CourseDo) {
        disposables.add(db.addCourseToLocalDB(course)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Long>() {
                    override fun onSuccess(courseId: Long) {
                        if (courseId == -1L) {
                            TODO("show some message insertion failed")
                        } else {
                            view.startNewCourseCreationService(data, courseId.toInt())
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.setBlockCreateButton(false)
                        TODO("Show some message etc......") //To change body of created functions use File | Settings | File Templates.
                    }

                }))

    }


    override fun disposeAll() {
        disposables.clear()
    }
}