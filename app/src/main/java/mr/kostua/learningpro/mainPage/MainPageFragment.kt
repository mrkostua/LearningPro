package mr.kostua.learningpro.mainPage

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.custom_view_create_course_dialog.view.*
import kotlinx.android.synthetic.main.fragment_main_page.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.injections.scopes.FragmentScope
import mr.kostua.learningpro.mainPage.executionService.NewCourseCreationService
import mr.kostua.learningpro.tools.*
import java.lang.ref.WeakReference
import javax.inject.Inject


/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
@FragmentScope
class MainPageFragment @Inject constructor() : FragmentInitializer<MainPageContract.Presenter>(),
        MainPageContract.View, View.OnClickListener {
    private val TAG = this.javaClass.simpleName
    private val reciver  = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            ShowLogs.log(TAG, "onReceive()")
            //TODO REFACTOR
            setBlockCreateButton(false)

        }
    }

    @Inject
    public lateinit var notificationTools: NotificationTools

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun initializeViews() {
        presenter.takeView(this)
        LocalBroadcastManager.getInstance(fragmentContext).registerReceiver(reciver, IntentFilter("bla"))

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
                displayCreateNewCourseDialog(data.data)
                //TODO show some progress bar on left of the button create new (and block this button until the end of the execution)
            } else {
                TODO("show some message")
            }
        } else {
            TODO("show some message")

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    //TODO think how to make background fo dialog with round corners, also how to make it more perfect
    private fun displayCreateNewCourseDialog(data: Uri) {
        //TODO read about passing null as root argument inflate()
        val customDialogView = LayoutInflater.from(fragmentContext).inflate(R.layout.custom_view_create_course_dialog, null, false)

        //TODO maybe also if user decide to go back save title,etc in SharedPreferences so after by opening this dialog again previously typed data will be put into et views
        val createCourseDialog = AlertDialog.Builder(parentActivity, R.style.CustomAlertDialogStyle)
                .setView(customDialogView).create()

        with(customDialogView) {
            tvNewCourseDialogFileName.setUnderlineText(FileTools.getFileNameFromUri(data,
                    fragmentContext.getString((R.string.create_course_dialog_default_file_name)), fragmentContext.contentResolver))
            tvNewCourseDialogFileName.setOnClickListener {
                //TODO go to file selection and dismiss so new dialog can be created but with previous data from SharedPreferences
            }

            bNewCourseBack.setOnClickListener {
                createCourseDialog.dismiss()
            }
            bNewCourseCreate.setOnClickListener {
                when {
                    etNewCourseDialogTitle.text.isEmpty() -> {
                        notificationTools.showToastMessage("Please set the title to continue")
                    }
                    tvNewCourseDialogFileName.text == resources.getString(R.string.create_course_dialog_default_file_name) -> {
                        notificationTools.showToastMessage("Please choose the file with questions before continuing")
                    }
                    else -> {
                        presenter.processData(data, CourseDo(title = etNewCourseDialogTitle.text.toString(),
                                description = etNewCourseDialogDescription.text.toString()))

                        setBlockCreateButton(true)
                        createCourseDialog.dismiss()
                    }
                }

            }
        }


        createCourseDialog.show()
    }

    override fun setBlockCreateButton(isBlocked: Boolean) {
        with(bCreateNewCourse) {
            isClickable = !isBlocked
            isFocusable = !isBlocked
        }
        ShowLogs.log(TAG, "setBlock : $isBlocked")
        if (isBlocked) {
            pbCreateNewCourse.visibility = View.VISIBLE
            bCreateNewCourse.setBackgroundResource(R.drawable.blocked_button)
        } else {
            pbCreateNewCourse.visibility = View.GONE
            bCreateNewCourse.setBackgroundResource(R.drawable.button_selector)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.bCreateNewCourse -> {
                createNewCourseClickListener(v)
            }
            R.id.bOpenCurrentCourse -> {
            }
            R.id.bManageReminders -> {
            }
        }
    }

    override fun startNewCourseCreationService(data: Uri, courseId: Int) {
        val intent = Intent(fragmentContext, NewCourseCreationService::class.java)
                .putExtra(ConstantValues.NEW_COURSE_URI_KEY, data.toString())
                .putExtra(ConstantValues.NEW_COURSE_ID_KEY, courseId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fragmentContext.startForegroundService(intent)
        } else {
            fragmentContext.startService(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAll()
        LocalBroadcastManager.getInstance(fragmentContext).unregisterReceiver(reciver)
    }

}