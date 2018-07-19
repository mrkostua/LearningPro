package mr.kostua.learningpro.tools

import android.view.View

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
interface ViewHolderBinder<D> {
    fun initializeViews(view: View)
    fun bind(item: D)
}