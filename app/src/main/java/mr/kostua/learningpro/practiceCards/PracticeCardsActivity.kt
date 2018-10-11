package mr.kostua.learningpro.practiceCards

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.view.View
import io.reactivex.disposables.CompositeDisposable
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
    private val DELETE_ITEM_ANIMATION_TIME = 500L
    @Inject
    public lateinit var notificationTools: NotificationTools
    @Inject
    public lateinit var presenter: PracticeCardsContract.Presenter

    private lateinit var cardsRecycleViewAdapter: PracticeCardsRecycleViewAdapter
    private val cardsCompositeDisposables = CompositeDisposable()
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
        ShowLogs.log(TAG, "onNewIntent")
        super.onNewIntent(intent)
        if (this::cardsRecycleViewAdapter.isInitialized) {
            intent.getIntExtra(ConstantValues.COURSE_ITEM_ID_TO_FOCUS_KEY, -1).let {
                if (it != -1) {
                    scrollToPosition(it)
                }
            }
            intent.getIntExtra(ConstantValues.COURSE_ITEM_DELETED_ID_KEY, -1).let {
                if (it != -1) {
                    for ((index, value) in cardsRecycleViewAdapter.data.withIndex()) {
                        ShowLogs.log(TAG, "onNewIntent data $index ${value.id}")
                        if (value.id == it) {
                            ShowLogs.log(TAG, "onNewIntent notifyItemRemoved $index")
                            if (cardsRecycleViewAdapter.data.size == 1) {
                                lastCardDeleted(index)
                                return
                            }
                            cardsRecycleViewAdapter.data.removeAt(index)
                            cardsRecycleViewAdapter.notifyDataSetChanged()
                            break
                        }
                    }

                }
            }
        }
    }

    private fun lastCardDeleted(deletedItemPosition: Int) {
        showFireWorkAnimation(rvPracticeCards, ConstantValues.ALL_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS, 150)
        rvPracticeCards.postDelayed({
            cardsRecycleViewAdapter.notifyItemRemoved(deletedItemPosition)
            rvPracticeCards.postDelayed({
                finish()
            }, DELETE_ITEM_ANIMATION_TIME)
        }, ConstantValues.ALL_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS)

    }

    override fun onResume() {
        super.onResume()
        ShowLogs.log(TAG, "onResume with : courseId$courseId")
        //TODO here after onStop() we coming back and e.g. some card is still flipped and the user continue to study it (but the viewCounts are not changing at all) he is gonna be confused (not logic behaviour)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAll()
        cardsCompositeDisposables.clear()
    }

    override fun onPause() {
        super.onPause()
        setDoneQuestionsAmount()
    }

    override fun onStop() {
        super.onStop()
        if (this::cardsRecycleViewAdapter.isInitialized) {
            cardsRecycleViewAdapter.disableAllHandlerMessages()
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

    override fun initializeRecycleView(data: ArrayList<QuestionDo>) {
        cardsRecycleViewAdapter = PracticeCardsRecycleViewAdapter(data, courseId)
        cardsCompositeDisposables.addAll(
                cardsRecycleViewAdapter.getIBMarkAsDoneObservable().subscribe({
                    presenter.updateQuestion(it)
                    doneQuestionsAmount++
                    if (isLastQuestionCard(data.size)) {
                        showFireWorkAnimation(rvPracticeCards, ConstantValues.ALL_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS, 150)
                        rvPracticeCards.postDelayed({
                            setDoneQuestionsAmount()
                            finish()
                        }, ConstantValues.ALL_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS + DELETE_ITEM_ANIMATION_TIME)
                    } else {
                        showFireWorkAnimation(rvPracticeCards, ConstantValues.CARD_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS, 50)
                    }
                }, {
                    showToast("please try to \"mark as done\" this card again")
                }),
                cardsRecycleViewAdapter.getViewsCountPublishSubject().subscribe({
                    presenter.updateViewCountOfCard(it)
                }, {

                }))
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

    private fun scrollToPosition(cardId: Int) {
        cardsRecycleViewAdapter.data.forEachIndexed { index, questionDo ->
            if (questionDo.id == cardId) {
                rvPracticeCards.layoutManager.scrollToPosition(index)
                ShowLogs.log(TAG, "onNewIntent scrollToPosition($index)")

            }
        }
    }

    private fun isLastQuestionCard(dataSize: Int) = dataSize == 1

    private fun setPBVisibility(isVisible: Boolean) {
        pbPracticeCards.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showToast(text: String) {
        notificationTools.showToastMessage(text)
    }

    override fun goBack() {
        finish()
    }
}
