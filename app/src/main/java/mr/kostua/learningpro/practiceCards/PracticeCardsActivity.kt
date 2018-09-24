package mr.kostua.learningpro.practiceCards

import android.annotation.SuppressLint
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
import javax.inject.Inject

class PracticeCardsActivity : BaseDaggerActivity(), PracticeCardsContract.View {
    private val TAG = this.javaClass.simpleName
    @Inject
    public lateinit var notificationTools: NotificationTools
    @Inject
    public lateinit var presenter: PracticeCardsContract.Presenter

    private lateinit var cardsRecycleViewAdapter: PracticeCardsRecycleViewAdapter
    private val cardsCompositeDisposables = CompositeDisposable()
    private var courseId = -1
    private var editedCourseItemId = -1
    private fun isStartedAfterEditing() = editedCourseItemId != -1
    private var doneQuestionsAmount = 0
    private var isShowAllCards = false
    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_practice_cards)
        initializeViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAll()
        cardsCompositeDisposables.clear()
    }

    override fun onPause() {
        super.onPause()
        if (courseId != -1 && doneQuestionsAmount != 0) {
            setDoneQuestionsAmount(courseId)
        }
    }

    private fun setDoneQuestionsAmount(courseId: Int) {
        if (!isShowAllCards) {
            presenter.increaseCourseDoneQuestionsAmountBy(courseId, doneQuestionsAmount)
            doneQuestionsAmount = 0
        }
    }

    private fun initializeViews() {
        courseId = intent.getIntExtra(ConstantValues.COURSE_ID_KEY, -1)
        editedCourseItemId = intent.getIntExtra(ConstantValues.COURSE_ITEM_ID_TO_FOCUS_KEY, -1)
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
                        setDoneQuestionsAmount(courseId)
                        finish() //TODO some animation  some additional extra firework animation
                    }
                }, {
                    showToast("please try to \"mark as done\" this card again")
                    //TODO make some dialog in the future like send a report about bug
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
            if (isStartedAfterEditing()) {
                data.forEachIndexed { index, questionDo ->
                    if (questionDo.id == editedCourseItemId) {
                        rvPracticeCards.layoutManager.scrollToPosition(index)
                    }
                }
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
