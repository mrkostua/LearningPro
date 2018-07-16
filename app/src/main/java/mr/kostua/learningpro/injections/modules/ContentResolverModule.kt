package mr.kostua.learningpro.injections.modules

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
@Module
class ContentResolverModule {
    @Provides
    fun provideContentResolver(context: Context): ContentResolver = context.contentResolver
}