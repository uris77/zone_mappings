package org.pasmo.locations

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject
import groovy.transform.CompileStatic
import org.pasmo.datastore.MongoDbClient

import javax.inject.Inject

@CompileStatic
class LocationsMongoImpl implements Locations {
    private final MongoDbClient mongoDbClient
    private final String COLLECTION_NAME = "pasmo_locations"

    @Inject
    LocationsMongoImpl(MongoDbClient mongoDbClient) {
        this.mongoDbClient = mongoDbClient
    }

    @Override
    List<Location> allAvailable() {
        DBCollection collection = mongoDbClient.getCollection(COLLECTION_NAME)
        List<Location> locations = []
        DBCursor cursor = collection.find(new BasicDBObject("deleted", false))
        try {
            while(cursor.hasNext()) {
                locations << createLocation(cursor.next())
            }
        } finally {
            cursor.close()
        }
        return locations
    }

    Location createLocation(DBObject doc) {
        new Location(
                name: doc.get("name") as String,
                district: doc.get("district") as String,
                locationType: doc.get("locationType") as String,
                loc: doc.get("loc") as Map
        )
    }
}
