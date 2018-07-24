package mr.kostua.learningpro.mainPage.executionService

import android.content.Intent
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.support.v4.content.LocalBroadcastManager
import dagger.android.DaggerIntentService
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.injections.scopes.ServiceScope
import mr.kostua.learningpro.tools.ConstantValues
import mr.kostua.learningpro.tools.NotificationTools
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
    public lateinit var dbHelper: DBHelper
    @Inject
    public lateinit var notificationTools: NotificationTools

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val uriData = Uri.parse(intent.getStringExtra(ConstantValues.NEW_COURSE_URI_KEY))
            val courseId = intent.getIntExtra(ConstantValues.NEW_COURSE_ID_KEY, ConstantValues.WRONG_TABLE_ID)
            val courseTitle = intent.getStringExtra(ConstantValues.NEW_COURSE_TITLE_KEY) ?: "Course"
            if (uriData != null && courseId != ConstantValues.WRONG_TABLE_ID) {
                createCourseNotification = notificationTools.createNewCourseNotification(courseTitle)
                startForeground(ConstantValues.CREATE_NEW_COURSE_NOTIFICATION_ID, createCourseNotification.build())
                createNewCourse(uriData, courseId)

            } else {
                updateTaskFailedUI()

            }
        } else {
            //if the service was restarted intent can be null
            updateTaskFailedUI()

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
                if (linesCount % 10 == 0 || linesCount == index) {
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
        updateTaskCompleteUI()
    }

    private fun updateCourseQuestionsCount(courseId: Int, questionsAmount: Int) {
        if (!dbHelper.updateCourse(courseId, questionsAmount)) {
            //TODO handel case when course table wasn't updated ?
        }
    }

    private fun updateTaskCompleteUI() {
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
            dbHelper.addQuestionToLocalDB(questionDo) //todo read how to handle insert to db errors
        }

    }

    private fun isLineQuestion(line: String): Boolean {
        if (line.isNotEmpty() && line[0].isUpperCase()) {
            if (line.endsWith("?")) {
                return true
            }
        }
        return false
    }

}