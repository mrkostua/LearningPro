package mr.kostua.learningpro.questionsCardPreview

import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.QuestionDo
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 8/2/2018.
 */
class QuestionCardsPreviewPresenter @Inject constructor(private val data: DBHelper) : QuestionCardsPreviewContract.Presenter {
    override lateinit var view: QuestionCardsPreviewContract.View

    override fun takeView(view: QuestionCardsPreviewContract.View) {
        this.view = view
    }

    override fun populateCourses(courseId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun acceptQuestion(questionId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateQuestionDo(questionId: Int, questionDo: QuestionDo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deleteQuestion(questionId: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    //TODO after show toast that action was complete

}