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
        fun showDialogCourseCreationFailed(courseName: String, fileUri: Uri)
        fun startLastOpenedCourse(course: CourseDo)
        fun showToast(message: String)
    }

    interface Presenter : BasePresenter<View> {
        fun processData(data: Uri, course: CourseDo)
        fun disposeAll()
        fun saveCourseInSP(courseDo: CourseDo, fileUri: Uri)
        fun isNotCreatedCourseDataExists(): Boolean
        fun getSavedCourse(): CourseDo
        fun isCourseDataSavedForThisFile(fileUri: Uri): Boolean
        fun startLastOpenedCourse()

    }

}