package mr.kostua.learningpro.practiceCards

import android.content.Intent
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.wajahatkarim3.easyflipview.EasyFlipView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.questionsCardPreview.QuestionsCardsPreviewActivity
import mr.kostua.learningpro.tools.ConstantValues

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
class PracticeCardsRecycleViewAdapter(private val data: ArrayList<QuestionDo>, private val courseId: Int) : RecyclerView.Adapter<PracticeCardsRecycleViewAdapter.ViewHolder>() {
    private val TAG = this.javaClass.simpleName
    private val ibMarkAsDonePublishSubject = PublishSubject.create<QuestionDo>()
    fun getIBMarkAsDoneObservable(): Observable<QuestionDo> = ibMarkAsDonePublishSubject.hide()
    private val viewsCountPublishSubject = PublishSubject.create<QuestionDo>()
    fun getViewsCountPublishSubject(): Observable<QuestionDo> = viewsCountPublishSubject.hide()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.practice_card_row_item, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val clQuestionItem: ConstraintLayout = view.findViewById(R.id.iQuestionSideCard)
        private val tvCardQuestion: TextView = clQuestionItem.findViewById(R.id.tvCardQuestion)
        private val bFlipCardQuestionSide: Button = clQuestionItem.findViewById(R.id.bFlipCardQuestionSide)


        private val clAnswerItem: ConstraintLayout = view.findViewById(R.id.iAnswerSideCard)
        private val tvCardAnswer: TextView = clAnswerItem.findViewById(R.id.tvCardAnswer)
        private val ibMarkCardAsDone: ImageButton = clAnswerItem.findViewById(R.id.ibMarkCardAsDone)
        private val ibEditCard: ImageButton = clAnswerItem.findViewById(R.id.ibEditCard)
        private val tvAnswerReadCount: TextView = clAnswerItem.findViewById(R.id.tvAnswerReadCount)
        private val bFlipCardAnswerSide: Button = clAnswerItem.findViewById(R.id.bFlipCardAnswerSide)

        private val flipViewPracticeCard: EasyFlipView = view.findViewById(R.id.flipViewPracticeCard)

        init {
            tvCardQuestion.movementMethod = ScrollingMovementMethod()
            RxView.clicks(bFlipCardQuestionSide).subscribe {
                ++data[adapterPosition].viewsCount
                notifyItemChanged(adapterPosition)
                viewsCountPublishSubject.onNext(data[adapterPosition])
                flipView() //TODO bad blink during flip animation - invoked by notifyItemChanged (fix IT)
            }

            tvCardAnswer.movementMethod = ScrollingMovementMethod()
            RxView.clicks(bFlipCardAnswerSide).subscribe {
                flipView()
            }

            RxView.clicks(ibEditCard).subscribe {
                with(view.context) {
                    startActivity(Intent(this, QuestionsCardsPreviewActivity::class.java) //TODO add some animation for moving ot another activity (read more)
                            .putExtra(ConstantValues.COURSE_ID_KEY, courseId)
                            .putExtra(ConstantValues.COURSE_ITEM_ID_KEY, data[adapterPosition].id!!))
                }
            }
            RxView.clicks(ibMarkCardAsDone).subscribe {
                data[adapterPosition].isLearned = true
                ibMarkAsDonePublishSubject.onNext(data[adapterPosition])
                ibMarkAsDoneClickListener()
            }
        }

        fun bind(item: QuestionDo) { //TODO after deleting one item (some other item is flipped) fix it maybe by if else inside bind() as if flipped flip back with duration 0
            view.setBackgroundResource(0)
            tvCardQuestion.run {
                text = item.question
            }
            tvCardAnswer.run {
                text = item.answer
            }
            tvAnswerReadCount.text = item.viewsCount.toString()
        }

        private fun flipView() {
            flipViewPracticeCard.flipDuration = 1000
            flipViewPracticeCard.flipTheView()
        }

        private fun ibMarkAsDoneClickListener() {
            data.removeAt(adapterPosition)
            view.setBackgroundResource(R.color.question_card_preview_accept_animation_color) //TODO choose better color for learnedDone (or some animation firework)
            notifyItemRemoved(adapterPosition)
            notifyItemRangeChanged(0, data.size)
        }
    }
}