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

    override fun takeView(view: MainPageContract.View) {
        this.view = view
    }

    override fun processData(data: Uri, course: CourseDo) {
        disposables.add(db.addCourseToLocalDB(course)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Long>() {
                    override fun onSuccess(courseId: Long) {
                        if (courseId != -1L) {
                            view.showMessageCourseCreatedSuccessfully(course.title)
                            view.startNewCourseCreationService(data, courseId.toInt())
                        } else {
                            view.setBlockCreateButton(false)
                            view.showDialogCourseCreationFailed(course.title, data)
                        }
                    }

                    override fun onError(e: Throwable) {
                        view.setBlockCreateButton(false)
                        view.showDialogCourseCreationFailed(course.title, data)
                    }
                }))

    }

    override fun disposeAll() {
        disposables.clear()
    }

    override fun saveCourseInSP(courseDo: CourseDo, fileUri: Uri) {
        with(db) {
            saveCreatingCourseDescription(courseDo.description)
            saveCreatingCourseTitle(courseDo.title)
            saveCreatingCourseUri(fileUri.toString())
        }
    }

    override fun isNotCreatedCourseDataExists() = db.isCreatingCourseDataExists()

    override fun getSavedCourse() =
            CourseDo(title = db.getCreatingCourseTitle(), description = db.getCreatingCourseDescription())


    override fun isCourseDataSavedForThisFile(fileUri: Uri): Boolean {
        val fileStringUri = db.getCreatingCourseStringUri()
        val savedUri = if (fileStringUri.isNotEmpty()) Uri.parse(fileStringUri) else null
        return savedUri != null && savedUri == fileUri
    }
}