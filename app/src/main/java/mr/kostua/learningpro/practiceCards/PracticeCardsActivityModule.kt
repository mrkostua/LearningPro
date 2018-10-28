package mr.kostua.learningpro.practiceCards

import dagger.Binds
import dagger.Module
import mr.kostua.learningpro.injections.scopes.ActivityScope

/**
 * @author Kostiantyn Prysiazhnyi on 9/13/2018.
 */
@Module
abstract class PracticeCardsActivityModule {
    @ActivityScope
    @Binds
    abstract fun providePracticeCardsPresenter(p: PracticeCardsPresenter): PracticeCardsContract.Presenter
}