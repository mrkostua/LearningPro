package mr.kostua.learningpro.tools

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.QuestionDo

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
object CourseDBUsingHelper {
    fun getQuestions(db: DBHelper, observer: DisposableSingleObserver<List<QuestionDo>>,
                     courseId: Int): DisposableSingleObserver<List<QuestionDo>> =
            db.getCourseQuestions(courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)

    fun getNotLearnedQuestions(db: DBHelper, observer: DisposableSingleObserver<List<QuestionDo>>,
                               courseId: Int): DisposableSingleObserver<List<QuestionDo>> =
            db.getCourseWithNotLearnedQuestions(courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)

    fun getNotAcceptedQuestions(db: DBHelper, observer: DisposableSingleObserver<List<QuestionDo>>,
                                courseId: Int): DisposableSingleObserver<List<QuestionDo>> =
            db.getCourseWithNotAcceptedQuestions(courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)

    fun getQuestionBuId(db: DBHelper, observer: DisposableSingleObserver<QuestionDo>,
                        questionId: Int): DisposableSingleObserver<QuestionDo> =
            db.getQuestion(questionId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)


    fun updateQuestion(db: DBHelper, observer: DisposableSingleObserver<Int>,
                       questionDo: QuestionDo): DisposableSingleObserver<Int> =
            db.updateQuestion(questionDo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)

    fun deleteQuestion(db: DBHelper, observer: DisposableSingleObserver<Int>,
                       questionDo: QuestionDo): DisposableSingleObserver<Int> =
            db.deleteQuestion(questionDo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)

    fun setCourseQuestionsToNotLearned(db: DBHelper, observer: DisposableSingleObserver<Int>,
                                       courseId: Int): DisposableSingleObserver<Int> =
            db.setCourseQuestionsToNotLearned(courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)

    fun setCourseDoneQuestionsAmount(db: DBHelper, observer: DisposableSingleObserver<Int>, courseId: Int,
                                     doneQuestionsAmount: Int): DisposableSingleObserver<Int> =
            db.setCourseDoneQuestionsAmount(courseId, doneQuestionsAmount)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)
    fun getCourseDoneQuestionsAmount(db: DBHelper, observer: DisposableSingleObserver<Int>, courseId: Int): DisposableSingleObserver<Int> =
            db.getCourseDoneQuestionsAmount(courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)
}