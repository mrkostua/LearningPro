package mr.kostua.learningpro.questionsCardPreview

import android.annotation.SuppressLint
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
import mr.kostua.learningpro.tools.*
import javax.inject.Inject


@ActivityScope
class QuestionsCardsPreviewActivity : BaseDaggerActivity(), QuestionCardsPreviewContract.View {
    @Inject
    public lateinit var notificationTools: NotificationTools
    @Inject
    public lateinit var presenter: QuestionCardsPreviewContract.Presenter

    private lateinit var questionsRecycleViewAdapter: QuestionCardsPreviewRecycleViewAdapter
    private val questionCardsCompositeDisposables = CompositeDisposable()
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

    @SuppressLint("SetTextI18n")
    override fun initializeRecycleView(data: ArrayList<QuestionDo>) {
        questionsRecycleViewAdapter = QuestionCardsPreviewRecycleViewAdapter(data, this)
        questionCardsCompositeDisposables.addAll(
                questionsRecycleViewAdapter.getButtonAcceptObservable().subscribe {
                    presenter.acceptQuestion(it)
                    if (data.size == 1) { //TODO test it or find more elegant solution
                        questionsPreviewFinished()
                    }
                },
                questionsRecycleViewAdapter.getButtonSaveObservable().subscribe {
                    presenter.updateQuestion(it)
                },
                questionsRecycleViewAdapter.getButtonDeleteObservable().subscribe {
                    presenter.deleteQuestion(it, courseId)
                    if (data.size == 1) {
                        questionsPreviewFinished()
                    }
                })
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

    private fun questionsPreviewFinished() {
        presenter.setCourseReviewedTrue(courseId)
        //TODO start course learning activity
    }


    override fun showToast(text: String) {
        notificationTools.showToastMessage(text)
    }
}
