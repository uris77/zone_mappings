package org.pasmo.locations

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject
import org.pasmo.datastore.MongoDbClient

class LocationsMongoImpl implements Locations {
    private final MongoDbClient mongoDbClient
    private final String COLLECTION_NAME = "pasmo_locations"

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
        return null
    }

    Location createLocation(DBObject doc) {
        new Location(
                name: doc.get("name"),
                district: doc.get("district"),
                locationType: doc.get("locationType"),
                loc: doc.get("loc") as Map
        )
    }
}
