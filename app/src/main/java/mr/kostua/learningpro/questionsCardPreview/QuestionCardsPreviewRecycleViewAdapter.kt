package mr.kostua.learningpro.questionsCardPreview

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.widget.*
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import mr.kostua.learningpro.R
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.tools.showSoftInputOnFocusAllAPI

/**
 * @author Kostiantyn Prysiazhnyi on 8/10/2018.
 */
class QuestionCardsPreviewRecycleViewAdapter(private val data: ArrayList<QuestionDo>, private val context: Context) : RecyclerView.Adapter<QuestionCardsPreviewRecycleViewAdapter.ViewHolder>() {
    private val TAG = this.javaClass.simpleName
    private val bAcceptPublishSubject = PublishSubject.create<QuestionDo>()
    fun getButtonAcceptObservable(): Observable<QuestionDo> = bAcceptPublishSubject.hide()

    private val bSavePublishSubject = PublishSubject.create<QuestionDo>()
    fun getButtonSaveObservable(): Observable<QuestionDo> = bSavePublishSubject.hide()

    private val bDeletePublishSubject = PublishSubject.create<QuestionDo>()
    fun getButtonDeleteObservable(): Observable<QuestionDo> = bDeletePublishSubject.hide()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.question_preview_row_item, parent, false))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }


    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val etQuestionPreview: EditText = view.findViewById(R.id.etQuestionPreview)
        private val etAnswerPreview: EditText = view.findViewById(R.id.etAnswerPreview)
        private val tvCurrentQuestionNumber: TextView = view.findViewById(R.id.tvCurrentQuestionNumber)
        private val bAcceptOrSave: Button = view.findViewById(R.id.bAcceptOrSave)
        private val bEditOrDelete: Button = view.findViewById(R.id.bEditOrDelete)

        init {
            RxView.clicks(bAcceptOrSave).subscribe {
                if (bAcceptOrSave.text == context.getString(R.string.questionPreviewAcceptButton)) {
                    bAcceptPublishSubject.onNext(data[adapterPosition])
                    bAccept()
                    //TODO onError not implemented exception (possible solution is to add onError and after use subscribeWith to catch this error
                } else {
                    bSavePublishSubject.onNext(data[adapterPosition])
                    bSave()
                }
            }

            RxView.clicks(bEditOrDelete).subscribe {
                if (bEditOrDelete.text == context.getString(R.string.questionPreviewEditButton)) {
                    bEdit()
                } else {
                    bDeletePublishSubject.onNext(data[adapterPosition])
                    bDelete()
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: QuestionDo) {
            view.setBackgroundResource(R.drawable.button_selector)
            with(item) {
                tvCurrentQuestionNumber.text = "${data.indexOf(this) + 1} / ${data.size}"
                etQuestionPreview.apply {
                    if (isAccepted) {
                        makeEditTextToBeEditText(this)
                    } else {
                        makeEditTextToBeText(this)
                    }
                    setText(question)
                }
                etAnswerPreview.apply {
                    if (isAccepted) {
                        makeEditTextToBeEditText(this)
                    } else {
                        makeEditTextToBeText(this)
                    }
                    setText(answer)
                }

                bAcceptOrSave.run {
                    if (isAccepted) {
                        if (text == context.getString(R.string.questionPreviewAcceptButton)) {
                            animateFromAcceptToSave()
                        }
                    } else {
                        if (text == context.getString(R.string.questionPreviewSaveButton)) {
                            animateFromSaveToAccept()
                        }
                    }
                }

                bEditOrDelete.apply {
                    if (isAccepted) {
                        if (text == context.getString(R.string.questionPreviewEditButton)) {
                            animateFromAcceptToSave()
                        }
                    } else {
                        if (text == context.getString(R.string.questionPreviewDeleteButton)) {
                            animateFromSaveToAccept()
                        }
                    }
                }
            }
        }

        private fun bSave() {
            with(data[adapterPosition]) {
                isAccepted = false
                question = etQuestionPreview.text.toString()
                answer = etAnswerPreview.text.toString()
                performCircularRevealAnimation(true)
                notifyItemChanged(adapterPosition)
            }
        }

        private fun bAccept() {
            data.removeAt(adapterPosition)
            view.setBackgroundResource(R.color.question_card_preview_accept_animation_color)
            notifyItemRemoved(adapterPosition)
            notifyItemRangeChanged(0, data.size)
        }

        private fun bEdit() {
            with(data[adapterPosition]) {
                performCircularRevealAnimation(false)
                setItemToEditable(id!!)
            }
        }

        private fun bDelete() {
            data.removeAt(adapterPosition)
            view.setBackgroundResource(R.color.question_card_preview_delete_animation_color)
            notifyItemRemoved(adapterPosition)
            notifyItemRangeChanged(0, data.size)
        }

        private fun performCircularRevealAnimation(isToAccept: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val animationBAccept = getButtonAnimator(bAcceptOrSave,
                        Math.hypot((bAcceptOrSave.width / 2).toDouble(), (bAcceptOrSave.height / 2).toDouble()).toFloat())
                animationBAccept.duration = 450
                val animationBEdit = getButtonAnimator(bEditOrDelete,
                        Math.hypot((bEditOrDelete.width / 2).toDouble(), (bEditOrDelete.height / 2).toDouble()).toFloat())
                animationBEdit.duration = 450
                if (isToAccept) {
                    animateFromSaveToAccept()
                } else {
                    animateFromAcceptToSave()

                }
                animationBAccept.start()
                animationBEdit.start()
            } else {
                if (isToAccept) {
                    animateFromSaveToAccept()
                } else {
                    animateFromAcceptToSave()
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        private fun getButtonAnimator(button: Button, buttonRadius: Float) = ViewAnimationUtils.createCircularReveal(button,
                button.width / 2, button.height / 2, 0f, buttonRadius)


        private fun setItemToEditable(itemId: Int) {
            data.forEach {
                if (it.id == itemId && !it.isAccepted) {
                    it.isAccepted = true
                    notifyItemChanged(data.indexOf(it))
                } else if (it.isAccepted) {
                    it.isAccepted = false
                    notifyItemChanged(data.indexOf(it))
                }
            }
        }

        private fun makeEditTextToBeText(view: EditText) {
            with(view) {
                setTextColor(ContextCompat.getColor(context, R.color.black))
                showSoftInputOnFocusAllAPI(false)
            }
        }

        private fun makeEditTextToBeEditText(view: EditText) {
            with(view) {
                setTextColor(ContextCompat.getColor(context, R.color.white))
                showSoftInputOnFocusAllAPI(true)
                setSelection(0)
            }
        }

        private fun animateFromAcceptToSave() {
            bEditOrDelete.setText(R.string.questionPreviewDeleteButton)
            bEditOrDelete.setBackgroundResource(R.drawable.button_selector_hint_colot)
            bAcceptOrSave.setText(R.string.questionPreviewSaveButton)
            bAcceptOrSave.setBackgroundResource(R.drawable.button_selector_hint_colot)

        }

        private fun animateFromSaveToAccept() {
            bAcceptOrSave.setText(R.string.questionPreviewAcceptButton)
            bAcceptOrSave.setBackgroundResource(R.drawable.button_selector)
            bEditOrDelete.setText(R.string.questionPreviewEditButton)
            bEditOrDelete.setBackgroundResource(R.drawable.button_selector)
        }
    }
}