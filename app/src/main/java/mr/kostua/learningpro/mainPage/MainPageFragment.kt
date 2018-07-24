package mr.kostua.learningpro.mainPage

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.custom_view_create_course_dialog.*
import kotlinx.android.synthetic.main.custom_view_create_course_dialog.view.*
import kotlinx.android.synthetic.main.fragment_main_page.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.injections.scopes.FragmentScope
import mr.kostua.learningpro.mainPage.executionService.NewCourseCreationService
import mr.kostua.learningpro.tools.*
import javax.inject.Inject


/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
@FragmentScope
class MainPageFragment @Inject constructor() : FragmentInitializer<MainPageContract.Presenter>(),
        MainPageContract.View, View.OnClickListener {
    private val TAG = this.javaClass.simpleName
    @Inject
    public lateinit var notificationTools: NotificationTools

    private val localBR = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.getBooleanExtra(ConstantValues.INTENT_KEY_IS_B_CREATE_BLOCKED, false)) {
                setBlockCreateButton(false)

            }
            if (intent.getBooleanExtra(ConstantValues.INTENT_KEY_COURSE_CREATION_FAILED, false)) {
                notificationTools.showToastMessage(resources.getString(R.string.failedToChooseFileMessage))

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun initializeViews() {
        presenter.takeView(this)
        LocalBroadcastManager.getInstance(fragmentContext).registerReceiver(localBR, IntentFilter(ConstantValues.INTENT_FILTER_NEW_COURSE_CREATION_SERVICE))

        bCreateNewCourse.setOnClickListener(this)
        bOpenCurrentCourse.setOnClickListener(this)
        bManageReminders.setOnClickListener(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                showCreateCourseDialog(data.data)
            } else {
                showFailedToChooseFileMessage()
            }
        } else {
            showFailedToChooseFileMessage()

        }

        super.onActivityResult(requestCode, resultCode, data)
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
                startActivityToChooseFile()
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

    private fun startActivityToChooseFile() {
        startActivityForResult(Intent(Intent.ACTION_GET_CONTENT)
                .setType("text/plain").addCategory(Intent.CATEGORY_OPENABLE),
                ConstantValues.PICK_FILE_REQUEST_CODE)
    }

    private fun showFailedToChooseFileMessage() {
        notificationTools.showToastMessage(getString(R.string.failedToChooseFileMessage))
    }

    override fun showMessageCourseCreatedSuccessfully(courseName: String) {
        notificationTools.showToastMessage("course \"$courseName\" was created successfully")

    }

    override fun showMessageCourseCreationFailed(courseName: String) {
        //TODO better to show some dialog, DO IT after creating SP helper (with b. try once more, back, open create course dialog once more)
        notificationTools.showToastMessage("course \"$courseName\" wasn't created")

    }


    //TODO think how to make background fo dialog with round corners, also how to make it more perfect
    private fun showCreateCourseDialog(data: Uri) {
        val customDialogView = LayoutInflater.from(fragmentContext).inflate(R.layout.custom_view_create_course_dialog, clBackgroundLayout, false)
        val createCourseDialog = AlertDialog.Builder(parentActivity, R.style.CustomAlertDialogStyle)
                .setView(customDialogView).create()
        val notCreatedCourseData = presenter.getNotCreatedCourseData()
        with(customDialogView) {
            if (notCreatedCourseData.second != null && notCreatedCourseData.second == data) {
                etNewCourseDialogTitle.setText(notCreatedCourseData.first.title)
                etNewCourseDialogDescription.setText(notCreatedCourseData.first.description)
            }
            tvNewCourseDialogFileName.setUnderlineText(FileTools.getFileNameFromUri(data,
                    fragmentContext.getString((R.string.create_course_dialog_default_file_name)), fragmentContext.contentResolver))
            tvNewCourseDialogFileName.setOnClickListener {
                createCourseDialog.dismiss()
                startActivityToChooseFile()
            }
            bNewCourseBack.setOnClickListener {
                createCourseDialog.dismiss()
                if (isCreatingCourseDataEmpty(this) && !isCreatingCourseServiceStarted()) {
                    presenter.saveNotCreatedCourseData(CourseDo(title = etNewCourseDialogTitle.text.toString(),
                            description = etNewCourseDialogDescription.text.toString()), data)
                }
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
            createCourseDialog.setOnDismissListener {
                //also called when clicked outside of AD
                if (isCreatingCourseDataEmpty(this) && !isCreatingCourseServiceStarted()) {
                    presenter.saveNotCreatedCourseData(CourseDo(title = etNewCourseDialogTitle.text.toString(),
                            description = etNewCourseDialogDescription.text.toString()), data)
                }
            }
        }

        createCourseDialog.show()
    }

    private fun isCreatingCourseDataEmpty(customDialogView: View) =
            customDialogView.etNewCourseDialogTitle.text.isNotEmpty() && customDialogView.etNewCourseDialogDescription.text.isNotEmpty()

    private fun isCreatingCourseServiceStarted() = pbCreateNewCourse.visibility == View.VISIBLE

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAll()
        LocalBroadcastManager.getInstance(fragmentContext).unregisterReceiver(localBR)
    }

}