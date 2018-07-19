package mr.kostua.learningpro.allCoursesPage

import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.CourseDo
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
class AllCoursesPresenter @Inject constructor(private val db: DBHelper) : AllCoursesContract.Presenter {
    override lateinit var view: AllCoursesContract.View

    override fun start() {
    }

    override fun takeView(view: AllCoursesContract.View) {
        this.view = view
    }

    override fun populateCourses() {
        val descr = "This course is for learning about computer architecture related topics, like : \nhow computer works, what is inside of it, more deep look into computer architecture" +
                "This course is for learning about computer architecture related topics, like : \nhow computer works, what is inside of it, more deep look into computer architecture"
        val list = ArrayList<CourseDo>()
        (1 until 10).forEach {
            list.add(CourseDo(title = "Egzamin#$it", description = "$descr$it", questionsAmount = 1000 / it, doneQuestionsAmount = 50 * it))
        }
        view.initializeRecycleView(list)
    }


}