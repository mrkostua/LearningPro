package mr.kostua.learningpro.injections.components

import android.app.Application

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import mr.kostua.learningpro.data.DBHelper
import mr.kostua.learningpro.data.local.LocalDB
import mr.kostua.learningpro.injections.LearningProApp
import mr.kostua.learningpro.injections.modules.ActivityBindingModule
import mr.kostua.learningpro.injections.modules.ApplicationModule
import mr.kostua.learningpro.injections.modules.DataModule
import javax.inject.Singleton

/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
@Singleton
@Component(modules = [(ApplicationModule::class),
    (AndroidSupportInjectionModule::class),
    (ActivityBindingModule::class)])
interface ApplicationComponent : AndroidInjector<LearningProApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): ApplicationComponent.Builder

        fun build(): ApplicationComponent
    }
}