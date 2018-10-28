package mr.kostua.learningpro.tools

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.data.local.QuestionDo

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
object DBObserverHelper {
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

    fun increaseCourseDoneQuestionsAmountBy(db: DBHelper, observer: DisposableSingleObserver<Int>, courseId: Int,
                                            increaseBy: Int): DisposableSingleObserver<Int> =
            db.increaseDoneQuestionsAmountBy(courseId, increaseBy)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)

    fun decreaseCourseQuestionsAmountBy(db: DBHelper, observer: DisposableSingleObserver<Int>, courseId: Int,
                                        decreaseBy: Int): DisposableSingleObserver<Int> =
            db.decreaseQuestionsAmount(courseId, decreaseBy)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)

    fun resetDoneQuestionsAmountToZero(db: DBHelper, observer: DisposableSingleObserver<Int>, courseId: Int): DisposableSingleObserver<Int> =
            db.setCourseDoneQuestionsToZero(courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)

    fun getCourse(db: DBHelper, observer: DisposableSingleObserver<CourseDo>, courseId: Int): DisposableSingleObserver<CourseDo> =
            db.getCourse(courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)
}