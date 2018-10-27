package mr.kostua.learningpro.practiceCards

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.view.View
import jp.wasabeef.recyclerview.animators.FadeInDownAnimator
import kotlinx.android.synthetic.main.activity_practice_cards.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.main.BaseDaggerActivity
import mr.kostua.learningpro.tools.ConstantValues
import mr.kostua.learningpro.tools.NotificationTools
import mr.kostua.learningpro.tools.ShowLogs
import mr.kostua.learningpro.tools.showFireWorkAnimation
import javax.inject.Inject

class PracticeCardsActivity : BaseDaggerActivity(), PracticeCardsContract.View {
    private val TAG = this.javaClass.simpleName
    @Inject
    public lateinit var notificationTools: NotificationTools
    @Inject
    public lateinit var presenter: PracticeCardsContract.Presenter

    private lateinit var cardsRecycleViewAdapter: PracticeCardsRecycleViewAdapter
    private var courseId = -1
    private var doneQuestionsAmount = 0
    private var isShowAllCards = false

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_practice_cards)
        ShowLogs.log(TAG, "onCreate()")
        initializeViews()
    }

    /**
     * calls when started with FLAG_ACTIVITY_REORDER_TO_FRONT (no onCreate called)
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        with(intent.getBooleanExtra(ConstantValues.COURSE_ITEM_EDITED_KEY, false)) {
            if (this && courseId != -1) {
                presenter.handleItemEditedIntent(courseId)
            }
        }
        with(intent.getIntExtra(ConstantValues.COURSE_ITEM_ID_TO_FOCUS_KEY, -1)) {
            if (this != -1) {
                focusOnCardWithId(this)
            }
        }
        with(intent.getIntExtra(ConstantValues.COURSE_ITEM_DELETED_ID_KEY, -1)) {
            if (this != -1) {
                presenter.handleItemDeletedIntent(this)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        setDoneQuestionsAmount()
    }

    override fun onStop() {
        super.onStop()
        ShowLogs.log(TAG, "onStop")
        disableCardViewCountsUpdater()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAll()
    }

    override fun lastCardDeleted(deletedItemPosition: Int) {
        showFireWorkAnimation(rvPracticeCards, ConstantValues.ALL_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS, 150)
        rvPracticeCards.postDelayed({
            cardsRecycleViewAdapter.notifyItemRemoved(deletedItemPosition)
            rvPracticeCards.postDelayed({
                finish()
            }, DELETE_ITEM_ANIMATION_TIME)
        }, ConstantValues.ALL_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS)
    }

    override fun notifyDataSetChangedAdapter() {
        rvPracticeCards.adapter.notifyDataSetChanged()
    }

    override fun markAsDone(isLastCard: Boolean) {
        doneQuestionsAmount++
        if (isLastCard) {
            showFireWorkAnimation(rvPracticeCards, ConstantValues.ALL_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS, 150)
            rvPracticeCards.postDelayed({
                setDoneQuestionsAmount()
                finish()
            }, ConstantValues.ALL_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS + PracticeCardsActivity.DELETE_ITEM_ANIMATION_TIME)
        } else {
            showFireWorkAnimation(rvPracticeCards, ConstantValues.CARD_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS, 50)
        }
    }

    override fun initializeRecycleView(data: ArrayList<QuestionDo>) {
        cardsRecycleViewAdapter = PracticeCardsRecycleViewAdapter(data, courseId)
        presenter.subscribeToMarkAsDoneButton(cardsRecycleViewAdapter.getIBMarkAsDoneObservable())
        presenter.subscribeToMarkAsDoneButton(cardsRecycleViewAdapter.getViewsCountPublishSubject())
        setPBVisibility(false)
        rvPracticeCards.run {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@PracticeCardsActivity, LinearLayoutManager.HORIZONTAL, false)
            itemAnimator = FadeInDownAnimator().apply {
                removeDuration = 600
                changeDuration = 250
                moveDuration = 300
            }
            adapter = cardsRecycleViewAdapter
            PagerSnapHelper().attachToRecyclerView(this)
        }
    }

    override fun showToast(text: String) {
        notificationTools.showToastMessage(text)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        disableCardViewCountsUpdater()
    }

    override fun goBack() {
        ShowLogs.log(TAG, "goBack")
        disableCardViewCountsUpdater()
        finish()
    }

    private fun focusOnCardWithId(cardId: Int) {
        presenter.getCardPositionInList(cardId).run {
            if (this != -1) {
                scrollToPosition(this)
                cardsRecycleViewAdapter.sendDelayMessageViewCounts(this, ConstantValues.UPDATE_VIEW_COUNTS_SHORT_TIMER_MS)
            }
        }
    }

    private fun disableCardViewCountsUpdater() {
        if (this::cardsRecycleViewAdapter.isInitialized) {
            cardsRecycleViewAdapter.disableAllHandlerMessages()
            ShowLogs.log(TAG, "disableCardViewCountsUpdater() cardsRecycleViewAdapter initialized = true")
        }
    }

    private fun initializeViews(intent: Intent = getIntent()) {
        courseId = intent.getIntExtra(ConstantValues.COURSE_ID_KEY, -1)
        isShowAllCards = intent.getBooleanExtra(ConstantValues.SHOW_ALL_CARDS_KEY, false)
        presenter.takeView(this)
        if (isShowAllCards) {
            presenter.populateAllCards(courseId)
        } else {
            presenter.populateNotLearnedCards(courseId)
        }
    }

    private fun setDoneQuestionsAmount() {
        if (courseId != -1 && doneQuestionsAmount != 0) {
            if (!isShowAllCards) {
                presenter.increaseCourseDoneQuestionsAmountBy(courseId, doneQuestionsAmount)
                doneQuestionsAmount = 0
            }
        }
    }

    private fun scrollToPosition(position: Int) {
        rvPracticeCards.layoutManager.scrollToPosition(position)
        ShowLogs.log(TAG, "onNewIntent scrollToPosition($position)")
    }

    private fun setPBVisibility(isVisible: Boolean) {
        pbPracticeCards.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    companion object {
        private const val DELETE_ITEM_ANIMATION_TIME = 500L
    }
}
