import org.pasmo.datastore.MongoDbModule
import org.pasmo.surveys.SurveysModule
import org.pasmo.jwt.GoogleJwtHandler
import ratpack.jackson.JacksonModule

import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        add new JacksonModule()
        add new MongoDbModule()
        add new SurveysModule()
    }

    handlers {
        prefix("auth") {
            handler chain(registry.get(GoogleJwtHandler))
        }
    }
}