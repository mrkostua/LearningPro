package mr.kostua.learningpro.questionsCardPreview

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_questions_card_preview.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.injections.scopes.ActivityScope
import mr.kostua.learningpro.main.BaseDaggerActivity
import mr.kostua.learningpro.tools.ConstantValues
import mr.kostua.learningpro.tools.NotificationTools
import mr.kostua.learningpro.tools.RecycleViewAdapter
import mr.kostua.learningpro.tools.ViewHolderBinder
import javax.inject.Inject

@ActivityScope
class QuestionsCardsPreviewActivity : BaseDaggerActivity(), QuestionCardsPreviewContract.View {
    @Inject
    public lateinit var notificationTools: NotificationTools
    @Inject
    public lateinit var presenter: QuestionCardsPreviewContract.Presenter

    private lateinit var questionsRecycleViewAdapter: RecycleViewAdapter<QuestionDo>
    private var currentItemId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_questions_card_preview)
        notificationTools.cancelNotification(ConstantValues.SAVED_COURSE_NOTIFICATION_ID)
        initializeViews()
    }

    private fun initializeViews() {
        val courseId = intent.getIntExtra(ConstantValues.CONTINUE_COURSE_CREATION_COURSE_ID_KEY, -1)
        presenter.takeView(this)
        presenter.populateCourses(courseId)
    }

    @SuppressLint("SetTextI18n")
    override fun initializeRecycleView(data: ArrayList<QuestionDo>) {
        var currentItemPosition: Int
        var etQuestionPreview: EditText
        var etAnswerPreview: EditText
        questionsRecycleViewAdapter = RecycleViewAdapter(data, R.layout.question_preview_row_item,
                object : ViewHolderBinder<QuestionDo> {
                    override fun bind(view: View, item: QuestionDo) {
                        with(item) {
                            currentItemPosition = data.indexOf(this)
                            tvCurrentQuestionNumber.text = "${currentItemPosition + 1} / ${data.size}"

                            etQuestionPreview = view.findViewById(R.id.etQuestionPreview)
                            etQuestionPreview.run {
                                setText(question)
                                isEnabled = false
                            }
                            etAnswerPreview = view.findViewById(R.id.etAnswerPreview)
                            etAnswerPreview.run {
                                setText(answer)
                                isEnabled = false
                            }
                            bAcceptOrSave.setOnClickListener {
                                when ((it as Button).text) {
                                    getString(R.string.questionPreviewAcceptButton) -> {
                                        etQuestionPreview.isEnabled = false
                                        etAnswerPreview.isEnabled = false

                                        rvQuestionsPreview.scrollToPosition(currentItemPosition + 1)
                                        data.remove(this)
                                        acceptQuestionCard(id!!, currentItemPosition)
                                    }
                                    getString(R.string.questionPreviewSaveButton) -> {
                                        etQuestionPreview.isEnabled = false
                                        etAnswerPreview.isEnabled = false

                                        question = etQuestionPreview.text.toString()
                                        answer = etAnswerPreview.text.toString()
                                        data[currentItemPosition].run {
                                            this.question = item.question
                                            this.answer = item.answer

                                        }
                                        updateQuestionCard(currentItemPosition, id!!, this)
                                        //TODO do the animation back to Accept / Edit
                                    }
                                }

                            }
                            bEditOrDelete.setOnClickListener {
                                when ((it as Button).text) {
                                    getString(R.string.questionPreviewEditButton) -> {
                                        etQuestionPreview.run {
                                            isEnabled = true
                                            setTextColor(ContextCompat.getColor(this@QuestionsCardsPreviewActivity, R.color.white))
                                            setSelection(0)
                                        }

                                        etAnswerPreview.isEnabled = true
                                        etAnswerPreview.setTextColor(ContextCompat.getColor(this@QuestionsCardsPreviewActivity, R.color.white))
                                        //TODO do the animation to Save / Delete

                                    }
                                    getString(R.string.questionPreviewDeleteButton) -> {
                                        etQuestionPreview.isEnabled = false
                                        etAnswerPreview.isEnabled = false
                                        rvQuestionsPreview.scrollToPosition(currentItemPosition + 1)//TODO test what to do first Scroll or remove from LIST
                                        data.remove(this)
                                        deleteQuestionDo(currentItemPosition, id!!)
                                        //TODO do the animation back to Accept / Edit

                                    }
                                }
                            }
                        }
                    }
                })

        pbQuestionsPreview.visibility = View.GONE
        rvQuestionsPreview.run {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@QuestionsCardsPreviewActivity,
                    LinearLayoutManager.HORIZONTAL, false)
            adapter = questionsRecycleViewAdapter
        }
    }

    private fun acceptQuestionCard(questionId: Int, itemPosition: Int) {
        questionsRecycleViewAdapter.notifyItemRemoved(itemPosition)
        presenter.acceptQuestion(questionId)
    }

    private fun updateQuestionCard(itemPosition: Int, questionId: Int, questionDo: QuestionDo) {
        questionsRecycleViewAdapter.notifyItemChanged(itemPosition)
        presenter.updateQuestionDo(questionId, questionDo)
    }

    private fun deleteQuestionDo(itemPosition: Int, questionId: Int) {
        questionsRecycleViewAdapter.notifyItemRemoved(itemPosition)
        presenter.deleteQuestion(questionId)
    }
}
