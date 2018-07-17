package mr.kostua.learningpro.mainPage.executionService

import android.content.Intent
import android.net.Uri
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
    @Inject
    public lateinit var dbHelper: DBHelper
    @Inject
    public lateinit var notificationTools: NotificationTools

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val uriData = Uri.parse(intent.getStringExtra(ConstantValues.NEW_COURSE_URI_KEY))
            if (uriData != null) {
                startForeground(ConstantValues.CREATE_NEW_COURSE_NOTIFICATION_ID, notificationTools.createNewCourseNotification("creating course..."))
                createNewCourse(uriData)

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
    private fun createNewCourse(data: Uri) {
        var question = ""
        val task = StringBuffer()
        val otherText = StringBuffer()
        val linesCount = getLinesCount(this.contentResolver.openInputStream(data)) - 1

        BufferedReader(InputStreamReader(this.contentResolver.openInputStream(data),
                "UTF-8")).useLines {
            it.forEachIndexed { index, line ->
                notificationTools.updateNewCourseNotificationProgress(linesCount, index)
                if (isLineQuestion(line)) {
                    if (question.isNotEmpty()) {
                        saveTaskInDB(question, task.toString())
                        task.delete(0, task.length)
                    }
                    question = line

                } else {
                    if (question.isEmpty()) {
                        otherText.appendln(line)
                    } else {
                        task.appendln(line)
                    }

                }
            }
        }
        if (question.isNotEmpty()) {
            saveTaskInDB(question, task.toString())
        }

        saveTaskInDB("Create question for this text or delete?", otherText.toString())
    }

    private fun getLinesCount(inputStream: InputStream): Int {
        val lineNumberReader = LineNumberReader(InputStreamReader(inputStream, "UTF-8"))
        lineNumberReader.skip(Long.MAX_VALUE)
        val linesCount = lineNumberReader.lineNumber
        lineNumberReader.close()
        return linesCount
    }

    private fun saveTaskInDB(question: String, task: String) {
        dbHelper.addQuestionToLocalDB(QuestionDo(question = question, answer = task))
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