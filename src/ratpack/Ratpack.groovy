import static ratpack.groovy.Groovy.ratpack

import ratpack.jackson.JacksonModule

ratpack {
    bindings {
        add new JacksonModule()
    }
}