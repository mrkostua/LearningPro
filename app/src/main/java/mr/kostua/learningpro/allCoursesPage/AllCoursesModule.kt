package mr.kostua.learningpro.allCoursesPage

import dagger.Module
import dagger.Provides

/**
 * @author Kostiantyn Prysiazhnyi on 7/19/2018.
 */
@Module
class AllCoursesModule {
    @Provides
    fun provideAllCoursesPresenter(p: AllCoursesPresenter): AllCoursesContract.Presenter = p
}