package mr.kostua.learningpro.allCoursesPage

import dagger.Binds
import dagger.Module
import mr.kostua.learningpro.injections.scopes.FragmentScope

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
@Module
abstract class AllCoursesModule {
    @FragmentScope
    @Binds
    abstract fun provideAllCoursesPresenter(p: AllCoursesPresenter): AllCoursesContract.Presenter
}