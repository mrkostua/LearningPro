package mr.kostua.learningpro.allCoursesPage

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import kotlinx.android.synthetic.main.custom_view_no_cards_dialog.view.*
import kotlinx.android.synthetic.main.fragment_all_courses.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.injections.scopes.FragmentScope
import mr.kostua.learningpro.practiceCards.PracticeCardsActivity
import mr.kostua.learningpro.questionsCardPreview.QuestionsCardsPreviewActivity
import mr.kostua.learningpro.tools.*
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
@FragmentScope
class AllCoursesFragment : FragmentInitializer<AllCoursesContract.Presenter>(), AllCoursesContract.View {
    @Inject
    lateinit var notificationTools: NotificationTools

    private val TAG = this.javaClass.simpleName
    private lateinit var coursesRecycleViewAdapter: AllCoursesRecycleViewAdapter
    private lateinit var finishedCorseDialog: AlertDialog
    private val courseItemClickListenerCDisposable = CompositeDisposable()
    private fun getStartPracticeActivityIntent() = Intent(fragmentContext, PracticeCardsActivity::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_courses, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        courseItemClickListenerCDisposable.clear()
        presenter.disposeAll()
    }

    override fun initializeViews() {
        presenter.takeView(this)
        presenter.populateCourses()
    }

    override fun initializeRecycleView(data: List<CourseDo>) {
        coursesRecycleViewAdapter = AllCoursesRecycleViewAdapter(data)
        presenter.subscribeCourseItemClick(coursesRecycleViewAdapter.getCourseItemObservable())

        rvAllCourses.run {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(fragmentContext)
            adapter = AlphaInAnimationAdapter(coursesRecycleViewAdapter)
        }
    }

    override fun isCourseListInitialized() = rvAllCourses.adapter != null

    /**
     * in presenter data variable was update(because here we using same variable no copy) this methods updates courses.
     */
    override fun updateCourseList() {
        rvAllCourses.adapter.notifyDataSetChanged()
    }

    override fun setPBVisibility(visible: Boolean) {
        pbLoadCourses.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun showFailedPopulationDialog() {
        notificationTools.showCustomAlertDialog(parentActivity,
                "Courses data problem",
                "Do you want to try again to retrieve data?",
                DialogInterface.OnClickListener { dialog, which ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> {
                            presenter.populateCourses()
                            dialog.dismiss()
                        }
                        DialogInterface.BUTTON_NEGATIVE -> {
                            dialog.dismiss()
                        }
                    }
                })
    }

    override fun createCourseFinishedDialog(courseId: Int) {
        val customDialogView = LayoutInflater.from(fragmentContext).inflate(R.layout.custom_view_no_cards_dialog,
                clFragmentAllCourses, false)
        finishedCorseDialog = AlertDialog.Builder(parentActivity, R.style.CustomAlertDialogStyle)
                .setView(customDialogView)
                .create()
        finishedCorseDialog.setSlideWindowAnimation()

        with(customDialogView) {
            rbFinishedCoursePracticeAgain.setOnClickListener { presenter.dialogButtonPracticeCourseAgainClickListener() }
            rbFinishedCourseShowAllCards.setOnClickListener { presenter.dialogButtonShowAllCardsClickListener() }
            bNoCardsDialogDo.setOnClickListener {
                it.postDelayed({
                    presenter.dialogButtonDo(courseId)
                }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
            }
            bNoCardsDialogBack.setOnClickListener {
                it.postDelayed({
                    finishedCorseDialog.dismiss()
                }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
            }
        }
        finishedCorseDialog.show()
    }

    override fun startPreviewActivity(courseId: Int) {
        startActivity(Intent(fragmentContext, QuestionsCardsPreviewActivity::class.java)
                .putExtra(ConstantValues.COURSE_ID_KEY, courseId)
                .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
    }

    override fun showToast(message: String) {
        notificationTools.showToastMessage(message)
    }

    override fun startActivityToShowAllCard(courseId: Int) {
        startActivity(getStartPracticeActivityIntent()
                .putExtra(ConstantValues.COURSE_ID_KEY, courseId)
                .putExtra(ConstantValues.SHOW_ALL_CARDS_KEY, true))
    }

    override fun dismissFinishedCourseDialog() {
        if (this::finishedCorseDialog.isInitialized) {
            finishedCorseDialog.dismiss()
        }
    }

    override fun startPracticeCardsActivity(courseId: Int) {
        startActivity(getStartPracticeActivityIntent()
                .putExtra(ConstantValues.COURSE_ID_KEY, courseId))
    }
}
