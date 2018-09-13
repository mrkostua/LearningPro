package mr.kostua.learningpro.mainPage

import dagger.Binds
import dagger.Module
import dagger.Provides
import mr.kostua.learningpro.injections.scopes.FragmentScope

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
@Module
abstract class MainPageModule {
    @FragmentScope
    @Binds
    abstract fun provideMainPagePresenter(p: MainPagePresenter): MainPageContract.Presenter
}