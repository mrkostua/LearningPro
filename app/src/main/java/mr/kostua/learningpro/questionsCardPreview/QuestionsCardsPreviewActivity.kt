package mr.kostua.learningpro.questionsCardPreview

import android.content.Intent
import android.os.Bundle
import mr.kostua.learningpro.R
import mr.kostua.learningpro.main.BaseDaggerActivity
import mr.kostua.learningpro.tools.ConstantValues
import mr.kostua.learningpro.tools.NotificationTools
import javax.inject.Inject

class QuestionsCardsPreviewActivity : BaseDaggerActivity() {
    @Inject
    public lateinit var notificationTools: NotificationTools

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState, R.layout.activity_questions_card_preview)
        notificationTools.cancelNotification(ConstantValues.SAVED_COURSE_NOTIFICATION_ID)
        initializeViews()
    }

    private fun initializeViews() {
        val courseId = intent.getIntExtra(ConstantValues.CONTINUE_COURSE_CREATION_COURSE_ID_KEY, -1)


    }
}
