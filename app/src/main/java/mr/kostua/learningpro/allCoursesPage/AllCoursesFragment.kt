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
    private val TAG = this.javaClass.simpleName
    private lateinit var coursesRecycleViewAdapter: AllCoursesRecycleViewAdapter
    @Inject
    lateinit var notificationTools: NotificationTools

    private val courseItemClickListenerCDisposable = CompositeDisposable()
    private fun getStartPracticeActivityIntent() = Intent(fragmentContext, PracticeCardsActivity::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_courses, container, false)
    }

    override fun onResume() {
        super.onResume()
        ShowLogs.log(TAG, "onResume")
    }

    override fun initializeViews() {
        presenter.takeView(this)
        presenter.populateCourses()
    }

    override fun initializeRecycleView(data: List<CourseDo>) {
        coursesRecycleViewAdapter = AllCoursesRecycleViewAdapter(data)
        courseItemClickListenerCDisposable.add(coursesRecycleViewAdapter.getCourseItemObservable().subscribe {
            presenter.saveLastOpenedCourseId(it.id!!)
            rvAllCourses.postDelayed({
                if (it.reviewed) {
                    if (it.questionsAmount == it.doneQuestionsAmount) {
                        createCourseFinishedDialog(it.id!!)
                    } else {
                        startActivity(getStartPracticeActivityIntent()
                                .putExtra(ConstantValues.COURSE_ID_KEY, it.id!!))
                    }
                } else {
                    startActivity(Intent(fragmentContext, QuestionsCardsPreviewActivity::class.java)
                            .putExtra(ConstantValues.COURSE_ID_KEY, it.id!!)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
                }
            }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
        })
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

    override fun onDestroy() {
        super.onDestroy()
        courseItemClickListenerCDisposable.clear()
        presenter.disposeAll()
    }

    private fun createCourseFinishedDialog(courseId: Int) {
        val customDialogView = LayoutInflater.from(fragmentContext).inflate(R.layout.custom_view_no_cards_dialog, clFragmentAllCourses, false)
        val dialog = AlertDialog.Builder(parentActivity, R.style.CustomAlertDialogStyle)
                .setView(customDialogView)
                .create()
        dialog.setSlideWindowAnimation()

        var checkedRadioButtonId = -1
        with(customDialogView) {
            rbFinishedCoursePracticeAgain.setOnClickListener { checkedRadioButtonId = it.id }
            rbFinishedCourseShowAllCards.setOnClickListener { checkedRadioButtonId = it.id }
            bNoCardsDialogDo.setOnClickListener {
                when (checkedRadioButtonId) {
                    rbFinishedCoursePracticeAgain.id -> {
                        it.postDelayed({
                            presenter.startLearningCourseAgain(courseId)
                            dialog.dismiss()
                        }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
                    }
                    rbFinishedCourseShowAllCards.id -> {
                        it.postDelayed({
                            startActivity(getStartPracticeActivityIntent()
                                    .putExtra(ConstantValues.COURSE_ID_KEY, courseId)
                                    .putExtra(ConstantValues.SHOW_ALL_CARDS_KEY, true))
                            dialog.dismiss()
                        }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
                    }
                    else -> {
                        notificationTools.showToastMessage("No actions chosen, please " +
                                "choose action and press \"Do\"")
                    }
                }
            }
            bNoCardsDialogBack.setOnClickListener {
                it.postDelayed({
                    dialog.dismiss()
                }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
            }
        }
        dialog.show()
    }

    override fun startPracticeCardsActivity(courseId: Int) {
        startActivity(getStartPracticeActivityIntent()
                .putExtra(ConstantValues.COURSE_ID_KEY, courseId))
    }
}
