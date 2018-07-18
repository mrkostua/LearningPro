package mr.kostua.learningpro.tools

import android.content.Context
import android.os.Bundle
import android.view.View
import dagger.android.support.DaggerFragment

/**
 * @author Kostiantyn Prysiazhnyi on 7/18/2018.
 */
abstract class FragmentInitializer : DaggerFragment() {
    abstract fun initializeViews()
    lateinit var fragmentContext: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentContext = activity!!.applicationContext
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }
}