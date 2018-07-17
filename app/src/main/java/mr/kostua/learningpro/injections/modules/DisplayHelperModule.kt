package mr.kostua.learningpro.injections.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import mr.kostua.learningpro.tools.NotificationTools

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
@Module
class DisplayHelperModule {
    @Provides
    fun getNotificationsTools(context: Context) = NotificationTools(context)
}