package mr.kostua.learningpro.toolsMVP

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
interface BasePresenter<T> {
    var view: T
    fun takeView(view: T)
}