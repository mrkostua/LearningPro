package mr.kostua.learningpro.injections.modules

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */

@Module
abstract class ApplicationModule {
    @Binds
    abstract fun provideAppContext(app: Application): Context

}