package mr.kostua.learningpro.practiceCards

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.QuestionDo

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
class PracticeCardsRecycleViewAdapter(private val data: ArrayList<QuestionDo>) : RecyclerView.Adapter<PracticeCardsRecycleViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.practice_card_row_item, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val tvCardQuestionOrAnswer: TextView = view.findViewById(R.id.tvCardQuestionOrAnswer)
        private val bFlipCard: Button = view.findViewById(R.id.bFlipCard)
        private var isQuestionSide = true

        init {
            RxView.clicks(bFlipCard).subscribe {

            }
        }

        fun bind(item: QuestionDo) {
            //TODO some boolean value to check if this card must display question or answer side
            if (isQuestionSide) {
                tvCardQuestionOrAnswer.text = item.question

            } else {
                tvCardQuestionOrAnswer.text = item.answer

            }
        }

    }
}