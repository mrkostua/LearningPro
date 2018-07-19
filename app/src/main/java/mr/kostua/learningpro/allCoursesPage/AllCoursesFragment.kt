package mr.kostua.learningpro.allCoursesPage

import android.annotation.SuppressLint
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
import mr.kostua.learningpro.tools.FragmentInitializer
import mr.kostua.learningpro.tools.RecycleViewAdapter
import mr.kostua.learningpro.tools.ViewHolderBinder
import android.text.method.ScrollingMovementMethod


/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
class AllCoursesFragment : FragmentInitializer<AllCoursesContract.Presenter>(), AllCoursesContract.View {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_courses, container, false)
    }
    //TODO use CollapsingToolbarLayout after implementing backend of list
    //TODO during scrolling of recycleView the actionBar is hidden and after moving to first tab (actionBar is still hidden fix it)

    override fun initializeViews() {
        presenter.takeView(this)
        presenter.populateCourses()
    }

    override fun initializeRecycleView(data: List<CourseDo>) {
        setPBVisibility(false)
        rvAllCourses.visibility = View.VISIBLE
        val adapter = RecycleViewAdapter(data, R.layout.course_row_item, object : ViewHolderBinder<CourseDo> {
            private lateinit var tvCourseTitle: TextView
            private lateinit var tvCourseDescription: TextView
            private lateinit var tvCourseQuestionsAmount: TextView
            private lateinit var tvDoneQuestionsAmount: TextView
            private lateinit var pbDoneQuestionsAmount: ProgressBar
            private val scrollingMovementMethod = ScrollingMovementMethod()

            //TODO think about scrolling textView inside of RecycleView maybe it's so obvious and useful
            override fun initializeViews(view: View) = with(view) {
                tvCourseTitle = findViewById(R.id.tvCourseTitle)
                tvCourseDescription = findViewById(R.id.tvCourseDescription)
                tvCourseQuestionsAmount = findViewById(R.id.tvCourseQuestionsAmount)
                tvDoneQuestionsAmount = findViewById(R.id.tvDoneQuestionsAmount)
                pbDoneQuestionsAmount = findViewById(R.id.pbDoneQuestionsAmount)

                view.setOnClickListener {
                    val item: Int = rvAllCourses.getChildLayoutPosition(it)
                    Toast.makeText(fragmentContext, "HelloBro $item  ", Toast.LENGTH_LONG).show()
                }
            }

            @SuppressLint("SetTextI18n")
            override fun bind(item: CourseDo) {
                with(item) {
                    tvCourseTitle.text = title
                    tvCourseDescription.text = description
                    tvCourseDescription.movementMethod = scrollingMovementMethod


                    tvCourseQuestionsAmount.text = questionsAmount.toString()
                    tvDoneQuestionsAmount.text = "${(doneQuestionsAmount * 100) / questionsAmount} %"
                    pbDoneQuestionsAmount.max = questionsAmount
                    pbDoneQuestionsAmount.progress = doneQuestionsAmount
                }

            }
        })
        rvAllCourses.layoutManager = LinearLayoutManager(fragmentContext)
        rvAllCourses.adapter = adapter


    }

    override fun setPBVisibility(visible: Boolean) {
        pbLoadCourses.visibility = if (visible) View.VISIBLE else View.GONE
    }
}