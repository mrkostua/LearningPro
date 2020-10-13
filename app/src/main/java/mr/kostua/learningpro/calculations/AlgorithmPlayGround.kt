package mr.kostua.learningpro.calculations

import android.net.Uri
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.tools.ShowLogs
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.LineNumberReader

/**
 * we have a data :
 * to recognize a question and answer.
 * Question:
 * - can start from anything (for new line doesn't matter if it starts with space or any character))
 * - must end with "?" mark it is important.
 *
 *
 *
 * THE PROBLEM :
 * -the problem with my current algorithm that it's only check last character of line, when in reality it can a empty space or next line (if question is long).
 * - another problem to think about is different languages? different rules (for now only latin related languages and cyrillic)
 * TODO:
 * But what if a question is constructed from 2 lines or 3 ?
 * Try to log out how does nextLine load into String (does it have max length or it's can be indefinitely long?
 * The problem is how to determine if previous line was a part of question? (do we need to start question with uppercase?) but
 * next problem - question can be constructed of 2 sentences.
 *
 * It looks like it's impossible (without actually using AI to read and try to determine if that sentence has verbs and constructed as question)
 * to determine if any random sentence is a question with 100% chance.
 * So the only choice is to create rules applied to question construction.
 * Or to simple run algorithm with basic rules (know to everybody) and in case of failure better catch
 * not readable text (!!! + 2,3 lines from previous answer, as it can be part of a question (underline it, or draw with yellow color)).
 *
 * probably even in case question was recognized it's still possible that 1-2 top line can be a part of a question.
 *
 * A case when 2 questions are located one after another ( it must handled as one question as next line can't be an answer))
 *
 *
 * Here is a lot of if? and it's very important that user will be able to edit cards, as mistakes during converting are not inevitable.
 *
 * Maybe regular expressions or something else, how to read text?
 *
 * One solution is : question is 1 sentence long always and ends with a question mark.
 * In that case we can use OpenNLP to determine this kind of sentences in file with question coordinates.
 * And after use all of text between questions as answers.
 * -- It's hard to find bad question here it can be added as an answer to previous question. But if all the rules are followed it must be ok.
 *
 *
 */

fun main() {

}

//Old algorithm code
private val TAG = "Algorithm"

private fun createNewCourse(data: Uri, courseId: Int) {
    var question = ""
    val answer = StringBuffer()
    val otherText = StringBuffer()
    var questionsAmount = 0
    //val inputStream = contentResolver.openInputStream(data)?.let { it } ?: return
    val linesCount = getLinesCount() - 1
    ShowLogs.log(TAG, "lineCounts is $linesCount")
    BufferedReader(InputStreamReader(this.contentResolver.openInputStream(data),
            "UTF-8")).useLines {
        it.forEachIndexed { index, line ->
            if (index % 10 == 0 || linesCount == index) {
                ShowLogs.log(TAG, "update notification : $index")

                //notificationTools.updateNewCourseNotificationProgress(createCourseNotification, linesCount, index)
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

private val MOCKED_LINES_COUNT = 20
private fun getLinesCount(): Int {
    return MOCKED_LINES_COUNT
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
    //dbHelper.updateCourseQuestionsAmount(courseId, questionsAmount)
}

private fun updateTaskCompleteUI(courseId: Int) {
    //notificationTools.cancelNotification(ConstantValues.CREATE_NEW_COURSE_NOTIFICATION_ID)
    //notificationTools.displaySavedCourseNotification(courseTitle, courseId)

//    LocalBroadcastManager.getInstance(this).sendBroadcast(
//            Intent(ConstantValues.INTENT_FILTER_NEW_COURSE_CREATION_SERVICE)
//                    .putExtra(ConstantValues.INTENT_KEY_IS_B_CREATE_BLOCKED, true))

}

private fun updateTaskFailedUI() {
//    LocalBroadcastManager.getInstance(this).sendBroadcast(
//            Intent(ConstantValues.INTENT_FILTER_NEW_COURSE_CREATION_SERVICE)
//                    .putExtra(ConstantValues.INTENT_KEY_COURSE_CREATION_FAILED, true))
}

private fun getLinesCount(inputStream: InputStream): Int {
    val lineNumberReader = LineNumberReader(InputStreamReader(inputStream, "UTF-8"))
    lineNumberReader.skip(Long.MAX_VALUE)
    val linesCount = lineNumberReader.lineNumber
    lineNumberReader.close()
    return linesCount
}

private fun saveTaskInDB(questionDo: QuestionDo) {
//    if (!dbHelper.addQuestionToLocalDB(questionDo)) {
//        dbHelper.addQuestionToLocalDB(questionDo)
//    }

}