package mr.kostua.learningpro.allCoursesPage

import io.reactivex.Observable
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.toolsMVP.BasePresenter
import mr.kostua.learningpro.toolsMVP.BaseView

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
interface AllCoursesContract {
    interface View : BaseView {
        fun initializeRecycleView(data: List<CourseDo>)
        fun setPBVisibility(visible: Boolean)
        fun updateCourseList()
        fun isCourseListInitialized(): Boolean
        fun showFailedPopulationDialog()
        fun startPracticeCardsActivity(courseId: Int)
        fun createCourseFinishedDialog(courseId: Int)
        fun startPreviewActivity(courseId: Int)
        fun showToast(message: String)
        fun dismissFinishedCourseDialog()
        fun startActivityToShowAllCard(courseId: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun populateCourses()
        fun startLearningCourseAgain(courseId: Int)
        fun disposeAll()
        fun saveLastOpenedCourseId(courseId: Int)
        fun subscribeCourseItemClick(observable: Observable<CourseDo>)
        fun dialogButtonPracticeCourseAgainClickListener()
        fun dialogButtonShowAllCardsClickListener()
        fun dialogButtonDo(courseId: Int)
    }
}