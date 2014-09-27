package org.pasmo.locations

import com.google.inject.AbstractModule
import com.google.inject.Scopes

class LocationsModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Locations.class).to(LocationsMongoImpl).in(Scopes.SINGLETON)

    }
}
