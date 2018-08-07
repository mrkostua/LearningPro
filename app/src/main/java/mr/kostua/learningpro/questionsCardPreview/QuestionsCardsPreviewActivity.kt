package mr.kostua.learningpro.questionsCardPreview

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_questions_card_preview.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.injections.scopes.ActivityScope
import mr.kostua.learningpro.main.BaseDaggerActivity
import mr.kostua.learningpro.tools.*
import javax.inject.Inject


@ActivityScope
class QuestionsCardsPreviewActivity : BaseDaggerActivity(), QuestionCardsPreviewContract.View {
    private val TAG = this.javaClass.simpleName
    @Inject
    public lateinit var notificationTools: NotificationTools
    @Inject
    public lateinit var presenter: QuestionCardsPreviewContract.Presenter

    private lateinit var questionsRecycleViewAdapter: RecycleViewAdapter<QuestionDo>
    private var courseId = -1

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_questions_card_preview)
        notificationTools.cancelNotification(ConstantValues.SAVED_COURSE_NOTIFICATION_ID)
        initializeViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAll()
    }

    private fun initializeViews() {
        courseId = intent.getIntExtra(ConstantValues.CONTINUE_COURSE_CREATION_COURSE_ID_KEY, -1)
        presenter.takeView(this)
        presenter.populateNotAcceptedQuestions(courseId)
    }
