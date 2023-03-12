package backend;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class BiometricAuthentication
{
    private final String authentication_api_host;
    private final int authentication_api_port;
    private final String authentication_api_app_key;

    public BiometricAuthentication(String authentication_api_host, int authentication_api_port, String authentication_api_app_key)
    {
        this.authentication_api_host = authentication_api_host;
        this.authentication_api_port = authentication_api_port;
        this.authentication_api_app_key = authentication_api_app_key;
    }

    public String register(String token_value, String email_address, String first_name, String last_name)
    {
        try
        {
            URL url = new URL("http", authentication_api_host, authentication_api_port, "/api/registration");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url.toURI())
                    .setHeader("Accept", "application/json")
                    .setHeader("Content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            "{" +
                            "    \"app_key\": \"" + authentication_api_app_key +"\"," +
                            "    \"token_value\": \"" + token_value + "\"," +
                            "    \"user_mail_address\": \"" + email_address + "\"," +
                            "    \"user_first_name\": \"" + first_name + "\"," +
                            "    \"user_last_name\": \"" + last_name + "\"" +
                            "}"))
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200)
            {
                return "200\nOK";
            }
            else
            {
                return response.body().split(",")[1].replace("\"", "").replace("status:", "")
                        + "\n"
                        + response.body().split(",")[3].replace("\"", "").replace("message:", "");
            }
        }
        catch (URISyntaxException | IOException | InterruptedException e)
        {
            return e.getMessage();
        }
    }

    public String login(String token_value, String email_address)
    {
       try
       {
           URL url = new URL("http", authentication_api_host, authentication_api_port, "/api/login");
           HttpRequest request = HttpRequest.newBuilder()
                   .uri(url.toURI())
                   .setHeader("Accept", "application/json")
                   .setHeader("Content-type", "application/json")
                   .POST(HttpRequest.BodyPublishers.ofString(
                           "{" +
                                   "    \"app_key\": \"" + authentication_api_app_key +"\"," +
                                   "    \"token_value\": \"" + token_value + "\"," +
                                   "    \"user_mail_address\": \"" + email_address + "\"" +
                                   "}"))
                   .build();

           HttpClient client = HttpClient.newHttpClient();
           HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

           if(response.statusCode() == 200)
           {
               return "200\nOK";
           }
           else
           {
               return response.body().split(",")[1].replace("\"", "").replace("status:", "")
                       + "\n"
                       + response.body().split(",")[3].replace("\"", "").replace("message:", "");
           }
       }
       catch (URISyntaxException | IOException | InterruptedException e)
       {
           return e.getMessage();
       }
    }
}
