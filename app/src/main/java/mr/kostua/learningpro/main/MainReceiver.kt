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
                val courseId = intent.getIntExtra(ConstantValues.COURSE_ID_KEY, -1)
                if (courseId != -1) {
                    startQuestionsCardPreviewActivity(context, courseId)
                }
            }

        }

    }

    private fun startQuestionsCardPreviewActivity(context: Context, courseId: Int) {
        context.startActivity(Intent(context, QuestionsCardsPreviewActivity::class.java)
                .putExtra(ConstantValues.COURSE_ID_KEY, courseId))
    }

}
