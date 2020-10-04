package mr.kostua.learningpro.questionsCardPreview

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
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
    private val questionCardsCompositeDisposables = CompositeDisposable()
    @Inject
    public lateinit var notificationTools: NotificationTools
    @Inject
    public lateinit var presenter: QuestionCardsPreviewContract.Presenter

    private lateinit var questionsRecycleViewAdapter: QuestionCardsPreviewRecycleViewAdapter
    private lateinit var practiceCardsIntent: Intent
    private var courseId = -1
    private var questionToEditId = -1
    private var deletedQuestionsAmount = 0
    private fun isStartedToEditOneItem() = questionToEditId != -1

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_questions_card_preview)
        if (intent.getBooleanExtra(ConstantValues.COURSE_STARTED_FROM_SERVICE, false)) {
            notificationTools.cancelNotification(ConstantValues.SAVED_COURSE_NOTIFICATION_ID)
        }
        courseId = intent.getIntExtra(ConstantValues.COURSE_ID_KEY, -1)
        practiceCardsIntent = Intent(this, PracticeCardsActivity::class.java).apply {
            putExtra(ConstantValues.COURSE_ID_KEY, courseId)
        }
        questionToEditId = intent.getIntExtra(ConstantValues.QUESTION_ID_KEY, -1)

        initializeViews()
    }

    private fun initializeViews() {
        presenter.takeView(this)
        if (isStartedToEditOneItem()) {
            presenter.populateQuestionToEdit(questionToEditId)
        } else {
            presenter.populateNotAcceptedQuestions(courseId)
        }
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
        presenter.subscribeToButtonAcceptClick(questionsRecycleViewAdapter.getButtonAcceptObservable())
        presenter.subscribeToButtonSaveClick(questionsRecycleViewAdapter.getButtonSaveObservable())
        presenter.subscribeToButtonDeleteClick(questionsRecycleViewAdapter.getButtonDeleteObservable())

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

    override fun acceptQuestion(question: QuestionDo, isLastCard: Boolean) {
        if (isLastCard) {
            allCardsReviewedContinue()
        }
    }

    override fun saveQuestion(question: QuestionDo, isLastEditedCard: Boolean) {
        if (isLastEditedCard && isStartedToEditOneItem()) {
            practiceCardsIntent.putExtra(ConstantValues.COURSE_ITEM_EDITED_KEY, true)
        }
    }

    override fun deleteQuestion(question: QuestionDo, isLastCard: Boolean) {
        deletedQuestionsAmount++
        if (isLastCard) {
            allCardsReviewedContinue(isDeleted = true)
        }
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

    private fun questionsPreviewFinished(isDeleted: Boolean = false) {
        if (isStartedToEditOneItem()) {
            setPracticeCardsIntent(isDeleted)
        } else {
            presenter.setCourseReviewedTrue(courseId)
        }
        startActivity(practiceCardsIntent)
    }

    private fun setPracticeCardsIntent(isDeleted: Boolean) {
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
