package mr.kostua.learningpro.mainPage

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_main_page.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.injections.scopes.FragmentScope
import mr.kostua.learningpro.mainPage.executionService.NewCourseCreationService
import mr.kostua.learningpro.tools.FragmentInitializer
import mr.kostua.learningpro.tools.ConstantValues
import javax.inject.Inject


/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
@FragmentScope
class MainPageFragment @Inject constructor() : FragmentInitializer<MainPageContract.Presenter>(),
        MainPageContract.View, View.OnClickListener {
    private val TAG = this.javaClass.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun initializeViews() {
        presenter.takeView(this)
        bCreateNewCourse.setOnClickListener(this)
        bOpenCurrentCourse.setOnClickListener(this)
        bManageReminders.setOnClickListener(this)

    }

    private fun createNewCourseClickListener(view: View) {
        startActivityForResult(Intent(Intent.ACTION_GET_CONTENT)
                .setType("text/plain").addCategory(Intent.CATEGORY_OPENABLE),
                ConstantValues.PICK_FILE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                presenter.processData(data.data)
                //TODO show some progress bar on left of the button create new (and block this button until the end of the execution)
            } else {
                TODO("show some message")
            }
        } else {
            TODO("show some message")

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bCreateNewCourse -> {
            }
            R.id.bOpenCurrentCourse -> {
            }
            R.id.bManageReminders -> {
            }
        }
    }

    override fun startNewCourseCreationService(data: Uri) {
        val intent = Intent(fragmentContext, NewCourseCreationService::class.java)
                .putExtra(ConstantValues.NEW_COURSE_URI_KEY, data.toString())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fragmentContext.startForegroundService(intent)
        } else {
            fragmentContext.startService(intent)
        }
    }

}