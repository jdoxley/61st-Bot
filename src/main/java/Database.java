import Models.LOA;
import com.mewna.catnip.entity.builder.EmbedBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class Database {
    final static Class<? extends List> dozClazz = new ArrayList<Long>().getClass();
    private static Dotenv dotenv = Dotenv.load();
    public static MongoClient mongoClient = MongoClients.create(
            "mongodb+srv://dbUser:"+dotenv.get("MONGODB_PWD")+"@discord-bot-ipot8.gcp.mongodb.net/test?retryWrites=true&w=majority");
    public static MongoDatabase database = mongoClient.getDatabase("61st-dev");

    public static String createDocument(MongoCollection<Document> collection, LOA loa) {
        Document doc = new Document();
        doc.put("userId", loa.getUserId());
        doc.put("startDate", loa.getStartDate());
        doc.put("endDate", loa.getEndDate());
        doc.put("reason",loa.getReason());
        doc.put("needsApprovedBy",loa.getNeedApprovedBy());
        doc.put("approvedBy",loa.getApprovedBy());
        collection.insertOne(doc);
        return doc.toString();
    }

    public static String updateDocument(MongoCollection<Document> collection, LOA loa, String key, Object value) {
        loa.setId(searchCollection(collection, key,value).next().getObjectId("_id"));
        Document doc = new Document();
        doc.put("_id", loa.getId());
        doc.put("userId", loa.getUserId());
        doc.put("startDate", loa.getStartDate());
        doc.put("endDate", loa.getEndDate());
        doc.put("reason",loa.getReason());
        doc.put("needsApprovedBy",loa.getNeedApprovedBy());
        doc.put("approvedBy",loa.getApprovedBy());
        return collection.replaceOne(getSearchQuery(key, value),doc).toString();
    }

    public static String deleteDocument(MongoCollection<Document>collection, String key, Object value){
        return collection.deleteOne(getSearchQuery(key, value)).toString();
    }

    public static Document getSearchQuery(String key, Object value) {
        return new Document(key, value);
    }

    public static MongoCursor<Document> searchCollection(MongoCollection<Document> collection, String key, Object value) {
        return collection.find(getSearchQuery(key, value)).cursor();
    }

    public static LOA getLOA(MongoCollection<Document> collection, ObjectId id) {
        LOA loa = new LOA();
        Document loaDoc = searchCollection(collection, "_id", id).tryNext();
        loa.setId(id);
        loa.setUserId(loaDoc.getLong("userId"));
        loa.setStartDate(loaDoc.getDate("startDate"));
        loa.setEndDate(loaDoc.getDate("endDate"));
        loa.setReason(loaDoc.getString("reason"));
        loa.setNeedApprovedBy(loaDoc.get("needsApprovedBy", dozClazz));
        loa.setApprovedBy(loaDoc.get("approvedBy", dozClazz));
        return loa;
    }



}
