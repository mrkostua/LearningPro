package mr.kostua.learningpro.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import mr.kostua.learningpro.questionsCardPreview.QuestionsCardsPreviewActivity
import mr.kostua.learningpro.tools.ConstantValues

class MainReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ConstantValues.ACTION_CONTINUE_COURSE_CREATION -> {
                with(intent.getIntExtra(ConstantValues.COURSE_ID_KEY, -1)) {
                    if (this != -1) {
                        startQuestionsCardPreviewActivity(context, this)
                    }
                }
            }

        }
    }

    private fun startQuestionsCardPreviewActivity(context: Context, courseId: Int) {
        context.startActivity(Intent(context, QuestionsCardsPreviewActivity::class.java).apply {
            putExtra(ConstantValues.COURSE_ID_KEY, courseId)
            putExtra(ConstantValues.COURSE_STARTED_FROM_SERVICE, true)
        })

    }
}
