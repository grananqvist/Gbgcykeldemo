package filipgranqvist.gbgcykeldemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class SecondActivity extends AppCompatActivity {

    public static ListView second_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //print header text
        ((TextView)findViewById(R.id.second_header)).setText("Du kan " +
                ((MainActivity.request_id == 0) ? "hämta" : "lämna" ) +
                " dina " + MainActivity.bike_amount + " cyklar vid dessa stationer:");


        //Print available stations into a listview

        second_listview = (ListView)findViewById(R.id.second_listview);

        String[] stations = MainActivity.stations.toArray(new String[MainActivity.stations.size()]);

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, stations);
        second_listview.setAdapter(adapter);




    }
}
