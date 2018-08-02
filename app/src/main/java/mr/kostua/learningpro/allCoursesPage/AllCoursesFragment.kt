package mr.kostua.learningpro.allCoursesPage

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_all_courses.*
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.injections.scopes.FragmentScope
import mr.kostua.learningpro.questionsCardPreview.QuestionsCardsPreviewActivity
import mr.kostua.learningpro.tools.*
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
@FragmentScope
class AllCoursesFragment : FragmentInitializer<AllCoursesContract.Presenter>(), AllCoursesContract.View {
    private lateinit var coursesRecycleViewAdapter: RecycleViewAdapter<CourseDo>
    @Inject
    lateinit var notificationTools: NotificationTools

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_courses, container, false)
    }

    override fun initializeViews() {
        presenter.takeView(this)
        presenter.populateCourses()
    }

    override fun initializeRecycleView(data: List<CourseDo>) {
        val animation = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 700
            startOffset = 20
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }
        coursesRecycleViewAdapter = RecycleViewAdapter(data, R.layout.course_row_item, object : ViewHolderBinder<CourseDo> {
            override fun bind(view: View, item: CourseDo) {
                with(item) {
                    with(view) {
                        setOnClickListener {
                            val courseId = item.id
                            if (courseId != null) {
                                if (!item.reviewed) {
                                    startActivity(Intent(fragmentContext, QuestionsCardsPreviewActivity::class.java)
                                            .putExtra(ConstantValues.CONTINUE_COURSE_CREATION_COURSE_ID_KEY, courseId))
                                }
                            }
                        }
                        findViewById<ImageView>(R.id.ivNotReviewedAlert).run {
                            if (item.reviewed) {
                                visibility = View.GONE
                                clearAnimation()
                            } else {
                                visibility = View.VISIBLE
                                startAnimation(animation)
                            }
                        }
                        findViewById<TextView>(R.id.tvCourseTitle).run {
                            text = title
                        }
                        findViewById<TextView>(R.id.tvCourseDescription).run {
                            text = description
                        }
                        findViewById<TextView>(R.id.tvCourseQuestionsAmount).run {
                            text = questionsAmount.toString()
                        }
                        findViewById<TextView>(R.id.tvDoneQuestionsAmount).run {
                            text = if (doneQuestionsAmount == 0 || questionsAmount == 0) "0 %"
                            else "${(doneQuestionsAmount * 100) / questionsAmount} %"
                        }
                        findViewById<ProgressBar>(R.id.pbDoneQuestionsAmount).run {
                            max = questionsAmount
                            progress = doneQuestionsAmount
                        }
                    }
                }
            }
        })
        rvAllCourses.run {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(fragmentContext)
            adapter = coursesRecycleViewAdapter
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
        presenter.disposeAll()
    }
}