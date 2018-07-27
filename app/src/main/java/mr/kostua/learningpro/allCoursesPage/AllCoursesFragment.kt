package mr.kostua.learningpro.allCoursesPage

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
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
        rvAllCourses.visibility = View.VISIBLE
        coursesRecycleViewAdapter = RecycleViewAdapter(data, R.layout.course_row_item, object : ViewHolderBinder<CourseDo> {
            private lateinit var tvCourseTitle: TextView
            private lateinit var tvCourseDescription: TextView
            private lateinit var tvCourseQuestionsAmount: TextView
            private lateinit var tvDoneQuestionsAmount: TextView
            private lateinit var pbDoneQuestionsAmount: ProgressBar

            override fun initializeListViews(view: View) = with(view) {
                tvCourseTitle = findViewById(R.id.tvCourseTitle)
                tvCourseDescription = findViewById(R.id.tvCourseDescription)
                tvCourseQuestionsAmount = findViewById(R.id.tvCourseQuestionsAmount)
                tvDoneQuestionsAmount = findViewById(R.id.tvDoneQuestionsAmount)
                pbDoneQuestionsAmount = findViewById(R.id.pbDoneQuestionsAmount)

                view.setOnClickListener {
                    val courseId = data[rvAllCourses.getChildLayoutPosition(it)].id
                    if (courseId != null) {
                        startActivity(Intent(fragmentContext, QuestionsCardsPreviewActivity::class.java)
                                .putExtra(ConstantValues.CONTINUE_COURSE_CREATION_COURSE_ID_KEY, courseId))
                        //TODO also put extra reviewed (after CourseDo update)
                    }
                }
            }

            @SuppressLint("SetTextI18n")
            override fun bind(item: CourseDo) {
                with(item) {
                    tvCourseTitle.text = title
                    tvCourseDescription.text = description

                    tvCourseQuestionsAmount.text = questionsAmount.toString()
                    tvDoneQuestionsAmount.text = if (doneQuestionsAmount == 0 || questionsAmount == 0) "0 %"
                    else "${(doneQuestionsAmount * 100) / questionsAmount} %"

                    pbDoneQuestionsAmount.max = questionsAmount
                    pbDoneQuestionsAmount.progress = doneQuestionsAmount
                }
            }
        })
        rvAllCourses.layoutManager = LinearLayoutManager(fragmentContext)
        rvAllCourses.adapter = coursesRecycleViewAdapter
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