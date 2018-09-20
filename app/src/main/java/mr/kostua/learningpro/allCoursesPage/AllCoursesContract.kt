package mr.kostua.learningpro.allCoursesPage

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
        fun startPracticeCardsActivity(courseId : Int)
    }

    interface Presenter : BasePresenter<View> {
        fun populateCourses()
        fun startLearningCourseAgain(courseId : Int)
        fun disposeAll()

    }
}