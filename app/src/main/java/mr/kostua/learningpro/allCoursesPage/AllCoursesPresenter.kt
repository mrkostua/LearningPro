package mr.kostua.learningpro.allCoursesPage

import mr.kostua.learningpro.data.DBHelper
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
}