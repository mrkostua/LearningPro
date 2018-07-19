package mr.kostua.learningpro.injections.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import mr.kostua.learningpro.allCoursesPage.AllCoursesFragment
import mr.kostua.learningpro.allCoursesPage.AllCoursesModule
import mr.kostua.learningpro.injections.scopes.FragmentScope
import mr.kostua.learningpro.injections.scopes.ServiceScope
import mr.kostua.learningpro.main.MainActivity
import mr.kostua.learningpro.main.MainActivityModule
import mr.kostua.learningpro.mainPage.MainPageFragment
import mr.kostua.learningpro.mainPage.MainPageModule
import mr.kostua.learningpro.mainPage.executionService.NewCourseCreationService

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

    @ServiceScope
    @ContributesAndroidInjector(modules = [(DataModule::class), (DisplayHelperModule::class)])
    public abstract fun getNewCourseCreationService(): NewCourseCreationService
    @ServiceScope
    @ContributesAndroidInjector(modules = [(DataModule::class), (AllCoursesModule::class)])
    public abstract fun getAllCoursesFragment(): AllCoursesFragment
}