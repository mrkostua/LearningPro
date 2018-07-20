package mr.kostua.learningpro.tools

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import dagger.android.support.DaggerFragment
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/18/2018.
 */
abstract class FragmentInitializer<T : Any> : DaggerFragment() {
    abstract fun initializeViews()
    lateinit var fragmentContext: Context
    lateinit var parentActivity: Activity
    @Inject
    public lateinit var presenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentContext = activity!!.applicationContext
        parentActivity = activity!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }
}