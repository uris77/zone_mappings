package org.pasmo.locations

import ratpack.groovy.handling.GroovyChainAction
import static ratpack.jackson.Jackson.json

import javax.inject.Inject

class LocationChainAction extends GroovyChainAction {
    private final Locations locations

    @Inject
    LocationChainAction(Locations locations) {
        this.locations = locations
    }

    @Override
    protected void execute() throws Exception {
        handler {
            byMethod {
                get {
                    blocking {
                        locations.allAvailable()
                    } then {List<Location> allLocations ->
                        render json(allLocations)
                    }
                }
            }
        }
    }
}
