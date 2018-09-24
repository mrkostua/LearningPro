package mr.kostua.learningpro.data

import android.content.SharedPreferences
import io.reactivex.Flowable
import io.reactivex.Single
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.data.local.LocalDB
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.data.local.sharedPref.SPHelper
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/17/2018.
 */
class DBHelper @Inject constructor(private val db: LocalDB, sp: SharedPreferences) : SPHelper(sp) {
    private val TAG = this.javaClass.simpleName

    fun addQuestionToLocalDB(question: QuestionDo) = -1L != db.questionsDao().addQuestion(question)

    fun addCourseToLocalDB(courseDo: CourseDo): Single<Long> =
            Single.fromCallable { db.coursesDao().insertCourse(courseDo) }

    fun getAllCourses(): Flowable<List<CourseDo>> = db.coursesDao().getAllCourses().distinctUntilChanged()

    fun updateCourseQuestionsAmount(courseId: Int, questionsAmount: Int) = db.coursesDao().updateCourseQuestionsAmount(courseId, questionsAmount) == 1

    fun updateCourseIsReviewedState(courseId: Int) = db.coursesDao().updateCourseIsReviewedState(courseId) == 1


    fun countQuestionsAmountInCourse(courseId: Int): Single<Int> = Single.fromCallable { db.questionsDao().countQuestionsAmountInCourse(courseId) }

    fun getCourseWithQuestions(courseId: Int) = db.courseWithQuestionsDao().getCourseWithQuestions(courseId)

    fun getCourseQuestions(courseId: Int) = db.questionsDao().getAllQuestionsFromCourse(courseId)

    fun getCourse(courseId: Int): Single<CourseDo> = Single.fromCallable { db.coursesDao().getCourse(courseId) }

    fun getCourseWithNotAcceptedQuestions(courseId: Int) = db.questionsDao().getAllNotAcceptedQuestionsFromCourse(courseId)

    fun getQuestion(questionId: Int) = db.questionsDao().getQuestionFromCourse(questionId)

    fun getCourseWithNotLearnedQuestions(courseId: Int) = db.questionsDao().getAllNotLearnedQuestionsFromCourse(courseId)

    fun updateQuestion(question: QuestionDo): Single<Int> = Single.fromCallable { db.questionsDao().updateQuestion(question) }

    fun deleteQuestion(question: QuestionDo): Single<Int> = Single.fromCallable { db.questionsDao().deleteQuestion(question) }

    fun setCourseQuestionsToNotLearned(courseId: Int): Single<Int> = Single.fromCallable { db.questionsDao().setAllQuestionsToNotLearnedInCourse(courseId) }

    fun increaseDoneQuestionsAmountBy(courseId: Int, increaseValue: Int): Single<Int> = Single.fromCallable { db.coursesDao().increaseDoneQuestionsAmountBy(courseId, increaseValue) }

    fun setCourseDoneQuestionsAmount(courseId: Int): Single<Int> = Single.fromCallable { db.coursesDao().updateDoneQuestionsAmount(courseId) }

    fun getCourseDoneQuestionsAmount(courseId: Int): Single<Int> = Single.fromCallable { db.coursesDao().getCourseDoneQuestionsAmount(courseId) }

}