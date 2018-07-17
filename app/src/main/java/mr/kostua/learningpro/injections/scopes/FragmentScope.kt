package mr.kostua.learningpro.injections.scopes

import javax.inject.Scope

/**
 * @author Kostiantyn Prysiazhnyi on 7/16/2018.
 */
@Scope
@Target(AnnotationTarget.CLASS, AnnotationTarget.FILE, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class FragmentScope
