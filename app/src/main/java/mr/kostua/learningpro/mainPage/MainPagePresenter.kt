package mr.kostua.learningpro.mainPage

import android.content.ContentResolver
import android.net.Uri
import io.reactivex.disposables.CompositeDisposable
import mr.kostua.learningpro.tools.ShowLogs
import java.io.*
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
class MainPagePresenter @Inject constructor(private val contentResolver: ContentResolver) : MainPageContract.Presenter {
    private val TAG = this.javaClass.simpleName
    override lateinit var view: MainPageContract.View

    override fun start() {

    }

    override fun takeView(view: MainPageContract.View) {
        this.view = view
    }


    override fun processData(data: Uri) {
        view.startNewCourseCreationService(data)

    }
}