/**
 * Fix for scrolling to-do  (don't make constraints to bottom of layout) so when keyboard will appear they will be hidden not moved up
 * Fix for changes applied to next item to-do (is to on every bind (or check if current id is the same as editableCard id and in that case do changes ) inside bind()
 *      or to just use some argument of QuestionDo (as Accepted) to true if editable and if else inside bind()
 */
    @SuppressLint("SetTextI18n")
    override fun initializeRecycleView(data: ArrayList<QuestionDo>) { //TODO result of Onclick Edit (make changes to next item in recycleView)??
        questionsRecycleViewAdapter = RecycleViewAdapter(data, R.layout.question_preview_row_item,
                object : ViewHolderBinder<QuestionDo> {
                    private var currentItemPosition = -1
                    private lateinit var etQuestionPreview: EditText
                    private lateinit var etAnswerPreview: EditText
                    private lateinit var tvCurrentQuestionNumber: TextView
                    private lateinit var bAcceptOrSave: Button
                    private lateinit var bEditOrDelete: Button
                    private var isCardEditable = false
                    private var editableItemPosition = -1

                    override fun bind(view: View, item: QuestionDo) {
                        tvCurrentQuestionNumber = view.findViewById(R.id.tvCurrentQuestionNumber)
                        bAcceptOrSave = view.findViewById(R.id.bAcceptOrSave)
                        bEditOrDelete = view.findViewById(R.id.bEditOrDelete)
                        with(item) {
                            currentItemPosition = data.indexOf(this)
                            tvCurrentQuestionNumber.text = "${currentItemPosition + 1} / ${data.size}"

                            etQuestionPreview = view.findViewById<EditText>(R.id.etQuestionPreview).apply {
                                makeEditTextToBeText(this)
                                setText(question)
                            }
                            etAnswerPreview = view.findViewById<EditText>(R.id.etAnswerPreview).apply {
                                makeEditTextToBeText(this)
                                setText(answer)
                            }
                            if (isCardEditable && editableItemPosition == currentItemPosition) {
                                ShowLogs.log(TAG, "bind isCardEdible true")
                            }
                            bAcceptOrSave.apply {
                                setOnClickListener {
                                    when ((it as Button).text) {
                                        getString(R.string.questionPreviewAcceptButton) -> {
                                            acceptQuestion(this@with, currentItemPosition)
                                        }
                                        getString(R.string.questionPreviewSaveButton) -> {
                                            saveQuestion(this@with, currentItemPosition)
                                            question = etQuestionPreview.text.toString()
                                            answer = etAnswerPreview.text.toString()
                                            animateFromSaveToAccept(bAcceptOrSave, bEditOrDelete)
                                            makeEditTextToBeText(etAnswerPreview)
                                            makeEditTextToBeText(etQuestionPreview)
                                        }
                                    }
                                    tvCurrentQuestionNumber.text = "${currentItemPosition + 1} / ${data.size}"
                                    isCardEditable = false
                                    editableItemPosition = -1
                                }
                            }
                            bEditOrDelete.apply {
                                setOnClickListener {
                                    when ((it as Button).text) {
                                        getString(R.string.questionPreviewEditButton) -> {
                                            editableItemPosition = currentItemPosition
                                            isCardEditable = true
                                            makeEditTextToBeEditText(etQuestionPreview)
                                            makeEditTextToBeEditText(etAnswerPreview)
                                            animateFromAcceptToSave(bAcceptOrSave, bEditOrDelete)
                                        }
                                        getString(R.string.questionPreviewDeleteButton) -> {
                                            editableItemPosition = -1
                                            isCardEditable = false
                                            deleteQuestion(this@with, currentItemPosition)
                                            animateFromSaveToAccept(bAcceptOrSave, bEditOrDelete)
                                            makeEditTextToBeText(etQuestionPreview)
                                            makeEditTextToBeText(etAnswerPreview)
                                            tvCurrentQuestionNumber.text = "${currentItemPosition + 1} / ${data.size}"
                                        }
                                    }
                                }
                            }
                        }
                    }

                    private fun acceptQuestion(currentItem: QuestionDo, currentItemPosition: Int) {
                        rvQuestionsPreview.scrollToPosition(currentItemPosition + 1)
                        acceptQuestionCard(currentItem, currentItemPosition)
                        data.remove(currentItem)
                    }

                    private fun saveQuestion(currentItem: QuestionDo, currentItemPosition: Int) {
                        data[currentItemPosition].run {
                            this.question = currentItem.question
                            this.answer = currentItem.answer

                        }
                        updateQuestionCard(currentItemPosition, currentItem)
                    }

                    private fun deleteQuestion(currentItem: QuestionDo, currentItemPosition: Int) {
                        rvQuestionsPreview.scrollToPosition(currentItemPosition + 1)
                        data.remove(currentItem)
                        deleteQuestionDo(currentItemPosition, currentItem)
                    }


                    private fun makeEditTextToBeText(view: EditText) {
                        with(view) {
                            setTextColor(ContextCompat.getColor(this@QuestionsCardsPreviewActivity, R.color.black))
                            showSoftInputOnFocusAllAPI(false)
                        }
                    }

                    private fun makeEditTextToBeEditText(view: EditText) {
                        with(view) {
                            setTextColor(ContextCompat.getColor(this@QuestionsCardsPreviewActivity, R.color.white))
                            showSoftInputOnFocusAllAPI(true)
                            setSelection(0)
                        }
                    }

                    private fun animateFromAcceptToSave(bAcceptOrSave: Button, bEditOrDelete: Button) {
                        bAcceptOrSave.setText(R.string.questionPreviewSaveButton)
                        bEditOrDelete.setText(R.string.questionPreviewDeleteButton)
                        //TODO implement visual animation
                    }

                    private fun animateFromSaveToAccept(bAcceptOrSave: Button, bEditOrDelete: Button) {
                        bAcceptOrSave.setText(R.string.questionPreviewAcceptButton)
                        bEditOrDelete.setText(R.string.questionPreviewEditButton)
                        //TODO implement visual animation
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

    private fun acceptQuestionCard(questionDo: QuestionDo, itemPosition: Int) {
        questionsRecycleViewAdapter.notifyItemRemoved(itemPosition)
        presenter.acceptQuestion(questionDo)
    }

    private fun updateQuestionCard(itemPosition: Int, questionDo: QuestionDo) {
        questionsRecycleViewAdapter.notifyItemChanged(itemPosition)
        presenter.updateQuestion(questionDo)
    }

    private fun deleteQuestionDo(itemPosition: Int, questionDo: QuestionDo) {
        questionsRecycleViewAdapter.notifyItemRemoved(itemPosition)
        presenter.deleteQuestion(questionDo, courseId)
    }

    override fun showToast(text: String) {
        notificationTools.showToastMessage(text)
    }
}
