package org.pasmo.datastore

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class MongoDbModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(MongoDbClient.class).in(Scopes.SINGLETON)

    }
}
