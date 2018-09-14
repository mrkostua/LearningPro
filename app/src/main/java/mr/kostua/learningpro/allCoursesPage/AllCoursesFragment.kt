package mr.kostua.learningpro.allCoursesPage

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
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
    private lateinit var coursesRecycleViewAdapter: AllCoursesRecycleViewAdapter
    @Inject
    lateinit var notificationTools: NotificationTools

    private val courseItemClickListenerCDisposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_courses, container, false)
    }

    override fun initializeViews() {
        presenter.takeView(this)
        presenter.populateCourses()
    }

    override fun initializeRecycleView(data: List<CourseDo>) {
        coursesRecycleViewAdapter = AllCoursesRecycleViewAdapter(data)
        courseItemClickListenerCDisposable.add(coursesRecycleViewAdapter.getCourseItemObservable().subscribe {
            with(it) {
                if (!this.reviewed) {
                    startActivity(Intent(fragmentContext, QuestionsCardsPreviewActivity::class.java)
                            .putExtra(ConstantValues.CONTINUE_COURSE_CREATION_COURSE_ID_KEY, this.id!!))
                } else {
                    startActivity(Intent(fragmentContext, PracticeCardsActivity::class.java)
                            .putExtra(ConstantValues.COURSE_ID_TO_PRACTICE_KEY, this.id!!))
                }
            }
        })
        rvAllCourses.run {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(fragmentContext)
            adapter = AlphaInAnimationAdapter(coursesRecycleViewAdapter)
        }
    }

    override fun isCourseListInitialized() = rvAllCourses.adapter != null

    override fun updateCourseList(courses: List<CourseDo>) {
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
}