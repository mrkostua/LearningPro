package mr.kostua.learningpro.toolsMVP

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
interface BasePresenter<T> {
    fun start()
    fun takeView(view: T)
}