package mr.kostua.learningpro.practiceCards

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Message
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
import java.lang.ref.WeakReference

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
class PracticeCardsRecycleViewAdapter(val data: ArrayList<QuestionDo>, private val courseId: Int) : RecyclerView.Adapter<PracticeCardsRecycleViewAdapter.ViewHolder>() { //TODO fix backStack and animation!
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

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.disableAllHandlerMessages()
    }

    @SuppressLint("CheckResult")
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val clQuestionSide: ConstraintLayout = view.findViewById(R.id.iQuestionSideCard)
        private val tvCardQuestion: TextView = clQuestionSide.findViewById(R.id.tvCardQuestion)
        private val bFlipCardQuestionSide: Button = clQuestionSide.findViewById(R.id.bFlipCardQuestionSide)

        private val clAnswerSide: ConstraintLayout = view.findViewById(R.id.iAnswerSideCard)
        private val tvCardAnswer: TextView = clAnswerSide.findViewById(R.id.tvCardAnswer)
        private val ibMarkCardAsDone: ImageButton = clAnswerSide.findViewById(R.id.ibMarkCardAsDone)
        private val ibEditCard: ImageButton = clAnswerSide.findViewById(R.id.ibEditCard)
        private val tvAnswerReadCount: TextView = clAnswerSide.findViewById(R.id.tvAnswerReadCount)
        private val bFlipCardAnswerSide: Button = clAnswerSide.findViewById(R.id.bFlipCardAnswerSide)

        private val flipViewPracticeCard: EasyFlipView = view.findViewById(R.id.flipViewPracticeCard)
        private var flippedItemId = -1

        private val handler = PracticeCardsHandler(this)
        val updateViewCountsKey = 0
        fun disableAllHandlerMessages() {
            handler.removeMessages(updateViewCountsKey) //TODO think about this
            ShowLogs.log(TAG, "disableAllHandlerMessages() ")

        }

        fun updateViewCounts(adapterPositionToUpdate: Int) {
            ShowLogs.log(TAG, "updateViewCounts() ")
            ++data[adapterPositionToUpdate].viewsCount
            ShowLogs.log(TAG, "updateViewCounts() ${data[adapterPositionToUpdate].viewsCount}")
            notifyItemChanged(adapterPositionToUpdate)
            viewsCountPublishSubject.onNext(data[adapterPositionToUpdate])
        }

        fun checkIfAnswerIsStillOpened(oldAdapterPosition: Int): Boolean {
            val result = oldAdapterPosition == adapterPosition && flipViewPracticeCard.currentFlipState == EasyFlipView.FlipState.BACK_SIDE
            ShowLogs.log(TAG, "checkIfAnswerIsStillOpened : is : $oldAdapterPosition and result is :$result")
            return result

        }

        init {
            tvCardQuestion.movementMethod = ScrollingMovementMethod()
            RxView.clicks(bFlipCardQuestionSide).subscribe {
                flipView(1000)
                flippedItemId = data[adapterPosition].id!!
                handler.sendMessageDelayed(handler.obtainMessage(updateViewCountsKey, adapterPosition), ConstantValues.UPDATE_VIEW_COUNTS_TIMER_MS)    //TODO test this solution Test it with logs
                ShowLogs.log(TAG, "FlipCardSide : delay message send  : cardId :${data[adapterPosition].id!!}")
            }

            tvCardAnswer.movementMethod = ScrollingMovementMethod()
            RxView.clicks(bFlipCardAnswerSide).subscribe {
                flippedItemId = -1
                flipView(1000)
            }

            RxView.clicks(ibEditCard).subscribe {
                ibEditClickListener()
            }
            RxView.clicks(ibMarkCardAsDone).subscribe {
                data[adapterPosition].isLearned = true
                flippedItemId = -1
                ibMarkAsDonePublishSubject.onNext(data[adapterPosition])
                ibMarkAsDoneClickListener(clAnswerSide)
            }
        }

        fun bind(item: QuestionDo) {
            ShowLogs.log(TAG, "bind item ${item.id} adapterPos $adapterPosition")
            clAnswerSide.setBackgroundResource(R.drawable.practice_card_answer_bg)
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

        private fun ibEditClickListener() {
            with(view.context) {
                ibEditCard.postDelayed({
                    startActivity(Intent(this, QuestionsCardsPreviewActivity::class.java)
                            .putExtra(ConstantValues.COURSE_ID_KEY, courseId)
                            .putExtra(ConstantValues.QUESTION_ID_KEY, data[adapterPosition].id!!)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
                }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
            }
        }

        private fun ibMarkAsDoneClickListener(currentSideLayout: View) {
            ibMarkCardAsDone.postDelayed({
                data.removeAt(adapterPosition)
                currentSideLayout.setBackgroundResource(R.color.question_card_preview_accept_animation_color)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(adapterPosition, data.size)
            }, if (isLastQuestionCard(data.size))
                ConstantValues.ALL_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS
            else
                ConstantValues.CARD_LEARNED_FIRE_WORK_ANIMATION_TIME_TO_LIVE_MS)
        }
    }

    private fun isLastQuestionCard(dataSize: Int) = dataSize == 1

    private class PracticeCardsHandler(viewHolder: ViewHolder) : Handler() {
        private val wrViewHolder: WeakReference<ViewHolder> = WeakReference(viewHolder)
        override fun handleMessage(msg: Message?) {
            val viewHolder = wrViewHolder.get()
            if (viewHolder != null) {
                when (msg?.what) {
                    viewHolder.updateViewCountsKey -> {
                        if (msg.obj is Int && viewHolder.checkIfAnswerIsStillOpened(msg.obj as Int)) {
                            viewHolder.updateViewCounts(msg.obj as Int)
                            ShowLogs.log(this.javaClass.simpleName, "handleMessage :updateViewCounts() started : ")
                        }
                    }
                }

            }
        }
    }
    fun nitifyLastItemDeleted(){

    }
}