package mr.kostua.learningpro.injections

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import mr.kostua.learningpro.injections.components.DaggerApplicationComponent

/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
class LearningProApp : DaggerApplication() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> =
            DaggerApplicationComponent.builder().application(this).build()


}