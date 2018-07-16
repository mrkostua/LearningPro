package mr.kostua.learningpro.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.allCoursesPage.AllCoursesFragment
import mr.kostua.learningpro.mainPage.MainPageFragment

class MainActivity : DaggerAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun initializeViewPager(pager: ViewPager) {
        CustomViewPagerAdapter(supportFragmentManager).apply {
            addFragment(MainPageFragment(), "Main")
            addFragment(AllCoursesFragment(), "All Courses")
            pager.adapter = this
        }
    }

    class CustomViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val fragmentsList = ArrayList<Pair<Fragment, String>>(2)
        override fun getItem(position: Int): Fragment {
            return fragmentsList[position].first
        }

        override fun getCount(): Int {
            return fragmentsList.size
        }

        fun addFragment(fragment: Fragment, name: String) {
            fragmentsList.add(fragment to name)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentsList[position].second
        }
    }
}
