package org.pasmo.locations

import ratpack.func.Action
import ratpack.groovy.Groovy
import ratpack.handling.Chain

import static ratpack.jackson.Jackson.json

import javax.inject.Inject

class LocationChainAction implements Action<Chain> {
    private final Locations locations

    @Inject
    LocationChainAction(Locations locations) {
        this.locations = locations
    }

    @Override
    void execute(Chain chain) throws Exception {
        Groovy.chain(chain) {
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
}
