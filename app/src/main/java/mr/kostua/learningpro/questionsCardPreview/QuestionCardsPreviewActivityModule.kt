package mr.kostua.learningpro.questionsCardPreview

import dagger.Binds
import dagger.Module
import mr.kostua.learningpro.injections.scopes.ActivityScope

/**
 * @author Kostiantyn Prysiazhnyi on 7/27/2018.
 */
@Module
abstract class QuestionCardsPreviewActivityModule {
    @ActivityScope
    @Binds
    abstract fun provideQuestionCardsPreviewPresenter(p: QuestionCardsPreviewPresenter): QuestionCardsPreviewContract.Presenter
}