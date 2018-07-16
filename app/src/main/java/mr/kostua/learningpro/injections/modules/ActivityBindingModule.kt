package mr.kostua.learningpro.injections.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import mr.kostua.learningpro.injections.scopes.FragmentScope
import mr.kostua.learningpro.main.MainActivity
import mr.kostua.learningpro.main.MainActivityModule
import mr.kostua.learningpro.mainPage.MainPageFragment
import mr.kostua.learningpro.mainPage.MainPageModule

/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
@Module
public abstract class ActivityBindingModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = [(MainPageModule::class), (ContentResolverModule::class)])
    public abstract fun getMainPageFragment(): MainPageFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [(MainActivityModule::class)])
    public abstract fun getMainActivity(): MainActivity
}