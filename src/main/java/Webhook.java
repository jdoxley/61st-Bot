import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class Webhook {
    public HttpURLConnection connection;

    Webhook() {
        try {
            URL url = new URL("https://script.google.com/macros/s/AKfycbxXVD3EadkJEUkwVeLp8iLY49QuKhS0SR5pLlONmZUSZUNHq84/exec");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Language", "en-US");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public StringBuffer sendMessage(String msg) {
        DataOutputStream wr = null;
        try {
            wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(msg);
            wr.close();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuffer response = new StringBuffer();
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println(response);
            if (connection != null) {
                connection.disconnect();
            }
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
