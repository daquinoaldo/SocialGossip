package base;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class Translation {
    private static final String baseUrl = "https://api.mymemory.translated.net/get";
    
    public static String translate(String text, String sourceLang, String destLang) {
        if (
                text == null ||
                sourceLang == null ||
                destLang == null ||
                text.length() == 0 ||
                sourceLang.length() != 2 ||
                destLang.length() != 2
                )
            throw new IllegalArgumentException("Invalid text or language codes.");
    
        HttpURLConnection connection = null;
        try {
            // Build URL
            URI buildUrl = new URI(
                    "http", "api.mymemory.translated.net", "/get",
                    "q="+text+"&langpair=" + sourceLang + "|" + destLang, "");
            URL url = buildUrl.toURL();
            
            // Start connection and send request
            URLConnection uc=url.openConnection();
            uc.connect();
            
            // Read response, parse it as a JSON
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line = null;
            StringBuffer sb = new StringBuffer();
            while ((line=in.readLine()) != null) {
                sb.append(line);
            }
            
            // If JSON res code is 200, return the translated string
            JSONObject result = (JSONObject) (new JSONParser()).parse(sb.toString());
            if ((Long) result.get("responseStatus") == 200)
                return (String) ((JSONObject) result.get("responseData")).get("translatedText");
            
        }
        catch (URISyntaxException | MalformedURLException e) {
            System.err.println("Can't build a valid url from:");
            System.err.println("Text: " + text);
            System.err.println("Source lang: " + sourceLang);
            System.err.println("Dest lang: " + destLang);
            e.printStackTrace();
        }
        catch (IOException e) {
            System.err.println("Error while comunicating with translation server");
            e.printStackTrace();
        }
        catch (ParseException e) {
            System.err.println("Invalid JSON response from server");
            e.printStackTrace();
        }
        
        return null;
    }
}
