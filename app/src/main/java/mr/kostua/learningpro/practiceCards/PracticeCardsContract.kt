package mr.kostua.learningpro.practiceCards

import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.toolsMVP.BasePresenter
import mr.kostua.learningpro.toolsMVP.BaseView

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
interface PracticeCardsContract {
    interface View : BaseView {
        fun initializeRecycleView(data: ArrayList<QuestionDo>)
        fun showToast(text: String)
    }

    interface Presenter : BasePresenter<View> {
        fun populateAllCards(courseId : Int)
        fun updateViewCountOfCard(questionDo: QuestionDo)
        fun updateQuestion(questionDo: QuestionDo)
        fun disposeAll()
    }

}
