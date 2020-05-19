import Models.LOA;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.*;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.*;

public class Test {
    public static void main(String[] args){
//        MongoCollection<Document> collection = Database.database.getCollection("loa");
//        System.out.println(Database.searchCollection(collection,"userId",168589151594086400L).next());
//        Database.mongoClient.close();
        Webhook webhook = new Webhook("https://script.google.com/macros/s/AKfycbxXVD3EadkJEUkwVeLp8iLY49QuKhS0SR5pLlONmZUSZUNHq84/exec");

        String name = "X. Dover \"Thunder\"";
        List<String> $n = new ArrayList<>(Arrays.asList(name.split("\"")));
        $n = new ArrayList<>(Arrays.asList($n.get(0).split(" ")));
        System.out.println($n);
        if ($n.get(0).length()!=2) $n.remove(0);
        String $n2 = String.join(" ",$n);
        System.out.println($n2);
        JsonNode response = webhook.getRequset($n2).getBody();
        System.out.println(response.toPrettyString());
        System.out.println(webhook.postRequest(20).getStatusText());

    }

    private static LOA createLOA(Date startDate, Date endDate, long id) {
        LOA l = new LOA();
        l.setUserId(id);
        l.setStartDate(startDate);
        l.setEndDate(endDate);
        return l;
    }


}
