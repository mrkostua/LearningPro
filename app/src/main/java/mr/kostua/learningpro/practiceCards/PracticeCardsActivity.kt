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
    @Inject
    public lateinit var notificationTools: NotificationTools
    @Inject
    public lateinit var presenter: PracticeCardsContract.Presenter

    private lateinit var cardsRecycleViewAdapter: PracticeCardsRecycleViewAdapter
    private val cardsCompositeDisposables = CompositeDisposable()
    private var courseId = -1

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

    private fun initializeViews() {
        courseId = intent.getIntExtra(ConstantValues.COURSE_ID_TO_PRACTICE_KEY, -1)
        presenter.takeView(this)
        presenter.populateAllCards(courseId)
    }

    override fun initializeRecycleView(data: ArrayList<QuestionDo>) {
        cardsRecycleViewAdapter = PracticeCardsRecycleViewAdapter(data, this)
        cardsCompositeDisposables.addAll(
                /*cardsRecycleViewAdapter.getIBEditCardObservable().subscribe({
                    // in edit button we didn't send anything only after save -> so change it after designing editing way
                }, {

                }),*/
                cardsRecycleViewAdapter.getIBMarkAsDoneObservable().subscribe({
                    //presenter.updateQuestion(it)
                }, {
                    showToast("please try to \"mark as done\" this card again")
                    //TODO make some dialog in the future like send a report about bug

                }))
        pbPracticeCards.visibility = View.GONE
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
}
