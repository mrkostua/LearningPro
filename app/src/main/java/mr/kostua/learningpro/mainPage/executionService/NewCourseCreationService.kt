package mr.kostua.learningpro.mainPage.executionService

import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import dagger.android.DaggerIntentService
import mr.kostua.learningpro.R
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

@ServiceScope
class NewCourseCreationService @Inject constructor() : DaggerIntentService("NewCourseCreationServiceThread") {
    private val TAG = this.javaClass.simpleName
    private lateinit var createCourseNotification: NotificationCompat.Builder
    @Inject
    lateinit var dbHelper: DBHelper
    @Inject
    lateinit var notificationTools: NotificationTools

    private lateinit var courseTitle: String
    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val uriData = Uri.parse(intent.getStringExtra(ConstantValues.NEW_COURSE_URI_KEY))
            val courseId = intent.getIntExtra(ConstantValues.NEW_COURSE_ID_KEY, ConstantValues.WRONG_TABLE_ID)
            courseTitle = intent.getStringExtra(ConstantValues.NEW_COURSE_TITLE_KEY)
                    ?: getString(R.string.course)
            if (uriData != null && courseId != ConstantValues.WRONG_TABLE_ID) {
                createCourseNotification = notificationTools.createNewCourseNotification(courseTitle)
                startForeground(ConstantValues.CREATE_NEW_COURSE_NOTIFICATION_ID, createCourseNotification.build())
                createNewCourse(uriData, courseId)

            } else {
                updateTaskFailedUI()
            }
        } else {
            updateTaskFailedUI()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        notificationTools.cancelNotification(ConstantValues.CREATE_NEW_COURSE_NOTIFICATION_ID)
    }

    private fun createNewCourse(data: Uri, courseId: Int) {
        var question = ""
        val answer = StringBuffer()
        val otherText = StringBuffer()
        var questionsAmount = 0
        val linesCount = getLinesCount(this.contentResolver.openInputStream(data)) - 1
        ShowLogs.log(TAG, "lineCounts is $linesCount")
        BufferedReader(InputStreamReader(this.contentResolver.openInputStream(data),
                "UTF-8")).useLines {
            it.forEachIndexed { index, line ->
                if (index % 10 == 0 || linesCount == index) {
                    ShowLogs.log(TAG, "update notification : $index")

                    notificationTools.updateNewCourseNotificationProgress(createCourseNotification, linesCount, index)
                }
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
        updateTaskCompleteUI(courseId)
    }

    private fun isLineQuestion(line: String): Boolean {
        if (line.isNotEmpty() && line[0].isUpperCase()) {
            if (line.endsWith("?")) {
                return true
            }
        }
        return false
    }

    private fun updateCourseQuestionsCount(courseId: Int, questionsAmount: Int) {
        dbHelper.updateCourseQuestionsAmount(courseId, questionsAmount)
    }

    private fun updateTaskCompleteUI(courseId: Int) {
        notificationTools.cancelNotification(ConstantValues.CREATE_NEW_COURSE_NOTIFICATION_ID)
        notificationTools.displaySavedCourseNotification(courseTitle, courseId)

        LocalBroadcastManager.getInstance(this).sendBroadcast(
                Intent(ConstantValues.INTENT_FILTER_NEW_COURSE_CREATION_SERVICE)
                        .putExtra(ConstantValues.INTENT_KEY_IS_B_CREATE_BLOCKED, true))

    }

    private fun updateTaskFailedUI() {
        LocalBroadcastManager.getInstance(this).sendBroadcast(
                Intent(ConstantValues.INTENT_FILTER_NEW_COURSE_CREATION_SERVICE)
                        .putExtra(ConstantValues.INTENT_KEY_COURSE_CREATION_FAILED, true))
    }

    private fun getLinesCount(inputStream: InputStream): Int {
        val lineNumberReader = LineNumberReader(InputStreamReader(inputStream, "UTF-8"))
        lineNumberReader.skip(Long.MAX_VALUE)
        val linesCount = lineNumberReader.lineNumber
        lineNumberReader.close()
        return linesCount
    }

    private fun saveTaskInDB(questionDo: QuestionDo) {
        if (!dbHelper.addQuestionToLocalDB(questionDo)) {
            dbHelper.addQuestionToLocalDB(questionDo)
        }

    }
}