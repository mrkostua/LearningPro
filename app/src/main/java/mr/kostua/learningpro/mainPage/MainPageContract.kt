package mr.kostua.learningpro.mainPage

import android.net.Uri
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.toolsMVP.BasePresenter
import mr.kostua.learningpro.toolsMVP.BaseView

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
interface MainPageContract {
    interface View : BaseView {
        fun startNewCourseCreationService(data: Uri, courseId: Int)
        fun setBlockCreateButton(isBlocked: Boolean)
        fun showMessageCourseCreatedSuccessfully(courseName: String)
        fun showMessageCourseCreationFailed(courseName: String, fileUri: Uri)
    }

    interface Presenter : BasePresenter<View> {
        fun processData(data: Uri, course: CourseDo)
        fun disposeAll()
        fun saveNotCreatedCourseData(courseDo: CourseDo, fileUri: Uri)
        fun isNotCreatedCourseDataExists(): Boolean
        fun getNotCreatedCourseData(): Pair<CourseDo, Uri?>

    }

}