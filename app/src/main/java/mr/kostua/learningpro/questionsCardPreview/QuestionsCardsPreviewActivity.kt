package mr.kostua.learningpro.questionsCardPreview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.view.View
import io.reactivex.disposables.CompositeDisposable
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator
import kotlinx.android.synthetic.main.activity_questions_card_preview.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.injections.scopes.ActivityScope
import mr.kostua.learningpro.main.BaseDaggerActivity
import mr.kostua.learningpro.practiceCards.PracticeCardsActivity
import mr.kostua.learningpro.tools.*
import javax.inject.Inject


@ActivityScope
class QuestionsCardsPreviewActivity : BaseDaggerActivity(), QuestionCardsPreviewContract.View {
    private val TAG = this.javaClass.simpleName
    @Inject
    public lateinit var notificationTools: NotificationTools
    @Inject
    public lateinit var presenter: QuestionCardsPreviewContract.Presenter

    private lateinit var questionsRecycleViewAdapter: QuestionCardsPreviewRecycleViewAdapter
    private val questionCardsCompositeDisposables = CompositeDisposable()
    private var courseId = -1
    private var questionToEditId = -1
    private fun isStartedToEditOneItem() = questionToEditId != -1
    private var deletedQuestionsAmount = 0


    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_questions_card_preview)
        notificationTools.cancelNotification(ConstantValues.SAVED_COURSE_NOTIFICATION_ID)
        initializeViews()
    }

    override fun onResume() {
        super.onResume()
        ShowLogs.log(TAG, "onResume with : courseId$courseId, questionToEditId$questionToEditId")
    }

    override fun onPause() {
        super.onPause()
        setQuestionsAmount()

    }

    private fun setQuestionsAmount() {
        if (courseId != -1 && deletedQuestionsAmount > 0) {
            ShowLogs.log(TAG, "setQuestionsAmount : deleteQuestionsAmount $deletedQuestionsAmount")
            presenter.decreaseQuestionsAmountBy(courseId, deletedQuestionsAmount)
            deletedQuestionsAmount = 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAll()
        questionCardsCompositeDisposables.clear()
    }

    private fun initializeViews() {
        courseId = intent.getIntExtra(ConstantValues.COURSE_ID_KEY, -1)
        questionToEditId = intent.getIntExtra(ConstantValues.QUESTION_ID_KEY, -1)
        presenter.takeView(this)
        if (isStartedToEditOneItem()) {
            presenter.populateQuestionToEdit(questionToEditId)
        } else {
            presenter.populateNotAcceptedQuestions(courseId)
        }

    }

    @SuppressLint("SetTextI18n")
    override fun initializeRecycleView(data: ArrayList<QuestionDo>) {
        questionsRecycleViewAdapter = QuestionCardsPreviewRecycleViewAdapter(data, this)
        questionCardsCompositeDisposables.addAll(
                questionsRecycleViewAdapter.getButtonAcceptObservable().subscribe({
                    presenter.acceptQuestion(it)
                    if (isLastQuestionCard(data.size)) {
                        allCardsReviewedContinue()
                    }
                }, {
                    showToast("please try to accept this question card again")
                    ShowLogs.log(TAG, "initializeRecycleView() acceptObservable error : ${it.message}")
                }),
                questionsRecycleViewAdapter.getButtonSaveObservable().subscribe({
                    presenter.updateQuestion(it)
                }, {
                    showToast("please try to save this question card again")
                }),
                questionsRecycleViewAdapter.getButtonDeleteObservable().subscribe({
                    presenter.deleteQuestion(it, courseId)
                    deletedQuestionsAmount++
                    if (isLastQuestionCard(data.size)) {
                        allCardsReviewedContinue()
                    }
                }, {
                    showToast("please try to save this question card again")
                }))
        pbQuestionsPreview.visibility = View.GONE
        rvQuestionsPreview.run {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@QuestionsCardsPreviewActivity,
                    LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = FadeInDownAnimator().apply {
                removeDuration = 600
                changeDuration = 250
                moveDuration = 300
            }
            adapter = questionsRecycleViewAdapter
            PagerSnapHelper().attachToRecyclerView(this)
        }
    }

    private fun allCardsReviewedContinue(isDeleted: Boolean = false) {
        rvQuestionsPreview.postDelayed({
            setQuestionsAmount()
            questionsPreviewFinished(isDeleted)
        }, ConstantValues.ALL_CARDS_REVIEWS_ANIMATION_TIME_MS)

    }


    private fun isLastQuestionCard(dataSize: Int) = dataSize == 0

    private fun questionsPreviewFinished(isDeleted: Boolean = false) {
        startActivity(Intent(this, PracticeCardsActivity::class.java)
                .putExtra(ConstantValues.COURSE_ID_KEY, courseId).apply {
                    if (isStartedToEditOneItem()) {
                        if (isDeleted) {
                            putExtra(ConstantValues.COURSE_ITEM_DELETED_ID_KEY, questionToEditId)
                        } else {
                            putExtra(ConstantValues.COURSE_ITEM_ID_TO_FOCUS_KEY, questionToEditId)
                        }
                        addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    } else {
                        presenter.setCourseReviewedTrue(courseId)
                    }
                })
    }


    override fun showToast(text: String) {
        notificationTools.showToastMessage(text)
    }
}
