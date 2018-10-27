package mr.kostua.learningpro.practiceCards

import io.reactivex.Observable
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.toolsMVP.BasePresenter
import mr.kostua.learningpro.toolsMVP.BaseView

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
interface PracticeCardsContract {
    interface View   : BaseView {
        fun initializeRecycleView(data: ArrayList<QuestionDo>)
        fun showToast(text: String)
        fun goBack()
        fun markAsDone(isLastCard: Boolean)
        fun lastCardDeleted(deletedItemPosition: Int)
        fun notifyDataSetChangedAdapter()
    }

    interface Presenter : BasePresenter<View> {
        fun populateNotLearnedCards(courseId: Int)
        fun populateAllCards(courseId: Int)
        fun updateViewCountOfCard(questionDo: QuestionDo)
        fun updateQuestion(questionDo: QuestionDo)
        fun increaseCourseDoneQuestionsAmountBy(courseId: Int, doneQuestionsAmount: Int)
        fun disposeAll()
        fun subscribeToMarkAsDoneButton(observable: Observable<QuestionDo>)
        fun subscribeToViewCounts(observable: Observable<QuestionDo>)
        fun getCardPositionInList(cardId: Int): Int
        fun handleItemDeletedIntent(deletedCardId: Int)
        fun handleItemEditedIntent(courseId: Int)
    }

}
