package mr.kostua.learningpro.allCoursesPage

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import mr.kostua.learningpro.R

/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
class AllCoursesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_courses, container, false)
    }
}