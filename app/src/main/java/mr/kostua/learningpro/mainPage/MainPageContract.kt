package mr.kostua.learningpro.mainPage

import android.net.Uri
import mr.kostua.learningpro.toolsMVP.BasePresenter
import mr.kostua.learningpro.toolsMVP.BaseView

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
interface MainPageContract {
    interface View : BaseView {
        fun startNewCourseCreationService(data: Uri)
    }

    interface Presenter : BasePresenter<View> {
        fun processData(data: Uri)
    }

}