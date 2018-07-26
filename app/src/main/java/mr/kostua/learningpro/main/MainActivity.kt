package mr.kostua.learningpro.main

import android.content.Intent
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
import mr.kostua.learningpro.appSettings.SettingPreferencesActivity
import mr.kostua.learningpro.mainPage.MainPageFragment

class MainActivity : DaggerAppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initializeViewPager(viewPager)
        tabLayout.setupWithViewPager(viewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab) {
                if (tab.text == getString(R.string.tabMainName)) {
                    appBar.setExpanded(true, true)
                }
            }
        })
    }

    private fun initializeViewPager(pager: ViewPager) {
        CustomViewPagerAdapter(supportFragmentManager).apply {
            addFragment(MainPageFragment(), getString(R.string.tabMainName))
            addFragment(AllCoursesFragment(), getString(R.string.tabAllCoursesName))
            pager.adapter = this
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_settings -> {
            startActivity(Intent(this, SettingPreferencesActivity::class.java))
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
