package mr.kostua.learningpro.questionsCardPreview

import io.reactivex.Observable
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.toolsMVP.BasePresenter
import mr.kostua.learningpro.toolsMVP.BaseView
import java.util.*

/**
 * @author Kostiantyn Prysiazhnyi on 8/2/2018.
 */
interface QuestionCardsPreviewContract {
    interface View : BaseView {
        fun initializeRecycleView(data: ArrayList<QuestionDo>)
        fun showToast(text: String)
        fun acceptQuestion(question : QuestionDo)
        fun deleteQuestion(question: QuestionDo)
        fun saveQuestion (question: QuestionDo)
    }

    interface Presenter : BasePresenter<View> {
        fun populateNotAcceptedQuestions(courseId: Int)
        fun populateQuestionToEdit(questionToEditId: Int)
        fun acceptQuestion(questionDo: QuestionDo)
        fun updateQuestion(questionDo: QuestionDo)
        fun deleteQuestion(questionDo: QuestionDo, courseId: Int)
        fun setCourseReviewedTrue(courseId: Int)
        fun disposeAll()
        fun getDataSize() : Int
        fun decreaseQuestionsAmountBy(courseId: Int, decreaseBy: Int)
        fun subscribeToButtonAcceptClick(observable: Observable<QuestionDo>)
        fun subscribeToButtonDeleteClick(observable: Observable<QuestionDo>)
        fun subscribeToButtonSaveClick(observable: Observable<QuestionDo>)
    }

}