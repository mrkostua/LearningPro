package mr.kostua.learningpro.data

import io.reactivex.Flowable
import io.reactivex.Single
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.data.local.LocalDB
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.tools.ShowLogs
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/17/2018.
 */
class DBHelper @Inject constructor(private val db: LocalDB) {
    private val TAG = this.javaClass.simpleName
    //TODO update this method to return Boolean and handel false result
    fun addQuestionToLocalDB(question: QuestionDo) {
        if (-1L == db.questionsDao().addQuestion(question)) {
            throw ValueNotInsertedException("question : ${question.question} wasn't inserted")
        }
    }

    fun addCourseToLocalDB(courseDo: CourseDo): Single<Long> =
            Single.fromCallable { db.coursesDao().insertCourse(courseDo) }

    fun getAllCourses(): Flowable<List<CourseDo>> = db.coursesDao().getAllCourses().distinctUntilChanged()

    fun updateCourse(courseId: Int, questionsAmount: Int) = db.coursesDao().updateCourseQuestionsAmount(courseId, questionsAmount) == 1
}

private class ValueNotInsertedException(message: String) : Throwable(message)