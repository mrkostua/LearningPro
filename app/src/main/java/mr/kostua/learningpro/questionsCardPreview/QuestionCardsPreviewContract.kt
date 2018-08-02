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

    }

    interface Presenter : BasePresenter<View> {
        fun populateCourses(courseId: Int)
        fun acceptQuestion(questionId: Int)
        fun updateQuestionDo(questionId: Int, questionDo: QuestionDo)
        fun deleteQuestion(questionId: Int)
    }

}