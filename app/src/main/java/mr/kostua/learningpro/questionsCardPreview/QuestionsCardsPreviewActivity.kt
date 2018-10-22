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
    private lateinit var practiceCardsIntent: Intent
    private fun isStartedToEditOneItem() = questionToEditId != -1
    private var deletedQuestionsAmount = 0


    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_questions_card_preview)
        notificationTools.cancelNotification(ConstantValues.SAVED_COURSE_NOTIFICATION_ID)
        initializeViews()
    }

    private fun initializeViews() {
        presenter.takeView(this)
        courseId = intent.getIntExtra(ConstantValues.COURSE_ID_KEY, -1)
        practiceCardsIntent = Intent(this, PracticeCardsActivity::class.java).apply {
            putExtra(ConstantValues.COURSE_ID_KEY, courseId)
        }
        questionToEditId = intent.getIntExtra(ConstantValues.QUESTION_ID_KEY, -1)
        if (isStartedToEditOneItem()) {
            presenter.populateQuestionToEdit(questionToEditId)
        } else {
            presenter.populateNotAcceptedQuestions(courseId)
        }
    }

    override fun onResume() {
        super.onResume()
        ShowLogs.log(TAG, "onResume with : courseId$courseId, questionToEditId$questionToEditId")
    }

    override fun onPause() {
        super.onPause()
        setQuestionsAmount()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAll()
        questionCardsCompositeDisposables.clear()
    }

    @SuppressLint("SetTextI18n")
    override fun initializeRecycleView(data: ArrayList<QuestionDo>) {
        questionsRecycleViewAdapter = QuestionCardsPreviewRecycleViewAdapter(data, this)
        questionCardsCompositeDisposables.addAll(
                questionsRecycleViewAdapter.getButtonAcceptObservable().subscribe({ onNext ->
                    presenter.acceptQuestion(onNext)
                    if (isLastQuestionCard(data.size)) {
                        allCardsReviewedContinue()
                    }
                }, {
                    showToast("please try to accept this question card again")
                    ShowLogs.log(TAG, "initializeRecycleView() acceptObservable error : ${it.message}")
                }),
                questionsRecycleViewAdapter.getButtonSaveObservable().subscribe({ onNext ->
                    presenter.updateQuestion(onNext)
                    if (data.size == 1 && isStartedToEditOneItem()) {
                        practiceCardsIntent.putExtra(ConstantValues.COURSE_ITEM_EDITED_KEY, true)
                    }
                }, {
                    showToast("please try to save this question card again")
                }),
                questionsRecycleViewAdapter.getButtonDeleteObservable().subscribe({ onNext ->
                    presenter.deleteQuestion(onNext, courseId)
                    deletedQuestionsAmount++
                    if (isLastQuestionCard(data.size)) {
                        allCardsReviewedContinue(isDeleted = true)
                    }
                }, {
                    showToast("please try to save this question card again")
                }))
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
        pbQuestionsPreview.visibility = View.GONE
    }

    //TODO maybe delete this postDelayed animation (read about performance is it harmful??)
    private fun allCardsReviewedContinue(isDeleted: Boolean = false) {
        rvQuestionsPreview.postDelayed({
            setQuestionsAmount()
            questionsPreviewFinished(isDeleted)
        }, ConstantValues.ALL_CARDS_REVIEWS_ANIMATION_TIME_MS)
    }

    private fun setQuestionsAmount() {
        if (courseId != -1 && deletedQuestionsAmount > 0) {
            ShowLogs.log(TAG, "setQuestionsAmount : deleteQuestionsAmount $deletedQuestionsAmount")
            presenter.decreaseQuestionsAmountBy(courseId, deletedQuestionsAmount)
            deletedQuestionsAmount = 0
        }
    }

    private fun isLastQuestionCard(dataSize: Int) = dataSize == 0

    private fun questionsPreviewFinished(isDeleted: Boolean = false) {
        if (isStartedToEditOneItem()) {
            setIntentStartedToEdit(isDeleted)
        } else {
            presenter.setCourseReviewedTrue(courseId)
        }
        startActivity(practiceCardsIntent)
    }

    private fun setIntentStartedToEdit(isDeleted: Boolean) {
        practiceCardsIntent.apply {
            if (isDeleted) {
                putExtra(ConstantValues.COURSE_ITEM_DELETED_ID_KEY, questionToEditId)
            } else {
                putExtra(ConstantValues.COURSE_ITEM_ID_TO_FOCUS_KEY, questionToEditId)
            }
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }
    }
    
    override fun showToast(text: String) {
        notificationTools.showToastMessage(text)
    }
}
