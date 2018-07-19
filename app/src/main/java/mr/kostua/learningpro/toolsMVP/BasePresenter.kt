package mr.kostua.learningpro.toolsMVP

import mr.kostua.learningpro.allCoursesPage.AllCoursesContract

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
interface BasePresenter<T> {
    var view: T

    fun start()
    fun takeView(view: T)
}