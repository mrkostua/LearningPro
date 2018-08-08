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

    //TODO hide keyboard (appears on activity start and RV initialization)
    @SuppressLint("SetTextI18n")
    override fun initializeRecycleView(data: ArrayList<QuestionDo>) {
        questionsRecycleViewAdapter = RecycleViewAdapter(data, R.layout.question_preview_row_item,
                object : ViewHolderBinder<QuestionDo> {
                    private var currentItemPosition = -1
                    private lateinit var etQuestionPreview: EditText
                    private lateinit var etAnswerPreview: EditText
                    private lateinit var tvCurrentQuestionNumber: TextView
                    private lateinit var bAcceptOrSave: Button
                    private lateinit var bEditOrDelete: Button

                    override fun bind(view: View, item: QuestionDo, payload: MutableList<Any>) {
                    }

                    override fun bind(view: View, item: QuestionDo) {
                        tvCurrentQuestionNumber = view.findViewById(R.id.tvCurrentQuestionNumber)
                        bAcceptOrSave = view.findViewById(R.id.bAcceptOrSave)
                        bEditOrDelete = view.findViewById(R.id.bEditOrDelete)
                        with(item) {
                            currentItemPosition = data.indexOf(this)
                            tvCurrentQuestionNumber.text = "${currentItemPosition + 1} / ${data.size}"

                            etQuestionPreview = view.findViewById<EditText>(R.id.etQuestionPreview).apply {
                                if (isAccepted) {
                                    makeEditTextToBeEditText(this)
                                } else {
                                    makeEditTextToBeText(this)
                                }
                                setText(question)
                            }
                            etAnswerPreview = view.findViewById<EditText>(R.id.etAnswerPreview).apply {
                                if (isAccepted) {
                                    makeEditTextToBeEditText(this)
                                } else {
                                    makeEditTextToBeText(this)
                                }
                                setText(answer)
                            }

                            bAcceptOrSave.apply {
                                if (isAccepted && text == getString(R.string.questionPreviewAcceptButton)) {
                                    animateFromAcceptToSave(this, bEditOrDelete)
                                } else {
                                    animateFromSaveToAccept(this, bEditOrDelete)
                                }
                                setOnClickListener {
                                    when ((it as Button).text) {
                                        getString(R.string.questionPreviewAcceptButton) -> {
                                            acceptQuestion(this@with, currentItemPosition)
                                        }
                                        getString(R.string.questionPreviewSaveButton) -> {
                                            question = etQuestionPreview.text.toString()
                                            answer = etAnswerPreview.text.toString()
                                            saveQuestion(this@with, currentItemPosition)
                                            animateFromSaveToAccept(bAcceptOrSave, bEditOrDelete)
                                            setItemToNotEditable()
                                        }
                                    }
                                    tvCurrentQuestionNumber.text = "${currentItemPosition + 1} / ${data.size}"
                                }
                            }
                            bEditOrDelete.apply {
                                if (isAccepted && text == getString(R.string.questionPreviewDeleteButton)) {
                                    animateFromAcceptToSave(bAcceptOrSave, this)
                                } else {
                                    animateFromSaveToAccept(this, bEditOrDelete)
                                }
                                setOnClickListener {
                                    when ((it as Button).text) {
                                        getString(R.string.questionPreviewEditButton) -> {
                                            animateFromAcceptToSave(bAcceptOrSave, bEditOrDelete)
                                            setItemToEditable(item.id!!)
                                        }
                                        getString(R.string.questionPreviewDeleteButton) -> {
                                            deleteQuestion(this@with, currentItemPosition)
                                            animateFromSaveToAccept(bAcceptOrSave, bEditOrDelete)
                                            setItemToNotEditable()
                                            tvCurrentQuestionNumber.text = "${currentItemPosition + 1} / ${data.size}"
                                        }
                                    }
                                }
                            }
                        }
                    }

                    private fun setItemToEditable(itemId: Int) {
                        data.forEach {
                            if (it.id == itemId && !it.isAccepted) {
                                it.isAccepted = true
                                questionsRecycleViewAdapter.notifyItemChanged(data.indexOf(it))
                            } else if (it.isAccepted == true) {
                                it.isAccepted = false
                                questionsRecycleViewAdapter.notifyItemChanged(data.indexOf(it))
                            }
                        }
                    }

                    private fun setItemToNotEditable() {
                        data.forEach {
                            if (it.isAccepted == true) {
                                it.isAccepted = false
                                questionsRecycleViewAdapter.notifyItemChanged(data.indexOf(it))
                            }
                        }
                    }

                    private fun acceptQuestion(currentItem: QuestionDo, currentItemPosition: Int) {
                        data.remove(currentItem)
                        acceptQuestionCard(currentItem, currentItemPosition)
                    }

                    private fun saveQuestion(currentItem: QuestionDo, currentItemPosition: Int) {
                        updateQuestionCard(currentItemPosition, currentItem)
                    }

                    private fun deleteQuestion(currentItem: QuestionDo, currentItemPosition: Int) {
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
        questionsRecycleViewAdapter.notifyDataSetChanged()
        presenter.acceptQuestion(questionDo)
    }

    private fun updateQuestionCard(itemPosition: Int, questionDo: QuestionDo) {
        questionsRecycleViewAdapter.notifyItemChanged(itemPosition)
        presenter.updateQuestion(questionDo)
    }

    private fun deleteQuestionDo(itemPosition: Int, questionDo: QuestionDo) {
        questionsRecycleViewAdapter.notifyItemRemoved(itemPosition)
        questionsRecycleViewAdapter.notifyDataSetChanged()
        presenter.deleteQuestion(questionDo, courseId)
    }

    override fun showToast(text: String) {
        notificationTools.showToastMessage(text)
    }
}
