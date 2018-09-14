package mr.kostua.learningpro.practiceCards

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.injections.scopes.ActivityScope
import mr.kostua.learningpro.tools.CourseDBUsingHelper
import mr.kostua.learningpro.tools.ShowLogs
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
@ActivityScope
class PracticeCardsPresenter @Inject constructor(private val db: DBHelper) : PracticeCardsContract.Presenter {
    private val TAG = this.javaClass.simpleName
    override lateinit var view: PracticeCardsContract.View
    override fun takeView(view: PracticeCardsContract.View) {
        this.view = view
    }

    private val disposables = CompositeDisposable()

    override fun populateAllCards(courseId: Int) {
        disposables.add(CourseDBUsingHelper.populateQuestion(db, object : DisposableSingleObserver<List<QuestionDo>>() {
            override fun onSuccess(questions: List<QuestionDo>) {
                view.initializeRecycleView(questions as ArrayList)
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "populateNotAcceptedQuestions onError : ${e.message}")
            }

        }, courseId))
    }

    override fun updateQuestion(questionDo: QuestionDo) {
        disposables.add(CourseDBUsingHelper.updateQuestion(db, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(updatedItems: Int) {
                if (updatedItems == 1) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

                } else {

                }
            }

            override fun onError(e: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }, questionDo))
    }

    override fun deleteQuestion(questionDo: QuestionDo, courseId: Int) {
        disposables.add(CourseDBUsingHelper.deleteQuestion(db, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(updatedItems: Int) {
                if (updatedItems == 1) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

                } else {

                }
            }

            override fun onError(e: Throwable) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }, questionDo))
    }

    override fun disposeAll() {
        disposables.clear()
    }
}