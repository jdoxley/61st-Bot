import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

public class Webhook{
    private String url;
    Webhook(String url){
        this.url=url;
    }

    public HttpResponse<JsonNode> getRequset(String name, Boolean sl){
        return Unirest.get(url)
                .queryString("user",name)
                .queryString("sl",sl)
                .asJson();
    }

    public HttpResponse<JsonNode> postRequest(int row){
        return Unirest.post(url)
                .queryString("row",row)
                .asJson();
    }
}