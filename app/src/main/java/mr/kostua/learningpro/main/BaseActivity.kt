package mr.kostua.learningpro.main

import android.os.Bundle
import androidx.core.app.NavUtils
import android.view.MenuItem
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.base_toolbar.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.tools.ShowLogs

/**
 * @author Kostiantyn Prysiazhnyi on 7/27/2018.
 */
abstract class BaseDaggerActivity : DaggerAppCompatActivity() {
    private val TAG = this.javaClass.simpleName
    protected fun onCreate(savedInstanceState: Bundle?, layoutId: Int) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        setSupportActionBar(toolbar)
        enableHomeButton(true)

    }

    protected fun enableHomeButton(enable : Boolean){
        supportActionBar?.setDisplayHomeAsUpEnabled(enable)
        supportActionBar?.setDisplayShowHomeEnabled(enable)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> { //in case this app provide some intent filters for other apps (check navigate up with new back stack)
            NavUtils.navigateUpFromSameTask(this)
            ShowLogs.log(TAG, "onOptionsItemSelected home")
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}