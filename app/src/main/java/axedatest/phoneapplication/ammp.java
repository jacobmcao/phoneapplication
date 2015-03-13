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


public class ammp extends AsyncTask<AmmpClient, Void, Void> {
    public static boolean IS_RUNNING = true;


    @Override
    protected Void doInBackground(AmmpClient... ammpClients) {
        AmmpClient client = ammpClients[0];
        Transmission transmission = new Transmission();
        transmission.add(createAlarm("testAlarm", 500));
        transmission.add(createDataItemSet());
        transmission.add(createEvent("test123", "this is a test"));
        transmission.add(createLocation(50.5, 45.5, 1002.2));
        client.register(10);
        AmmpResult result = client.send(transmission);
        if (result.hasEgress()) {
            try {
                handleEgress(client, result.egressNotification());
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
        while (IS_RUNNING) {
            result = client.poll();
            if (result.hasEgress()) {
                try {
                    handleEgress(client, result.egressNotification());
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            } else {
                System.out.println("No egress available.");
            }
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }
        }
        return null;
    }

    static AmmpClient buildClient() {
        Endpoint ep = new Endpoint(Endpoint.HTTP, "windriver.axeda.com", 52689);
        CommunicatingAsset asset = new CommunicatingAsset("toolkitTest",
                "helloWorld_123" + new Date().getTime());
        return AmmpClient.getInstance(ep, asset);

    }

    public static Alarm createAlarm(String name, Integer severity) {
        return new Alarm.Builder(name, severity)
                .acquisitionTime(new Date())
                .build();
    }
    public static DataItemSet createDataItemSet() {
        return new DataItemSet.Builder("stringDataItem", "str test")
                .addDataItem("analogTest", 1234.567890)
                .addDataItem("digitalTest", true)
                .acquisitionTime(new Date())
                .build();
    }
    public static Event createEvent(String name, String description) {
        return new Event.Builder(name, description)
                .acquisitionTime(new Date())
                .build();
    }
    public static CoordinateLocation createLocation(double lat, double lon, double
            alt) {
        return new CoordinateLocation.Builder(lat, lon)
                .altitude(alt)
                .build();
    }
    static void handleEgress(AmmpClient client, EgressNotification noti) throws
            IOException
    {
        for (SetDataItemsRequest set : noti.setRequests()) {
            System.out.println(set.toString());
        }
        for (SendDataItemsRequest send : noti.sendRequests()) {
            System.out.println(send.toString());
        }
        for (com.axeda.toolkit.ammp.Package p : noti.packages()) {
            client.updatePackageStatus(PackageStatus.queued(p.id()));
            for (PackageInstruction pi : p.instructions()) {
                FileDownload fd = client.downloadPackageFile(pi);
                processFileDownload(fd);
            }
            client.updatePackageStatus(PackageStatus.success(p.id()));
        }
    }
    static void processFileDownload(FileDownload fd) throws IOException {
        int read;
        byte[] buff = new byte[64 * 1024];
        InputStream is = null;
        try {
            is = fd.content();
            StringBuilder sb = new StringBuilder();
            while ((read = is.read(buff)) != -1) {
                if (read < 64 * 1024) {
                    buff = Arrays.copyOf(buff, read);
                }
                sb.append(new String(buff));
            }


            System.out.println("Contents of the file download: ");
            // Large files may cause OOM errors
            System.out.println(sb.toString());
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            if (is != null) {
                is.close();
            }
        }

    }
}

