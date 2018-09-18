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
        disposables.add(CourseDBUsingHelper.populateNotLearnedQuestions(db, object : DisposableSingleObserver<List<QuestionDo>>() {
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
                    ShowLogs.log(TAG, "updateQuestion item updated successfully ")
                    view.showToast("Card was learned successfully, congratulations!")
                    //TODO here in view maybe make some animation as firework
                } else {
                    ShowLogs.log(TAG, "updateQuestion no item updated")

                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "updateQuestion error : ${e.message}")
            }
        }, questionDo))
    }

    override fun updateViewCountOfCard(questionDo: QuestionDo) {
        disposables.add(CourseDBUsingHelper.updateQuestion(db, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(updatedItems: Int) {
                if (updatedItems != 1) {
                    ShowLogs.log(TAG, "updateViewCountOfCard no item updated")
                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "updateViewCountOfCard error : ${e.message}")

            }

        }, questionDo))
    }

    override fun disposeAll() {
        disposables.clear()
    }
}