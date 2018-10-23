package mr.kostua.learningpro.practiceCards

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.injections.scopes.ActivityScope
import mr.kostua.learningpro.tools.DBObserverHelper
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

    override fun populateNotLearnedCards(courseId: Int) {
        disposables.add(DBObserverHelper.getNotLearnedQuestions(db, object : DisposableSingleObserver<List<QuestionDo>>() {
            override fun onSuccess(questions: List<QuestionDo>) {
                if (questions.isNotEmpty()) {
                    view.initializeRecycleView(questions as ArrayList)
                } else {
                    view.showToast("All cards from this course are learned.")
                    view.goBack()
                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "getNotAcceptedQuestions onError : ${e.message}")
            }

        }, courseId))
    }

    override fun updateCardsData(courseId: Int) {
        disposables.add(DBObserverHelper.getNotLearnedQuestions(db, object : DisposableSingleObserver<List<QuestionDo>>() {
            override fun onSuccess(questions: List<QuestionDo>) {
                if (questions.isNotEmpty()) {
                    view.updateAdapterCardsData(questions as ArrayList)
                } else {
                    view.showToast("All cards from this course are learned.")
                    view.goBack()
                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "updateCardsData onError : ${e.message}")
            }

        }, courseId))
    }

    override fun populateAllCards(courseId: Int) {
        disposables.add(DBObserverHelper.getQuestions(db, object : DisposableSingleObserver<List<QuestionDo>>() {
            override fun onSuccess(list: List<QuestionDo>) {
                view.initializeRecycleView(list as ArrayList)
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "populateAllCards onError : ${e.message}")
            }

        }, courseId))
    }

    override fun increaseCourseDoneQuestionsAmountBy(courseId: Int, doneQuestionsAmount: Int) {
        disposables.add(DBObserverHelper.increaseCourseDoneQuestionsAmountBy(db, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(t: Int) {
                if (t == 1) {
                    ShowLogs.log(TAG, "increaseCourseDoneQuestionsAmountBy updated successfully")
                } else {
                    ShowLogs.log(TAG, "increaseCourseDoneQuestionsAmountBy no item updated")

                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "increaseCourseDoneQuestionsAmountBy onError : ${e.message}")

            }

        }, courseId, doneQuestionsAmount))
    }

    override fun updateQuestion(questionDo: QuestionDo) {
        disposables.add(DBObserverHelper.updateQuestion(db, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(updatedItems: Int) {
                if (updatedItems == 1) {
                    ShowLogs.log(TAG, "updateQuestion item updated successfully ")
                    view.showToast("Card was learned successfully, congratulations!")
                } else {
                    ShowLogs.log(TAG, "updateQuestion no item updated")
                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "updateQuestion error : ${e.message}")
            }
        }, questionDo))
    }

/*    override fun initializeDoneQuestionsAmount(courseId: Int) {
        disposables.add(CourseDBUsingHelper.getCourseDoneQuestionsAmount(db, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(doneQuestionsAmount: Int) {
                if (doneQuestionsAmount != -1) {
                    view.setDoneQuestionsAmount(doneQuestionsAmount)
                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "initializeDoneQuestionsAmount error : ${e.message}")
            }

        }, courseId))

    }*/

    override fun updateViewCountOfCard(questionDo: QuestionDo) {
        disposables.add(DBObserverHelper.updateQuestion(db, object : DisposableSingleObserver<Int>() {
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