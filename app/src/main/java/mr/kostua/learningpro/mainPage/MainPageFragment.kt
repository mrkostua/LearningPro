package mr.kostua.learningpro.mainPage

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.appcompat.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.custom_view_create_course_dialog.view.*
import kotlinx.android.synthetic.main.fragment_main_page.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.injections.scopes.FragmentScope
import mr.kostua.learningpro.mainPage.executionService.NewCourseCreationService
import mr.kostua.learningpro.practiceCards.PracticeCardsActivity
import mr.kostua.learningpro.questionsCardPreview.QuestionsCardsPreviewActivity
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
    lateinit var notificationTools: NotificationTools

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

    override fun setBlockCreateButton(isBlocked: Boolean) {
        with(bCreateNewCourse) {
            isClickable = !isBlocked
            isFocusable = !isBlocked
        }
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
                bCreateNewCourse.postDelayed({
                    startActivityToChooseFile()
                }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
            }
            R.id.bOpenCurrentCourse -> {
                bOpenCurrentCourse.postDelayed({
                    presenter.startLastOpenedCourse()
                }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
            }
            R.id.bManageReminders -> {
            }
        }
    }

    override fun startLastOpenedCourse(course: CourseDo) {
        if (course.reviewed) {
            startActivity(Intent(fragmentContext, PracticeCardsActivity::class.java)
                    .putExtra(ConstantValues.COURSE_ID_KEY, course.id!!))
        } else {
            startActivity(Intent(fragmentContext, QuestionsCardsPreviewActivity::class.java)
                    .putExtra(ConstantValues.COURSE_ID_KEY, course.id!!))
        }
    }

    override fun showToast(message: String) {
        notificationTools.showToastMessage(message)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (data?.data != null) {
                showCreateCourseDialog(data.data!!)
            } else {
                showFailedToChooseFileMessage()
            }
        } else {
            showFailedToChooseFileMessage()
        }
    }

    private fun showFailedToChooseFileMessage() {
        notificationTools.showToastMessage(getString(R.string.failedToChooseFileMessage))
    }

    override fun showMessageCourseCreatedSuccessfully(courseName: String) {
        notificationTools.showToastMessage("course \"$courseName\" was created successfully")
    }

    override fun showDialogCourseCreationFailed(courseName: String, fileUri: Uri) {
        notificationTools.showCustomAlertDialog(parentActivity,
                "Course \"$courseName\" creation failed",
                "Do you want to try again with same data?",
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            showCreateCourseDialog(fileUri)
                            dialog.dismiss()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                })
    }

    private fun showCreateCourseDialog(data: Uri) {
        val customDialogView = LayoutInflater.from(fragmentContext).inflate(R.layout.custom_view_create_course_dialog,
                clBackgroundLayout, false)
        val createCourseDialog = AlertDialog.Builder(parentActivity, R.style.CustomAlertDialogStyle)
                .setView(customDialogView).create()
        createCourseDialog.setSlideWindowAnimation()
        with(customDialogView) {
            initializeCourseDataFromSP(this, data)

            tvNewCourseDialogFileName.setUnderlineText(FileTools.getFileNameFromUri(data,
                    fragmentContext.getString((R.string.create_course_dialog_default_file_name)), fragmentContext.contentResolver))
            tvNewCourseDialogFileName.setOnClickListener {
                createCourseDialog.dismiss()
                startActivityToChooseFile()
            }
            bNewCourseBack.setOnClickListener {
                bNewCourseBack.postDelayed({
                    saveCourseInSP(this, data)
                    createCourseDialog.dismiss()
                }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)

            }
            bNewCourseCreate.setOnClickListener {
                when {
                    etNewCourseDialogTitle.text.isEmpty() ->
                        notificationTools.showToastMessage("Please set the title to continue")
                    tvNewCourseDialogFileName.text == resources.getString(R.string.create_course_dialog_default_file_name) ->
                        notificationTools.showToastMessage("Please choose the file with questions before continuing")
                    else -> {
                        setBlockCreateButton(true)
                        bNewCourseCreate.postDelayed({
                            presenter.processData(data, CourseDo(title = etNewCourseDialogTitle.text.toString(),
                                    description = etNewCourseDialogDescription.text.toString()))
                            createCourseDialog.dismiss()
                        }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
                    }
                }
            }
            createCourseDialog.setOnDismissListener {
                saveCourseInSP(this, data)
            }
        }
        createCourseDialog.show()
    }

    private fun initializeCourseDataFromSP(dialogView: View, chosenFileUri: Uri) {
        val savedCourse = presenter.getSavedCourse()
        if (presenter.isCourseDataSavedForThisFile(chosenFileUri)) {
            dialogView.etNewCourseDialogTitle.setText(savedCourse.title)
            dialogView.etNewCourseDialogDescription.setText(savedCourse.title)
        }
    }

    private fun saveCourseInSP(dialogView: View, fileUri: Uri) {
        with(dialogView) {
            if (isCreatingCourseDataEmpty(this) && !isCreatingCourseServiceStarted()) {
                presenter.saveCourseInSP(CourseDo(title = etNewCourseDialogTitle.text.toString(),
                        description = etNewCourseDialogDescription.text.toString()), fileUri)
            }
        }
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