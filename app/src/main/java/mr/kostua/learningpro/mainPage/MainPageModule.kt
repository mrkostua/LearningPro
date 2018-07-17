package mr.kostua.learningpro.mainPage

import dagger.Module
import dagger.Provides
import mr.kostua.learningpro.injections.scopes.FragmentScope

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
@Module
class MainPageModule {
    @FragmentScope
    @Provides
    public fun provideMainPagePresenter(p: MainPagePresenter): MainPageContract.Presenter = p
}