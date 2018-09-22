package mr.kostua.learningpro.practiceCards

import android.content.Intent
import android.support.constraint.ConstraintLayout
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
import mr.kostua.learningpro.tools.ShowLogs

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
        private var flippedItemId = -1

        init {
            tvCardQuestion.movementMethod = ScrollingMovementMethod()
            RxView.clicks(bFlipCardQuestionSide).subscribe {
                flipView(1000)
                flippedItemId = data[adapterPosition].id!!
                /* ++data[adapterPosition].viewsCount
                 notifyItemChanged(adapterPosition)
                 viewsCountPublishSubject.onNext(data[adapterPosition])*/
                /**
                 * //TODO bad blink during flip animation - invoked by notifyItemChanged (fix IT)
                 * Answer
                 * I think the answer will be to create Handler and after pushing flipView - sent message with delay(depends on the amount of text calculate it by size of text)
                 * or delay can depend also if user scroll to end of the textView.
                 * and send past adapterPosition to Handler, if current adapterPosition same -> do the update with some animation
                 *
                 * also add some security against multiple clicking on the button flip
                 */
            }

            tvCardAnswer.movementMethod = ScrollingMovementMethod()
            RxView.clicks(bFlipCardAnswerSide).subscribe {
                flippedItemId = -1
                flipView(1000)
            }

            RxView.clicks(ibEditCard).subscribe {
                with(view.context) {
                    startActivity(Intent(this, QuestionsCardsPreviewActivity::class.java)
                            .putExtra(ConstantValues.COURSE_ID_KEY, courseId)
                            .putExtra(ConstantValues.QUESTION_ID_KEY, data[adapterPosition].id!!))
                }
            }
            RxView.clicks(ibMarkCardAsDone).subscribe {
                data[adapterPosition].isLearned = true
                flippedItemId = -1
                ibMarkAsDonePublishSubject.onNext(data[adapterPosition])
                ibMarkAsDoneClickListener()
            }
        }

        fun bind(item: QuestionDo) {
            view.setBackgroundResource(0)
            when {
                flipViewPracticeCard.currentFlipState == EasyFlipView.FlipState.BACK_SIDE && item.id != flippedItemId -> {
                    flipView(0)
                }
                flipViewPracticeCard.currentFlipState == EasyFlipView.FlipState.FRONT_SIDE && item.id == flippedItemId -> {
                    flipView(0)
                }
            }
            tvCardQuestion.run {
                text = item.question
            }
            tvCardAnswer.run {
                text = item.answer
            }
            tvAnswerReadCount.text = item.viewsCount.toString()
        }

        private fun flipView(duration: Int) {
            flipViewPracticeCard.flipDuration = duration
            flipViewPracticeCard.flipTheView()
        }

        private fun ibMarkAsDoneClickListener() {
            data.removeAt(adapterPosition)
            view.setBackgroundResource(R.color.question_card_preview_accept_animation_color)
            /**
             * //TODO choose better color for learnedDone (or some animation firework)
             * try using Leonardis to make firework animation with your own drawable as particles
             */
            notifyItemRemoved(adapterPosition)
            notifyItemRangeChanged(0, data.size)
        } 
    }
}