package mr.kostua.learningpro.main

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.app.NavUtils
import android.support.v4.view.ViewPager
import android.view.Menu
import android.view.MenuItem
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.allCoursesPage.AllCoursesFragment
import mr.kostua.learningpro.mainPage.MainPageFragment

class MainActivity : DaggerAppCompatActivity(), TabLayout.OnTabSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeViewPager(viewPager)

        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(this)
    }

    override fun onPause() {
        super.onPause()
        tabLayout.removeOnTabSelectedListener(this)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
       // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
     //   TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun initializeViewPager(pager: ViewPager) {
        CustomViewPagerAdapter(supportFragmentManager).apply {
            addFragment(MainPageFragment(), "Main")
            addFragment(AllCoursesFragment(), "All Courses")
            pager.adapter = this
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            //TODO open preference activity
            true
        }
        R.id.home -> { //in case this app provide some intent filters for other apps (check navigate up with new back stack)
            NavUtils.navigateUpFromSameTask(this)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private class CustomViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
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
