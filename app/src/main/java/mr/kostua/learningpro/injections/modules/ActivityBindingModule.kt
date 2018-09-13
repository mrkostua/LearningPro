package mr.kostua.learningpro.injections.modules

import dagger.Module
import dagger.android.ContributesAndroidInjector
import mr.kostua.learningpro.allCoursesPage.AllCoursesFragment
import mr.kostua.learningpro.allCoursesPage.AllCoursesModule
import mr.kostua.learningpro.injections.scopes.ActivityScope
import mr.kostua.learningpro.injections.scopes.FragmentScope
import mr.kostua.learningpro.injections.scopes.ServiceScope
import mr.kostua.learningpro.main.MainActivity
import mr.kostua.learningpro.main.MainActivityModule
import mr.kostua.learningpro.mainPage.MainPageFragment
import mr.kostua.learningpro.mainPage.MainPageModule
import mr.kostua.learningpro.mainPage.executionService.NewCourseCreationService
import mr.kostua.learningpro.questionsCardPreview.QuestionCardsPreviewActivityModule
import mr.kostua.learningpro.questionsCardPreview.QuestionsCardsPreviewActivity

/**
 * @author Kostiantyn Prysiazhnyi on 7/13/2018.
 */
@Module
abstract class ActivityBindingModule {
    @FragmentScope
    @ContributesAndroidInjector(modules = [(MainPageModule::class), (DataModule::class), (DisplayHelperModule::class)])
    abstract fun getMainPageFragment(): MainPageFragment

    @ActivityScope
    @ContributesAndroidInjector(modules = [(MainActivityModule::class)])
    abstract fun getMainActivity(): MainActivity

    @ServiceScope
    @ContributesAndroidInjector(modules = [(DataModule::class), (DisplayHelperModule::class)])
    abstract fun getNewCourseCreationService(): NewCourseCreationService

    @FragmentScope
    @ContributesAndroidInjector(modules = [(DataModule::class), (AllCoursesModule::class), (DisplayHelperModule::class)])
    abstract fun getAllCoursesFragment(): AllCoursesFragment

    @ActivityScope
    @ContributesAndroidInjector(modules = [(QuestionCardsPreviewActivityModule::class), (DisplayHelperModule::class), (DataModule::class)])
    abstract fun getQuestionCardPreviewActivity(): QuestionsCardsPreviewActivity
}