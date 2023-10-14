import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;

public class HttpGet {

   private URL url;
   private byte[] data;

   private URL check(String urlString) {
      try {
         URI uri = new URI(urlString);
         return uri.toURL();
      } catch (URISyntaxException | IllegalArgumentException | MalformedURLException e) {
         return null;
      }
   }

   public HttpGet(String urlString) {
      this.url = check(urlString);
   }

   public void httpRequest(String string) {
      HttpURLConnection connection = null;
      try {
         //System.out.println("Requesting URL: " + this.url);
         connection = (HttpURLConnection) this.url.openConnection();
         connection.setRequestMethod("GET");

         if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String tmp = new String();
            StringBuffer response = new StringBuffer();

            while ((tmp = input.readLine()) != null) {
               response.append(tmp);
               response.append("\r\n");
            }
            //System.out.println("Received HTTP response: " + response.toString());

            data = Base64.getEncoder().encode(response.toString().getBytes());
            input.close();
         }
      } catch (IOException e) {
         e.printStackTrace();
      } finally {
         if (connection != null) {
            connection.disconnect();
         }
      }

   }

   public byte[] SendData() throws IOException {
      return data;
   }
}
