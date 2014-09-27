import org.pasmo.datastore.MongoDbModule
import org.pasmo.locations.LocationChainAction
import org.pasmo.locations.LocationsModule
import org.pasmo.surveys.SurveysModule
import org.pasmo.jwt.GoogleJwtHandler
import ratpack.jackson.JacksonModule

import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        add new JacksonModule()
        add new MongoDbModule()
        add new SurveysModule()
        add new LocationsModule()
    }

    handlers {
        prefix("auth") {
            handler chain(registry.get(GoogleJwtHandler))
        }

        prefix("api") {
            prefix("locations") {
                handler chain(registry.get(LocationChainAction))
            }
        }
    }
}