package org.pasmo.surveys

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject
import groovy.transform.CompileStatic
import org.pasmo.datastore.MongoDbClient

import javax.inject.Inject

@CompileStatic
class SurveysMongoImpl implements Surveys{
    private final MongoDbClient mongoDbClient

    @Inject
    SurveysMongoImpl(MongoDbClient mongoDbClient) {
        this.mongoDbClient = mongoDbClient
    }

    @Override
    List<Map<String, String>> findAllBy(Map<String, String> survey, String outletType) {
        List<Map<String, String>> surveys = []
        DBCollection surveyCollection = collectionByOutlet(outletType)
        BasicDBObject queryDoc = new BasicDBObject("survey.year", survey.year)
        queryDoc.append("survey.month", survey.month)
        DBCursor cursor = surveyCollection.find(queryDoc)
        try {
            while(cursor.hasNext()) {
                surveys << createSurvey(cursor.next())
            }
        } finally {
            cursor.close()
        }
        surveys
    }

    private Map<String, String>createSurvey(DBObject doc) {
        [
                id: doc.get("_id").toString(),
                targetPopulations: doc.get("targetPopulations"),
                outreach: doc.get("outreach"),
                condomsAvailable: doc.get("condomsAvailable"),
                lubesAvailable: doc.get("lubesAvailable"),
                gigi: doc.get("gigi"),
                locationName: (doc.get("location") as Map).name,
                district: (doc.get("location") as Map).district,
                loc: (doc.get("location") as Map).loc
        ]
    }

    private DBCollection collectionByOutlet(String outletType) {
        String collectionType = ""
        switch(outletType.toLowerCase()) {
            case "traditional":
                collectionType = "traditional_outlet_surveys"
                break
            case "nonTraditional":
                collectionType = "nontraditional_outlet_surveys"
                break
            case "hotspot":
                collectionType = "hotspot_surveys"
                break
        }
        mongoDbClient.getCollection(collectionType)
    }


}
