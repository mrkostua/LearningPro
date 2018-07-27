package mr.kostua.learningpro.injections.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.LocalDB
import mr.kostua.learningpro.data.local.sharedPref.ConstantsPreferences

/**
 * @author Kostiantyn Prysiazhnyi on 7/17/2018.
 */
@Module
class DataModule {
    @Provides
    fun provideDBHelper(context: Context): DBHelper = DBHelper(LocalDB.getInstance(context),
            context.getSharedPreferences(ConstantsPreferences.APP_SP_NAME.getKeyValue(), Context.MODE_PRIVATE))
}