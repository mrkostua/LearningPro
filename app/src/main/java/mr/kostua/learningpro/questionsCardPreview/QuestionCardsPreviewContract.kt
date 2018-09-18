package mr.kostua.learningpro.questionsCardPreview

import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.toolsMVP.BasePresenter
import mr.kostua.learningpro.toolsMVP.BaseView

/**
 * @author Kostiantyn Prysiazhnyi on 8/2/2018.
 */
interface QuestionCardsPreviewContract {
    interface View : BaseView {
        fun initializeRecycleView(data: ArrayList<QuestionDo>)
        fun showToast(text: String)
    }

    interface Presenter : BasePresenter<View> {
        fun populateNotAcceptedQuestions(courseId: Int)
        fun populateQuestionToEdit(courseItemId: Int)
        fun acceptQuestion(questionDo: QuestionDo)
        fun updateQuestion(questionDo: QuestionDo)
        fun deleteQuestion(questionDo: QuestionDo, courseId: Int)
        fun setCourseReviewedTrue(courseId: Int)
        fun disposeAll()
    }

}