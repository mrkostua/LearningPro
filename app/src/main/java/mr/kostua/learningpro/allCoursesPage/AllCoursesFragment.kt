package mr.kostua.learningpro.allCoursesPage

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Message
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
import mr.kostua.learningpro.mainPage.MainPageFragment
import mr.kostua.learningpro.tools.ConstantValues
import java.lang.ref.WeakReference


/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
class AllCoursesFragment : FragmentInitializer<AllCoursesContract.Presenter>(), AllCoursesContract.View {
    private lateinit var coursesRecycleViewAdapter: RecycleViewAdapter<CourseDo>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_all_courses, container, false)
    }
    //TODO use CollapsingToolbarLayout after implementing backend of list
    //TODO during scrolling of recycleView the actionBar is hidden and after moving to first tab (actionBar is still hidden fix it)

    override fun initializeViews() {
        presenter.takeView(this)
        presenter.populateCourses()
        val uiHandler = CustomHandler(this)

    }

    override fun initializeRecycleView(data: List<CourseDo>) {
        setPBVisibility(false)
        rvAllCourses.visibility = View.VISIBLE
        coursesRecycleViewAdapter = RecycleViewAdapter(data, R.layout.course_row_item, object : ViewHolderBinder<CourseDo> {
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

    override fun setPBVisibility(visible: Boolean) {
        pbLoadCourses.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.disposeAll()
    }

    private fun updateRecycleView() {
        if (pbLoadCourses.visibility == View.GONE) {
            coursesRecycleViewAdapter.notifyDataSetChanged()

        }
    }

    private class CustomHandler(fragment: AllCoursesFragment) : Handler() {
        private val weakReference: WeakReference<AllCoursesFragment> = WeakReference(fragment)
        override fun handleMessage(msg: Message?) {
            val fragment = weakReference.get()
            if (fragment != null) {
                when (msg?.what) {
                    ConstantValues.UI_HANDLER_UPDATE_COURSES_LIST_MESSAGE -> fragment.updateRecycleView()
                }

            }
        }
    }
}