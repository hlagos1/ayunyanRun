package lawtechthai.com.ayunyanRun;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FetchAddressIntentService extends IntentService {


    protected ResultReceiver mReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FetchAddressIntentService(String name) {
        super(name);
    }

    public FetchAddressIntentService() {
        super("com.veriapp.FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        Location location = intent.getParcelableExtra("location");
        mReceiver = intent.getParcelableExtra("receiver");


        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.isEmpty()) {
                //no results
            } else {

                ArrayList<String> lines = new ArrayList<>();
                for (int i = 0; i <= addresses.get(0).getMaxAddressLineIndex(); i++) {
                    lines.add(addresses.get(0).getAddressLine(i));

                }

                Bundle bundle = new Bundle();
                bundle.putStringArrayList("address", lines);
                mReceiver.send(0, bundle);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
