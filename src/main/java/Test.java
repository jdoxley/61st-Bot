import Models.LOA;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.Calendar;
import java.util.Date;

public class Test {
    public static void main(String[] args){
        MongoCollection<Document> collection = Database.database.getCollection("loa");
        System.out.println(Database.searchCollection(collection,"userId",168589151594086400L).next());
        Database.mongoClient.close();

    }

    private static LOA createLOA(Date startDate, Date endDate, long id) {
        LOA l = new LOA();
        l.setUserId(id);
        l.setStartDate(startDate);
        l.setEndDate(endDate);
        return l;
    }


}
