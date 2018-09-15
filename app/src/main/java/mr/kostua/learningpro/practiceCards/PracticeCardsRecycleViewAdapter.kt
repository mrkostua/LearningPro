package mr.kostua.learningpro.practiceCards

import android.animation.ObjectAnimator
import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.tools.ShowLogs

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
class PracticeCardsRecycleViewAdapter(private val data: ArrayList<QuestionDo>, private val context: Context) : RecyclerView.Adapter<PracticeCardsRecycleViewAdapter.ViewHolder>() {
    private val TAG = this.javaClass.simpleName
    private val ibMarkAsDonePublishSubject = PublishSubject.create<QuestionDo>()
    fun getIBMarkAsDoneObservable(): Observable<QuestionDo> = ibMarkAsDonePublishSubject.hide()

/*    private val ibEditCardPublishSubject = PublishSubject.create<QuestionDo>()
    fun getIBEditCardObservable(): Observable<QuestionDo> = ibEditCardPublishSubject.hide()*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.practice_card_row_item, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        /**
         * TODO today tasks :
         * Edit button -> after save how to handle it maybe open another activity as QuestionsPreview
         * 2 bFlip animation for item
         * 3 design of answer side (also change flip bg)
         * 4 use this library for perfect flipping
         * https://github.com/wajahatkarim3/EasyFlipView
         *
         */
        private val tvCardQuestionOrAnswer: TextView = view.findViewById(R.id.tvCardQuestionOrAnswer)
        private val ibMarkCardAsDone: ImageButton = view.findViewById(R.id.ibMarkCardAsDone)
        private val ibEditCard: ImageButton = view.findViewById(R.id.ibEditCard)
        private val tvAnswerReadCount: TextView = view.findViewById(R.id.tvAnswerReadCount)
        private val ivAnswerReadCountIcon: ImageView = view.findViewById(R.id.ivAnswerReadCountIcon)
        private val ivQuestionCardSign: ImageView = view.findViewById(R.id.ivQuestionCardSign)

        private val bFlipCard: Button = view.findViewById(R.id.bFlipCard)
        private var flippedItemId = -1

        init {
            tvCardQuestionOrAnswer.movementMethod = ScrollingMovementMethod()
            RxView.clicks(bFlipCard).subscribe {
                bFlipClickListener()
            }

            RxView.clicks(ibEditCard).subscribe {
                //ibEditCardPublishSubject.onNext(data[adapterPosition])
                ibEditCardClickListener()
                //TODO start QuestionsPreviewActivity with 2 intExtras -> indicating to populate courseId with one item (questionID)
                //and after accepting (so it must be set also to not accept before populating) we check again is it questionPA with one item and moves back to our PracticeActivity
            }
            RxView.clicks(ibMarkCardAsDone).subscribe {
                ibMarkAsDonePublishSubject.onNext(data[adapterPosition])
                ibMarkAsDoneClickListener()
            }
        }

        fun bind(item: QuestionDo) {
            if (item.id!! != flippedItemId) {
                ShowLogs.log(TAG, "bind item :)")
                tvCardQuestionOrAnswer.run {
                    text = item.question
                    textSize = 22f
                }
                setCardsViewsVisibility(false)
                setCardToQuestion()
            } else {
                tvCardQuestionOrAnswer.run {
                    text = item.answer
                    textSize = 18f
                }
                setCardsViewsVisibility(true)
                setCardToAnswer()
            }
        }

        private fun bFlipClickListener() {
            if (isCurrentCardQuestion()) {
                val obAnimator = getFlipToAnswerObjectAnimator(view)
                tvCardQuestionOrAnswer.run {
                    text = data[adapterPosition].answer
                    textSize = 18f
                }
                setCardsViewsVisibility(true)
                setCardToAnswer()
                obAnimator.start()
                flippedItemId = data[adapterPosition].id!!
            } else {
                flippedItemId = -1
                val obAnimator = getFlipToQuestionObjectAnimator(view)
                tvCardQuestionOrAnswer.run {
                    text = data[adapterPosition].question
                    textSize = 22f
                }
                setCardsViewsVisibility(false)
                setCardToQuestion()
                obAnimator.start()
            }
        }

        private fun isCurrentCardQuestion() = flippedItemId != data[adapterPosition].id

        private fun ibEditCardClickListener() {

        }

        private fun ibMarkAsDoneClickListener() {

        }

        private fun setCardToQuestion() {
            view.setBackgroundResource(R.drawable.practice_card_question_bg)
            bFlipCard.setBackgroundResource(R.drawable.practice_card_question_b_flip)
            ivQuestionCardSign.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_pc_question_sign))
        }

        private fun setCardToAnswer() {
            view.setBackgroundResource(R.drawable.practice_card_answer_bg)
            bFlipCard.setBackgroundResource(R.drawable.practice_card_answer_b_flip)
            ivQuestionCardSign.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_pc_answer_sign))
        }

        private fun notifyCardFlippedToQuestion(itemPosition: Int) {
            notifyItemChanged(itemPosition)
        }

        private fun notifyCardFlippedToAnswer(itemPosition: Int) {
            notifyItemChanged(itemPosition)
        }

        //TODO flip animation sucks - probably the best way is to animate between 2 views not one and changing it is properties on the run
        private fun getFlipToAnswerObjectAnimator(targetView: View) =
                ObjectAnimator.ofFloat(targetView, "rotationY", 180f, 360f)
                        .setDuration(1000)


        private fun getFlipToQuestionObjectAnimator(targetView: View) =
                ObjectAnimator.ofFloat(targetView, "rotationY", 180f, 360f)
                        .setDuration(1000)


        private fun setCardsViewsVisibility(isVisible: Boolean) {
            if (isVisible) {
                ibMarkCardAsDone.visibility = View.VISIBLE
                ibEditCard.visibility = View.VISIBLE
                tvAnswerReadCount.visibility = View.VISIBLE
                ivAnswerReadCountIcon.visibility = View.VISIBLE
            } else {

                ibMarkCardAsDone.visibility = View.GONE
                ibEditCard.visibility = View.GONE
                tvAnswerReadCount.visibility = View.GONE
                ivAnswerReadCountIcon.visibility = View.GONE

            }


        }
    }
}