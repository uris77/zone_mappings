package org.pasmo.surveys

import com.google.inject.AbstractModule
import com.google.inject.Scopes
import groovy.transform.CompileStatic

@CompileStatic
class SurveysModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Surveys.class).to(SurveysMongoImpl).in(Scopes.SINGLETON)
    }
}
