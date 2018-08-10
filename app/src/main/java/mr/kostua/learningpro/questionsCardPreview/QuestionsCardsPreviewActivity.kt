package mr.kostua.learningpro.questionsCardPreview

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.view.View
import io.reactivex.disposables.CompositeDisposable
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

    //TODO hide keyboard (appears on activity start and RV initialization)
    //TODO add item accepted Animation and item deleted animation
    //TODO buttons flip animation ERROR
    @SuppressLint("SetTextI18n")
    override fun initializeRecycleView(data: ArrayList<QuestionDo>) {
        questionsRecycleViewAdapter = QuestionCardsPreviewRecycleViewAdapter(data, this)
        questionCardsCompositeDisposables.addAll(
                questionsRecycleViewAdapter.getButtonAcceptObservable().subscribe {
                    presenter.acceptQuestion(it)
                },
                questionsRecycleViewAdapter.getButtonSaveObservable().subscribe {
                    presenter.updateQuestion(it)
                },
                questionsRecycleViewAdapter.getButtonDeleteObservable().subscribe {
                    presenter.deleteQuestion(it, courseId)
                })
        pbQuestionsPreview.visibility = View.GONE
        rvQuestionsPreview.run {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@QuestionsCardsPreviewActivity,
                    LinearLayoutManager.HORIZONTAL, false)
            adapter = questionsRecycleViewAdapter
            PagerSnapHelper().attachToRecyclerView(this)//pager or linear ??
        }
    }


    override fun showToast(text: String) {
        notificationTools.showToastMessage(text)
    }
}
