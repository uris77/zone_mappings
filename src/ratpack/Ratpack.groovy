import org.pasmo.jwt.GoogleJwtHandler
import ratpack.jackson.JacksonModule

import static ratpack.groovy.Groovy.ratpack

ratpack {
    bindings {
        add new JacksonModule()
    }

    handlers {
        prefix("auth") {
            handler chain(registry.get(GoogleJwtHandler))
        }
    }
}