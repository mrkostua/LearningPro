package mr.kostua.learningpro.allCoursesPage

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.CourseDo
import mr.kostua.learningpro.tools.ShowLogs

/**
 * @author Kostiantyn Prysiazhnyi on 8/10/2018.
 */
class AllCoursesRecycleViewAdapter(private val data: List<CourseDo>) : RecyclerView.Adapter<AllCoursesRecycleViewAdapter.ViewHolder>() {
    private val TAG = this.javaClass.simpleName
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.course_row_item, parent, false))
    }

    private val coursePublishSubject = PublishSubject.create<CourseDo>()
    fun getCourseItemObservable(): Observable<CourseDo> = coursePublishSubject.hide()


    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("CheckResult")
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivNotReviewedAlert: ImageView = view.findViewById(R.id.ivNotReviewedAlert)
        private val tvCourseTitle: TextView = view.findViewById(R.id.tvCourseTitle)
        private val tvCourseDescription: TextView = view.findViewById(R.id.tvCourseDescription)
        private val tvCourseQuestionsAmount: TextView = view.findViewById(R.id.tvCourseQuestionsAmount)
        private val tvDoneQuestionsAmount: TextView = view.findViewById(R.id.tvDoneQuestionsAmount)
        private val pbDoneQuestionsAmount: ProgressBar = view.findViewById(R.id.pbDoneQuestionsAmount)
        private var doneQuestions = 0
        private val notReviewedAnimation = AlphaAnimation(0.0f, 1.0f).apply {
            duration = 700
            startOffset = 20
            repeatMode = Animation.REVERSE
            repeatCount = Animation.INFINITE
        }

        init {
            RxView.clicks(view).subscribe { coursePublishSubject.onNext(data[adapterPosition]) }

        }

        fun bind(item: CourseDo) {
            ShowLogs.log(TAG, "bind() courseName : ${item.title} - questionsAmount ${item.questionsAmount} - doneQuestionsAmount : ${item.doneQuestionsAmount}")
            with(item) {
                ivNotReviewedAlert.run {
                    if (item.reviewed) {
                        visibility = View.GONE
                        clearAnimation()
                    } else {
                        visibility = View.VISIBLE
                        startAnimation(notReviewedAnimation)
                    }
                }
                tvCourseTitle.run {
                    text = title
                }
                tvCourseDescription.run {
                    text = description
                }
                tvCourseQuestionsAmount.run {
                    text = questionsAmount.toString()
                }
                tvDoneQuestionsAmount.run {
                    doneQuestions = if (doneQuestionsAmount == 0 || questionsAmount == 0) 0
                    else (doneQuestionsAmount * 100) / questionsAmount
                    text = if (doneQuestions > 100) "100%" else "$doneQuestions%"
                }
                pbDoneQuestionsAmount.run {
                    max = questionsAmount
                    progress = doneQuestionsAmount
                }
            }
        }

    }
}