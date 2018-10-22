package mr.kostua.learningpro.questionsCardPreview

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.QuestionDo
import mr.kostua.learningpro.injections.scopes.ActivityScope
import mr.kostua.learningpro.tools.DBObserverHelper
import mr.kostua.learningpro.tools.ShowLogs
import javax.inject.Inject

/**
 * @author Kostiantyn Prysiazhnyi on 8/2/2018.
 */
@ActivityScope
class QuestionCardsPreviewPresenter @Inject constructor(private val db: DBHelper) : QuestionCardsPreviewContract.Presenter {
    private val TAG = this.javaClass.simpleName
    private val disposables = CompositeDisposable()
    override lateinit var view: QuestionCardsPreviewContract.View
    override fun takeView(view: QuestionCardsPreviewContract.View) {
        this.view = view
    }

    override fun populateNotAcceptedQuestions(courseId: Int) {
        disposables.add(DBObserverHelper.getNotAcceptedQuestions(db, object : DisposableSingleObserver<List<QuestionDo>>() {
            override fun onSuccess(list: List<QuestionDo>) {
                view.initializeRecycleView(list as ArrayList)
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "getNotAcceptedQuestions onError : ${e.message}")
            }

        }, courseId))
    }

    override fun populateQuestionToEdit(questionToEditId: Int) {
        disposables.add(DBObserverHelper.getQuestionBuId(db, object : DisposableSingleObserver<QuestionDo>() {
            override fun onSuccess(itemToEdit: QuestionDo) {
                view.initializeRecycleView(arrayListOf(itemToEdit))
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "getQuestionBuId onError : ${e.message}")
            }
        }, questionToEditId))
    }

    override fun acceptQuestion(questionDo: QuestionDo) {
        updateQuestionDo(questionDo, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(updatedItems: Int) {
                if (updatedItems == 1) {
                    ShowLogs.log(TAG, "acceptQuestion item accepted successfully ")
                    view.showToast("accepted successfully")
                } else {
                    ShowLogs.log(TAG, "acceptQuestion no item accepted ")
                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "acceptQuestion error ${e.message} ")
            }
        })
    }

    override fun updateQuestion(questionDo: QuestionDo) {
        updateQuestionDo(questionDo, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(updatedItems: Int) {
                if (updatedItems == 1) {
                    ShowLogs.log(TAG, "updateQuestion item updated successfully ")
                    view.showToast("updated successfully")
                } else {
                    ShowLogs.log(TAG, "updateQuestion no item updated ")
                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "updateQuestion error ${e.message} ")
            }
        })
    }

    override fun decreaseQuestionsAmountBy(courseId: Int, decreaseBy: Int) {
        disposables.add(DBObserverHelper.decreaseCourseQuestionsAmountBy(db, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(updatedItems: Int) {
                ShowLogs.log(TAG, "decreaseQuestionsAmountBy onSuccess updatedItems :$updatedItems")
                //TODO in future do some error handling (like in AllCoursesList (after restarting app) do some check of db (as calculating all questions with given courseID and comparing to course questionAmount)
                //TODO OR just send some bug report to developer (info will be logged) READ about logging bugs on production
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "decreaseQuestionsAmountBy no item deleted ")
            }
        }, courseId, decreaseBy))

    }

    override fun deleteQuestion(questionDo: QuestionDo, courseId: Int) {
        disposables.add(DBObserverHelper.deleteQuestion(db, object : DisposableSingleObserver<Int>() {
            override fun onSuccess(updatedItems: Int) {
                if (updatedItems != 1) {
                    ShowLogs.log(TAG, "deleteQuestion no item deleted ")
                }
            }

            override fun onError(e: Throwable) {
                ShowLogs.log(TAG, "deleteQuestion error ${e.message} ")
            }
        }, questionDo))
    }

    override fun setCourseReviewedTrue(courseId: Int) {
        disposables.add(Single.fromCallable { db.updateCourseIsReviewedState(courseId) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<Boolean>() {
                    override fun onSuccess(isDeleted: Boolean) {
                        if (isDeleted) {
                            ShowLogs.log(TAG, "deleteQuestion item deleted successfully ")
                        }
                    }

                    override fun onError(e: Throwable) {
                        ShowLogs.log(TAG, "deleteQuestion2 error ${e.message} ")
                    }
                }))
    }

    override fun disposeAll() {
        disposables.clear()
    }

    private fun updateQuestionDo(questionDo: QuestionDo, observer: DisposableSingleObserver<Int>) {
        disposables.add(DBObserverHelper.updateQuestion(db, observer, questionDo))
    }

}