package mr.kostua.learningpro.practiceCards

import io.reactivex.Observable
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
    private val data = ArrayList<QuestionDo>()
    private fun isLastCard() = data.size == 1

    override fun getCardPositionInList(cardId: Int): Int {
        data.forEachIndexed { index, questionDo ->
            if (questionDo.id == cardId) {
                return index
            }
        }
        return -1
    }

    override fun handleItemDeletedIntent(deletedCardId: Int) {
        getCardPositionInList(deletedCardId).let {
            if (it != -1) {
                if (isLastCard()) {
                    view.lastCardDeleted(it)
                } else {
                    data.removeAt(it)
                    view.notifyDataSetChangedAdapter()
                }
            }
        }
    }

    override fun handleItemEditedIntent(courseId: Int) {
        updateCardsData(courseId)
    }

    override fun subscribeToMarkAsDoneButton(observable: Observable<QuestionDo>) {
        disposables.add(observable.subscribe({
            updateQuestion(it)
            view.markAsDone(isLastCard())
        }, {
            view.showToast("please try to \"mark as done\" this card again")
        }))
    }

    override fun subscribeToViewCounts(observable: Observable<QuestionDo>) {
        disposables.add(observable.subscribe({
            updateViewCountOfCard(it)
        }, {
            ShowLogs.log(TAG, "subscribeToViewCounts error : ${it.message}")
        }))
    }

    override fun populateNotLearnedCards(courseId: Int) {
        disposables.add(DBObserverHelper.getNotLearnedQuestions(db, object : DisposableSingleObserver<List<QuestionDo>>() {
            override fun onSuccess(questions: List<QuestionDo>) {
                if (questions.isNotEmpty()) {
                    view.initializeRecycleView(getChangedCardsData(questions))
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

    override fun populateAllCards(courseId: Int) {
        disposables.add(DBObserverHelper.getQuestions(db, object : DisposableSingleObserver<List<QuestionDo>>() {
            override fun onSuccess(list: List<QuestionDo>) {
                view.initializeRecycleView(getChangedCardsData(list))
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

    private fun updateCardsData(courseId: Int) {
        disposables.add(DBObserverHelper.getNotLearnedQuestions(db, object : DisposableSingleObserver<List<QuestionDo>>() {
            override fun onSuccess(questions: List<QuestionDo>) {
                if (questions.isNotEmpty()) {
                    getChangedCardsData(questions)
                    view.notifyDataSetChangedAdapter()
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

    private fun getChangedCardsData(list: List<QuestionDo>) =
            data.apply {
                clear()
                data.addAll(list)
                data
            }
}