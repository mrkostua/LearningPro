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
        fun updateCourseList(courses: List<CourseDo>)
        fun isCourseListInitialized(): Boolean
    }

    interface Presenter : BasePresenter<View> {
        fun populateCourses()
        fun disposeAll()

    }
}