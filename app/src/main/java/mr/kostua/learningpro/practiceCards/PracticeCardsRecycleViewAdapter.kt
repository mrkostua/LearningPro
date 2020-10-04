package mr.kostua.learningpro.practiceCards

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Message
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
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
class PracticeCardsRecycleViewAdapter(private val data: ArrayList<QuestionDo>, private val courseId: Int) : RecyclerView.Adapter<PracticeCardsRecycleViewAdapter.ViewHolder>() {
    private val TAG = this.javaClass.simpleName
    private val ibMarkAsDonePublishSubject = PublishSubject.create<QuestionDo>()
    private val viewsCountPublishSubject = PublishSubject.create<QuestionDo>()
    private lateinit var handler: PracticeCardsHandler

    fun getViewsCountPublishSubject(): Observable<QuestionDo> = viewsCountPublishSubject.hide()
    fun getIBMarkAsDoneObservable(): Observable<QuestionDo> = ibMarkAsDonePublishSubject.hide()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.practice_card_row_item, parent, false)).run {
                handler = PracticeCardsHandler(this)
                this
            }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    fun disableAllHandlerMessages() {
        if (handler.hasMessages(PracticeCardsRecycleViewAdapter.HANDLER_VIEW_COUNTS_KEY)) {
            handler.removeMessages(PracticeCardsRecycleViewAdapter.HANDLER_VIEW_COUNTS_KEY)
            ShowLogs.log(TAG, "disableAllHandlerMessages() hasMessages true")
        }
    }

    fun sendDelayMessageViewCounts(cardPosition: Int, timeMs: Long) {
        ShowLogs.log(TAG, "sendDelayMessageViewCounts")
        handler.sendMessageDelayed(handler.obtainMessage(PracticeCardsRecycleViewAdapter.HANDLER_VIEW_COUNTS_KEY, cardPosition), timeMs)
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

        init {
            tvCardQuestion.movementMethod = ScrollingMovementMethod()
            tvCardAnswer.movementMethod = ScrollingMovementMethod()

            setFlipCardQuestionSideClickListener()
            setFlipCardAnswerSideClickListener()
            setEditCardClickListener()
            setMarkCardAsDoneClickListener()
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

        fun updateViewCounts(adapterPositionToUpdate: Int) {
            ++data[adapterPositionToUpdate].viewsCount
            ShowLogs.log(TAG, "updateViewCounts() viewCounts : ${data[adapterPositionToUpdate].viewsCount}")
            notifyItemChanged(adapterPositionToUpdate)
            viewsCountPublishSubject.onNext(data[adapterPositionToUpdate])
        }

        fun checkIfAnswerIsStillOpened(oldAdapterPosition: Int): Boolean {
            val result = oldAdapterPosition == adapterPosition && flipViewPracticeCard.currentFlipState == EasyFlipView.FlipState.BACK_SIDE
            ShowLogs.log(TAG, "checkIfAnswerIsStillOpened : is : oldAdapter : $oldAdapterPosition adaPost $adapterPosition and flipState is :${flipViewPracticeCard.currentFlipState == EasyFlipView.FlipState.BACK_SIDE} and result is :$result ")
            return result
        }

        private fun setFlipCardQuestionSideClickListener() {
            RxView.clicks(bFlipCardQuestionSide).subscribe {
                flipView(1000)
                flippedItemId = data[adapterPosition].id!!
                disableAllHandlerMessages()
                sendDelayMessageViewCounts(adapterPosition, ConstantValues.UPDATE_VIEW_COUNTS_TIMER_MS)
                ShowLogs.log(TAG, "bFlipCardQuestionSide : delay message send  : cardId :${data[adapterPosition].id!!} adapterPosition is :$adapterPosition")
            }
        }

        private fun setFlipCardAnswerSideClickListener() {
            RxView.clicks(bFlipCardAnswerSide).subscribe {
                disableAllHandlerMessages()
                ShowLogs.log(TAG, "bFlipCardAnswerSide")
                flippedItemId = -1
                flipView(1000)
            }
        }

        private fun setEditCardClickListener() {
            RxView.clicks(ibEditCard).subscribe {
                edit()
            }
        }

        private fun setMarkCardAsDoneClickListener() {
            RxView.clicks(ibMarkCardAsDone).subscribe {
                data[adapterPosition].isLearned = true
                flippedItemId = -1
                ibMarkAsDonePublishSubject.onNext(data[adapterPosition])
                markAsDone(clAnswerSide)
            }
        }

        private fun flipView(duration: Int) {
            flipViewPracticeCard.flipDuration = duration
            flipViewPracticeCard.flipTheView()
        }

        private fun edit() {
            with(view.context) {
                ibEditCard.postDelayed({
                    startActivity(Intent(this, QuestionsCardsPreviewActivity::class.java)
                            .putExtra(ConstantValues.COURSE_ID_KEY, courseId)
                            .putExtra(ConstantValues.QUESTION_ID_KEY, data[adapterPosition].id!!)
                            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY))
                }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
            }
        }

        private fun markAsDone(currentSideLayout: View) {
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
        override fun handleMessage(msg: Message) {
            val viewHolder = wrViewHolder.get()
            if (viewHolder != null) {
                when (msg.what) {
                    PracticeCardsRecycleViewAdapter.HANDLER_VIEW_COUNTS_KEY -> {
                        if (msg.obj is Int && viewHolder.checkIfAnswerIsStillOpened(msg.obj as Int)) {
                            viewHolder.updateViewCounts(msg.obj as Int)
                            ShowLogs.log(this.javaClass.simpleName, "handleMessage :handleMessage() TRUE ")
                        } else {
                            ShowLogs.log(this.javaClass.simpleName, "handleMessage :handleMessage() FALSE : ")
                        }
                    }
                }

            }
        }
    }

    companion object {
        const val HANDLER_VIEW_COUNTS_KEY = 0
    }
}