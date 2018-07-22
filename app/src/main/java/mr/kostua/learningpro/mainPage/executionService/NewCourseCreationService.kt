package mr.kostua.learningpro.mainPage.executionService

import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.content.LocalBroadcastManager
import dagger.android.DaggerIntentService
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.injections.scopes.ServiceScope
import mr.kostua.learningpro.tools.ConstantValues
import mr.kostua.learningpro.tools.NotificationTools
import mr.kostua.learningpro.tools.ShowLogs
import java.io.*
import javax.inject.Inject


/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */

//TODO create handler to sent actions to Activity as (unblock create button) and update AllCoursesFragment List!!!!!

@ServiceScope
class NewCourseCreationService @Inject constructor() : DaggerIntentService("NewCourseCreationServiceThread") {
    private val TAG = this.javaClass.simpleName
    @Inject
    public lateinit var dbHelper: DBHelper
    @Inject
    public lateinit var notificationTools: NotificationTools

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val uriData = Uri.parse(intent.getStringExtra(ConstantValues.NEW_COURSE_URI_KEY))
            val courseId = intent.getIntExtra(ConstantValues.NEW_COURSE_ID_KEY, ConstantValues.WRONG_TABLE_ID)
            if (uriData != null && courseId != ConstantValues.WRONG_TABLE_ID) {
                startForeground(ConstantValues.CREATE_NEW_COURSE_NOTIFICATION_ID, notificationTools.createNewCourseNotification("creating course..."))
                createNewCourse(uriData, courseId)

            } else {
                showFailedMessage()

            }
        } else {
            showFailedMessage()

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationTools.cancelNotification(ConstantValues.CREATE_NEW_COURSE_NOTIFICATION_ID)
    }

    //TODO in future update method to be able handle big answer to questions bigger than 8000 characters (As SQL can save in one varchar type)
    //TODO no network error during accessing file from GDrive, crash
    private fun createNewCourse(data: Uri, courseId: Int) {
        var question = ""
        val answer = StringBuffer()
        val otherText = StringBuffer()
        var questionsAmount = 0
        val linesCount = getLinesCount(this.contentResolver.openInputStream(data)) - 1

        BufferedReader(InputStreamReader(this.contentResolver.openInputStream(data),
                "UTF-8")).useLines {
            it.forEachIndexed { index, line ->
                notificationTools.updateNewCourseNotificationProgress(linesCount, index)
                if (isLineQuestion(line)) {
                    if (question.isNotEmpty()) {
                        saveTaskInDB(QuestionDo(question = question, answer = answer.toString(), courseId = courseId))
                        answer.delete(0, answer.length)
                        ++questionsAmount
                    }
                    question = line

                } else {
                    if (question.isEmpty()) {
                        otherText.appendln(line)
                    } else {
                        answer.appendln(line)
                    }

                }
            }
        }
        if (question.isNotEmpty()) {
            saveTaskInDB(QuestionDo(question = question, answer = answer.toString(), courseId = courseId))
            ++questionsAmount
        }
        if (otherText.isNotEmpty()) {
            saveTaskInDB(QuestionDo(question = "", answer = otherText.toString(), courseId = courseId))
            ++questionsAmount
        }
        updateCourseQuestionsCount(courseId, questionsAmount)
        updateUI()
    }

    private fun updateCourseQuestionsCount(courseId: Int, questionsAmount: Int) {
        if (!dbHelper.updateCourse(courseId, questionsAmount)) {
            //TODO handel case when course table wasn't updated ?
        }
    }

    private fun updateUI() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent("bla"))
        ShowLogs.log(TAG,"updateUI message sent bla bla :) ")

    }


    private fun getLinesCount(inputStream: InputStream): Int {
        val lineNumberReader = LineNumberReader(InputStreamReader(inputStream, "UTF-8"))
        lineNumberReader.skip(Long.MAX_VALUE)
        val linesCount = lineNumberReader.lineNumber
        lineNumberReader.close()
        return linesCount
    }

    private fun saveTaskInDB(questionDo: QuestionDo) {
        dbHelper.addQuestionToLocalDB(questionDo)
    }

    private fun isLineQuestion(line: String): Boolean {
        if (line.isNotEmpty() && line[0].isUpperCase()) {
            if (line.endsWith("?")) {
                return true
            }
        }
        return false
    }


    private fun showFailedMessage() {
        TODO("not implemented")
    }
}