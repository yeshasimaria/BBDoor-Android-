package sg.businessbuddy.yesha.bbdoor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class OpenDoorActivity extends AppCompatActivity {

    private static final String INT_OFFICE_DOOR = "1";
    private static final String INT_MEETING_ROOM_DOOR = "3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_door);
    }

    public void openOfficeDoor(View view) {
        new AccessDoor(INT_OFFICE_DOOR).execute();
    }

    public void openMeetingRoomDoor(View view) {
        new AccessDoor(INT_MEETING_ROOM_DOOR).execute();
    }

    private class AccessDoor extends AsyncTask<String, Void, Void> {
        private static final String URL_BEFORE_IP_ADDRESS = "http://";
        private static final String URL_AFTER_IP_ADDRESS = ":3480/data_request?id=action" +
                "&serviceId=urn:micasaverde-com:serviceId:HomeAutomationGateway1" +
                "&action=RunScene&SceneNum=";
        private static final String URL_FOR_IP_ADDRESS =  "https://sta1.mios.com/" +
                "locator_json.php?username=xxx";
        private static final String REQUEST_METHOD_GET = "GET";
        private static final String UNIT_SERIAL_NUMBER = "30105938";
        private static final String JSON_NAME_UNITS = "units";
        private static final String JSON_NAME_SERIAL_NUMBER = "serialNumber";
        private static final String JSON_NAME_IP_ADDRESS = "ipAddress";


        private final String doorNum;
        private String ipAddress;

        public AccessDoor (String doorNum) {
            this.doorNum = doorNum;
        }

        @Override
        protected Void doInBackground(String... params) {
            getIPAddress();
            getDoor();
            return null;
        }

        private void getIPAddress() {
            URL url = convertToUrl(URL_FOR_IP_ADDRESS);
            HttpURLConnection httpURLConnection = null;
            int responseCode = -1;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod(REQUEST_METHOD_GET);
                httpURLConnection.connect();

                responseCode = httpURLConnection.getResponseCode();

                if (responseCode == httpURLConnection.HTTP_OK) {
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    inputStream.close();

                    String result = stringBuilder.toString();

                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray unitsArray = jsonObject.getJSONArray(JSON_NAME_UNITS);
                    for (int i = 0; i < unitsArray.length(); i++) {
                        jsonObject = unitsArray.getJSONObject(i);
                        String serialNumber = jsonObject.getString(JSON_NAME_SERIAL_NUMBER);
                        if (serialNumber.equals(UNIT_SERIAL_NUMBER)) {
                            ipAddress = jsonObject.getString(JSON_NAME_IP_ADDRESS);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
            }
        }

        private void getDoor() {
            URL url = convertToUrl(URL_BEFORE_IP_ADDRESS + ipAddress + URL_AFTER_IP_ADDRESS + doorNum);
            HttpURLConnection httpURLConnection = null;
            int responseCode = -1;
            StringBuilder stringBuilder = new StringBuilder();
            try {
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod(REQUEST_METHOD_GET);
                httpURLConnection.connect();

                responseCode = httpURLConnection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
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
