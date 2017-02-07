package sg.businessbuddy.yesha.bbdoor;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openOfficeDoor(View view) {
        new GetClass(this).execute("http://192.168.5.152:3480/data_request?" +
                "id=action&serviceId=urn:micasaverde-com:" +
                "serviceId:HomeAutomationGateway1" +
                "&action=RunScene&SceneNum=1");
    }

    public void openMeetingRoomDoor(View view) {
        new GetClass(this).execute("http://192.168.5.152:3480/data_request?" +
                "id=action&serviceId=urn:micasaverde-com:" +
                "serviceId:HomeAutomationGateway1" +
                "&action=RunScene&SceneNum=3");
    }

    private class GetClass extends AsyncTask<String, Void, String> {

        Context context;

        public GetClass (Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... params) {
            return getDoor(params[0]);
        }

        public String getDoor(String URL_string) {
            URL url = convertToUrl(URL_string);
            HttpURLConnection httpURLConnection = null;
            int responseCode = -1;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                System.out.println("I'm here!");
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                responseCode = httpURLConnection.getResponseCode();

                if (responseCode == httpURLConnection.HTTP_OK) {
                    System.out.println("I'm here! 2");
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null)
                httpURLConnection.disconnect();
            }
             return stringBuilder.toString();
        }

        private URL convertToUrl(String url_string) {
            try {
                URL url = new URL(url_string);
                URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(),
                        url.getPort(), url.getPath(), url.getQuery(), url.getRef());
                url = uri.toURL();
                return url;
            } catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

    }
}
