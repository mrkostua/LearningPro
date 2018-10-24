package mr.kostua.learningpro.questionsCardPreview

import android.animation.Animator
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
import mr.kostua.learningpro.tools.ConstantValues
import mr.kostua.learningpro.tools.showSoftInputOnFocusAllAPI

/**
 * @author Kostiantyn Prysiazhnyi on 8/10/2018.
 */
class QuestionCardsPreviewRecycleViewAdapter(private val data: ArrayList<QuestionDo>, private val context: Context) : RecyclerView.Adapter<QuestionCardsPreviewRecycleViewAdapter.ViewHolder>() {
    private val bAcceptPublishSubject = PublishSubject.create<QuestionDo>()
    fun getButtonAcceptObservable(): Observable<QuestionDo> = bAcceptPublishSubject.hide()

    private val bSavePublishSubject = PublishSubject.create<QuestionDo>()
    fun getButtonSaveObservable(): Observable<QuestionDo> = bSavePublishSubject.hide()

    private val bDeletePublishSubject = PublishSubject.create<QuestionDo>()
    fun getButtonDeleteObservable(): Observable<QuestionDo> = bDeletePublishSubject.hide()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.question_preview_row_item, parent, false))

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position])
    }

    @SuppressLint("CheckResult") //TODO refactoring
    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val etQuestionPreview: EditText = view.findViewById(R.id.etQuestionPreview)
        private val etAnswerPreview: EditText = view.findViewById(R.id.etAnswerPreview)
        private val tvCurrentQuestionNumber: TextView = view.findViewById(R.id.tvCurrentQuestionNumber)
        private val bAcceptOrSave: Button = view.findViewById(R.id.bAcceptOrSave)
        private val bEditOrDelete: Button = view.findViewById(R.id.bEditOrDelete)
        private var editableItemId = -1
        private fun isCurrentCardAvailable() = data.getOrNull(adapterPosition) != null

        init {
            initializeAcceptSaveClickListeners()
            initializeEditDeleteClickListeners()
        }

        private fun initializeAcceptSaveClickListeners() {
            RxView.clicks(bAcceptOrSave).subscribe {
                if (isCurrentCardAvailable()) {
                    if (bAcceptOrSave.text == context.getString(R.string.questionPreviewAcceptButton)) {
                        data[adapterPosition].isAccepted = true
                        bAccept(data[adapterPosition])
                    } else {
                        editableItemId = -1
                        bSave()
                        bSavePublishSubject.onNext(data[adapterPosition])
                    }
                }
            }
        }

        private fun initializeEditDeleteClickListeners() {
            RxView.clicks(bEditOrDelete).subscribe {
                if (isCurrentCardAvailable()) {
                    if (bEditOrDelete.text == context.getString(R.string.questionPreviewEditButton)) {
                        bEdit()
                    } else {
                        bDelete(data[adapterPosition])
                        editableItemId = -1
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(item: QuestionDo) {
            view.setBackgroundResource(R.drawable.button_selector)
            with(item) {
                tvCurrentQuestionNumber.text = "${data.indexOf(this) + 1} / ${data.size}"
                etQuestionPreview.apply {
                    if (editableItemId == item.id) {
                        makeEditTextToBeEditText(this)
                    } else {
                        makeEditTextToBeText(this)
                    }
                    setText(question)
                }
                etAnswerPreview.apply {
                    if (editableItemId == item.id) {
                        makeEditTextToBeEditText(this)
                    } else {
                        makeEditTextToBeText(this)
                    }
                    setText(answer)
                }
                bAcceptOrSave.run {
                    if (editableItemId == item.id) {
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
                    if (editableItemId == item.id) {
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
            bAcceptOrSave.postDelayed({
                with(data[adapterPosition]) {
                    question = etQuestionPreview.text.toString()
                    answer = etAnswerPreview.text.toString()
                }
                performCircularRevealAnimation(true)
                notifyItemChanged(adapterPosition)
            }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
        }

        private fun bAccept(cardToDelete: QuestionDo) {
            bAcceptOrSave.postDelayed({
                data.removeAt(adapterPosition)
                bAcceptPublishSubject.onNext(cardToDelete)
                view.setBackgroundResource(R.color.question_card_preview_accept_animation_color)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(0, data.size)
            }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
        }

        private fun bEdit() {
            with(data[adapterPosition]) {
                performCircularRevealAnimation(false)
                setItemToEditable(id!!)
            }
        }

        private fun bDelete(cardToDelete: QuestionDo) {
            bEditOrDelete.postDelayed({
                data.removeAt(adapterPosition)
                bDeletePublishSubject.onNext(cardToDelete)
                view.setBackgroundResource(R.color.question_card_preview_delete_animation_color)
                notifyItemRemoved(adapterPosition)
                notifyItemRangeChanged(0, data.size)
            }, ConstantValues.BUTTON_SELECTOR_ANIMATION_TIME_MS)
        }

        private lateinit var animationBAccept: Animator
        private lateinit var animationBEdit: Animator
        private fun performCircularRevealAnimation(isToAccept: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animationBAccept = getButtonAnimator(bAcceptOrSave).apply { duration = 450 }
                animationBEdit = getButtonAnimator(bEditOrDelete).apply { duration = 450 }

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
        private fun getButtonAnimator(button: Button) = ViewAnimationUtils.createCircularReveal(button,
                button.width / 2, button.height / 2, 0f, getButtonRadius(button))

        private fun getButtonRadius(button: Button): Float = Math.hypot((button.width / 2).toDouble(), (button.height / 2).toDouble()).toFloat()

        private fun setItemToEditable(itemId: Int) {
            var itemToEdit: QuestionDo = data[0]
            data.forEach {
                if (it.id == itemId) {
                    itemToEdit = it
                } else if (it.id == editableItemId) {
                    editableItemId = -1
                    notifyItemChanged(data.indexOf(it))
                }
            }
            editableItemId = itemToEdit.id!!
            notifyItemChanged(data.indexOf(itemToEdit))
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
            bEditOrDelete.setBackgroundResource(R.drawable.button_selector_white)
            bAcceptOrSave.setText(R.string.questionPreviewSaveButton)
            bAcceptOrSave.setBackgroundResource(R.drawable.button_selector_white)

        }

        private fun animateFromSaveToAccept() {
            bAcceptOrSave.setText(R.string.questionPreviewAcceptButton)
            bAcceptOrSave.setBackgroundResource(R.drawable.button_selector)
            bEditOrDelete.setText(R.string.questionPreviewEditButton)
            bEditOrDelete.setBackgroundResource(R.drawable.button_selector)
        }
    }
}