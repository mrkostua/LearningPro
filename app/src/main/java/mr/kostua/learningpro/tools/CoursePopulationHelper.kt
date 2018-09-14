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
    fun populateQuestion(db: DBHelper, observer: DisposableSingleObserver<List<QuestionDo>>,
                         courseId: Int): DisposableSingleObserver<List<QuestionDo>> =
            db.getCourseWithNotAcceptedQuestions(courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)
    fun populateNotLearnedQuestion(db: DBHelper, observer: DisposableSingleObserver<List<QuestionDo>>,
                         courseId: Int): DisposableSingleObserver<List<QuestionDo>> =
            db.getCourseWithNotAcceptedQuestions(courseId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(observer)
    fun populateNotAcceptedQuestion(db: DBHelper, observer: DisposableSingleObserver<List<QuestionDo>>,
                         courseId: Int): DisposableSingleObserver<List<QuestionDo>> =
            db.getCourseWithNotAcceptedQuestions(courseId)
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
}