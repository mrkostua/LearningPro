package mr.kostua.learningpro.practiceCards

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
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.QuestionDo

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
class PracticeCardsRecycleViewAdapter(private val data: ArrayList<QuestionDo>, private val context: Context) : RecyclerView.Adapter<PracticeCardsRecycleViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.practice_card_row_item, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        /**
         * TODO today tasks :
         * 1 handle clickListeners in adapter and in activity
         * 2 bFlip animation for item
         * Only one card can be set to answer side (for know so save id of this questionDo and after
         * in bind method check if it equal set visible otherwise not visible)
         * Same in onClickListener for bFlip check if the id of flipping card ( data[adapterPosition])
         *
         * 3 design of answer side (also change flip bg)
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
                if (flippedItemId == data[adapterPosition].id) {
                    flippedItemId = -1
                    notifyCardFlippedToQuestion(adapterPosition)
                } else {
                    flippedItemId = data[adapterPosition].id!!
                    notifyCardFlippedToAnswer(adapterPosition)
                }
            }

            RxView.clicks(ibEditCard).subscribe {

            }
            RxView.clicks(ibMarkCardAsDone).subscribe {

            }
        }

        fun bind(item: QuestionDo) {
            if (item.id!! != flippedItemId) {
                tvCardQuestionOrAnswer.run {
                    text = item.question
                    textSize = 22f
                }

                setCardsViewsVisibility(false)
                view.setBackgroundResource(R.drawable.practice_card_question_bg)
                bFlipCard.setBackgroundResource(R.drawable.practice_card_question_b_flip)
                ivQuestionCardSign.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_pc_question_sign))
                //TODO also here perform back flip to question animation (in different direction) 180 - 360
            } else {
                tvCardQuestionOrAnswer.run {
                    text = item.answer
                    textSize = 18f
                }
                setCardsViewsVisibility(true)
                view.setBackgroundResource(R.drawable.practice_card_answer_bg)
                bFlipCard.setBackgroundResource(R.drawable.practice_card_answer_b_flip)
                ivQuestionCardSign.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icon_pc_answer_sign))
                //TODO also here perform flip animation ... 0-180
            }
        }

        private fun notifyCardFlippedToQuestion(itemPosition: Int) {
            notifyItemChanged(itemPosition)
        }

        private fun notifyCardFlippedToAnswer(itemPosition: Int) {
            notifyItemChanged(itemPosition)
        }


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