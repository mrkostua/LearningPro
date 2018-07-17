package mr.kostua.learningpro.data

import mr.kostua.learningpro.data.local.LocalDB
import mr.kostua.learningpro.data.local.QuestionDo
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/17/2018.
 */
class DBHelper @Inject constructor(private val db: LocalDB) {

    fun addQuestionToLocalDB(question: QuestionDo) {
        if (-1L == db.questionsDao().addQuestion(question)) {
            throw ValueNotInsertedException("question : ${question.question} wasn't inserted")
        }
    }
}

private class ValueNotInsertedException(message: String) : Throwable(message)