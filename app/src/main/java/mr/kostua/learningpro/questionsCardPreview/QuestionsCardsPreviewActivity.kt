package mr.kostua.learningpro.questionsCardPreview

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.View
import android.view.inputmethod.EditorInfo
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
        val courseId = intent.getIntExtra(ConstantValues.CONTINUE_COURSE_CREATION_COURSE_ID_KEY, -1)
        presenter.takeView(this)
        presenter.populateCourses(courseId)
    }
//TODO probably the best way is to move buttons inside RecycleView adapter and also TV with items numbers (so the onClicks will work propeplry because onBind sucks)
    @SuppressLint("SetTextI18n")
    override fun initializeRecycleView(data: ArrayList<QuestionDo>) {
        var currentItemPosition: Int
        var etQuestionPreview: EditText
        var etAnswerPreview: EditText
        questionsRecycleViewAdapter = RecycleViewAdapter(data, R.layout.question_preview_row_item,
                object : ViewHolderBinder<QuestionDo> {
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
                        rvQuestionsPreview.scrollToPosition(currentItemPosition + 1)//TODO test what to do first Scroll or remove from LIST
                        data.remove(currentItem)
                        deleteQuestionDo(currentItemPosition, currentItem)
                    }


                    private fun makeEditTextToBeText(view: EditText) {
                        with(view) {
                            setTextColor(ContextCompat.getColor(this@QuestionsCardsPreviewActivity, R.color.black))
                            //inputType = EditorInfo.TYPE_NULL //TODO make text to be scrollable but not editable (just hide the keyboard in onTouchListener)
                            setSelection(0)
                        }
                    }

                    private fun makeEditTextToBeEditText(view: EditText) {
                        with(view) {
                            setTextColor(ContextCompat.getColor(this@QuestionsCardsPreviewActivity, R.color.white))
                            setSelection(0)
                        }
                    }

                    private fun animateFromAcceptToSave() {
                        bAcceptOrSave.setText(R.string.questionPreviewSaveButton)
                        bEditOrDelete.setText(R.string.questionPreviewDeleteButton)
                        //TODO implement visual animation
                    }

                    private fun animateFromSaveToAccept() {
                        bAcceptOrSave.setText(R.string.questionPreviewAcceptButton)
                        bEditOrDelete.setText(R.string.questionPreviewEditButton)
                        //TODO implement visual animation
                    }

                    override fun bind(view: View, item: QuestionDo) {
                        with(item) {
                            currentItemPosition = data.indexOf(this)
                            tvCurrentQuestionNumber.text = "${currentItemPosition + 1} / ${data.size}" //TODO move it to different method (bind doesn't every proper time)

                            etQuestionPreview = view.findViewById<EditText>(R.id.etQuestionPreview).apply {
                                makeEditTextToBeText(this)
                                setText(question)
                            }
                            etAnswerPreview = view.findViewById<EditText>(R.id.etAnswerPreview).apply {
                                //TODO make it scrollable
                                makeEditTextToBeText(this)
                                setText(answer)
                            }
                            bAcceptOrSave.setOnClickListener {
                                when ((it as Button).text) {
                                    getString(R.string.questionPreviewAcceptButton) -> {
                                        acceptQuestion(this, currentItemPosition)
                                    }
                                    getString(R.string.questionPreviewSaveButton) -> {
                                        saveQuestion(this, currentItemPosition)
                                        question = etQuestionPreview.text.toString()
                                        answer = etAnswerPreview.text.toString()
                                        animateFromSaveToAccept()
                                        makeEditTextToBeText(etAnswerPreview)
                                        makeEditTextToBeText(etQuestionPreview)
                                    }
                                }
                            }
                            bEditOrDelete.setOnClickListener {
                                when ((it as Button).text) {
                                    getString(R.string.questionPreviewEditButton) -> {
                                        makeEditTextToBeEditText(etQuestionPreview)
                                        makeEditTextToBeEditText(etAnswerPreview)
                                        animateFromAcceptToSave()
                                        //TODO EDIT TEXT with look of TEXT view and onTouchListener when to do keyboard displaying (or just hide and after show keyboard)
                                        //https://stackoverflow.com/questions/2119072/how-to-do-something-after-user-clicks-on-my-edittext
                                    }
                                    getString(R.string.questionPreviewDeleteButton) -> {
                                        deleteQuestion(this, currentItemPosition)
                                        animateFromSaveToAccept()
                                        makeEditTextToBeText(etQuestionPreview)
                                        makeEditTextToBeText(etAnswerPreview)
                                    }
                                }
                            }
                        }
                    }
                }) //TODO read about inner classes inside activity (memory leaks ???)

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
        presenter.deleteQuestion(questionDo)
    }

    override fun showToast(text: String) {
        notificationTools.showToastMessage(text)
    }
}
