package choongyul.android.com.retrofit2study;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by myPC on 2017-04-11.
 */

public class Remote {

    public static String postJson(String siteUrl, String data) {
        String result = "";

        if( !siteUrl.startsWith("http")) {
            siteUrl = "http://" + siteUrl;
        }

        try {
            URL url = new URL(siteUrl);
            url.openConnection();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");

            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();

            os.write(data.getBytes());

            os.flush();
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                StringBuilder builderResult = new StringBuilder();
                String lineOfData = "";
                while ((lineOfData = br.readLine()) != null) {
                    builderResult.append(lineOfData);
                }
                connection.disconnect();
                return builderResult.toString();
            } else {
                Log.e("HTTPConnection", " Error code = " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
