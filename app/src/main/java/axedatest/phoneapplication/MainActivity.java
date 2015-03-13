package axedatest.phoneapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.axeda.toolkit.ammp.AmmpClient;
import com.axeda.toolkit.ammp.AmmpResult;
import com.axeda.toolkit.ammp.CommunicatingAsset;
import com.axeda.toolkit.ammp.EgressNotification;
import com.axeda.toolkit.ammp.Endpoint;
import com.axeda.toolkit.ammp.FileDownload;
import com.axeda.toolkit.ammp.PackageInstruction;
import com.axeda.toolkit.ammp.SendDataItemsRequest;
import com.axeda.toolkit.ammp.SetDataItemsRequest;
import com.axeda.toolkit.ammp.domain.Alarm;
import com.axeda.toolkit.ammp.domain.CoordinateLocation;
import com.axeda.toolkit.ammp.domain.DataItemSet;
import com.axeda.toolkit.ammp.domain.Event;
import com.axeda.toolkit.ammp.domain.PackageStatus;
import com.axeda.toolkit.ammp.domain.Transmission;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;

import android.os.AsyncTask;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Endpoint ep = new Endpoint(Endpoint.HTTP, "windriver.axeda.com", 443);
        CommunicatingAsset asset = new CommunicatingAsset("toolkitTest", "helloWorld_123");
        AmmpClient client = AmmpClient.getInstance(ep, asset);
        Transmission transmission = new Transmission();
        Event event = new Event.Builder("Hello World","This is a test")
                .acquisitionTime(new Date())
                .priority(100)
                .build();
        transmission.add(event);
        System.out.println("SENDING DATA");
        client.send(transmission);
        System.out.println("SENDING DATA AGAIN");
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
