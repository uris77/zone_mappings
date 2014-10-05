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
    private final String HOTSPOT_COLOR = "#BE1162"
    private final String TRADITIONAL_COLOR = "#11A132"
    private final String NON_TRADITIONAL_COLOR = "#10BB33"

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
        String locationType = doc.get("locationType") as String
        new Location(
                name: doc.get("name") as String,
                district: doc.get("district") as String,
                locationType: locationType,
                geometry: doc.get("loc") as Map,
                properties: [
                        title: doc.get("name") as String,
                        'marker-size': 'large',
                        'marker-color': markerColor(locationType),
                        'marker-symbol': 'building'
                ]
        )
    }

    private String markerColor(String locationType) {
        String markerColor = HOTSPOT_COLOR
        switch(locationType) {
            case "Hotspot":
                markerColor = HOTSPOT_COLOR
                break
            case "traditional":
                markerColor = TRADITIONAL_COLOR
                break
            case "Non-Traditional":
                markerColor = NON_TRADITIONAL_COLOR
                break
        }
        markerColor
    }
}
