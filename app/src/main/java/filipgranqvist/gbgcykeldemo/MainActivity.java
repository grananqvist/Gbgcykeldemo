package filipgranqvist.gbgcykeldemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;


public class MainActivity extends AppCompatActivity {

    //variable that stores if requesting for hämta or lämna cykel
    public static int request_id;

    //amount of bikes requested
    public static int bike_amount;

    //List of stations that meets requirements
    public static LinkedList<String> stations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hämta 1 cykel by default
        ((RadioButton)findViewById(R.id.cykel_hamta)).setChecked(true);
        request_id = 0;
        bike_amount = 1;

    }

    public void onRadioButtonClicked(View view) {

        //is clicked button checked?
        boolean checked = ((RadioButton) view).isChecked();

        RadioButton btn_hamta = (RadioButton)findViewById(R.id.cykel_hamta);
        RadioButton btn_lamna = (RadioButton)findViewById(R.id.cykel_lamna);

        //Check which radio button was clicked
        switch(view.getId()) {
            case R.id.cykel_hamta:
                if (checked)
                    btn_lamna.setChecked(false);
                    request_id = 0;
                    break;
            case R.id.cykel_lamna:
                if (checked)
                    btn_hamta.setChecked(false);
                    request_id = 1;
                    break;
        }
    }

    public void onConfirmClicked(View view) {

        bike_amount = Integer.parseInt(((EditText)findViewById(R.id.cyklar)).getText().toString());

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {

                    //send a http GET request to request for bike stations
                    URL url = new URL("http://data.goteborg.se/SelfServiceBicycleService/v1.0/Stations/8d055041-f608-4bd8-8a83-aa5de806e78a?format=Json");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");

                    //build a string from HTTP response
                    BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder strBuilder = new StringBuilder(2048);
                    String responseStr;
                    while ((responseStr = reader.readLine()) != null)
                        strBuilder.append(responseStr);


                    //end connection
                    urlConnection.disconnect();

                    JSONArray jsonResponse = new JSONArray( strBuilder.toString().trim() );
                    stations = new LinkedList<String>();

                    //iterate through Json array
                    for(int i = 0; i < jsonResponse.length(); i++) {

                        JSONObject row = new JSONObject(jsonResponse.getString(i));

                        try {

                            if (request_id == 0 && Integer.parseInt(row.get("AvailableBikes").toString()) > bike_amount) {
                                //request to hämta x bikes
                                stations.add(row.get("Name").toString());
                            } else if (request_id == 1 && Integer.parseInt(row.get("AvailableBikeStands").toString()) > bike_amount) {
                                //request to lämna x bikes
                                stations.add(row.get("Name").toString());
                            }
                        } catch (Exception e) {
                            //Index AvailableBikes or AvailableBikeStands not available for particular row, ignore
                        }
                    }


                    startActivity(new Intent(MainActivity.this,SecondActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });

        thread.start();

    }

}
