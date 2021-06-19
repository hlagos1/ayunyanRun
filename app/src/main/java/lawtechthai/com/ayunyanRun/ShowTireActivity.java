package lawtechthai.com.ayunyanRun;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ShowTireActivity extends AppCompatActivity {

    private static final int REQUEST_FINE_LOCATION = 22;
    private static final int REQUEST_CHECK_SETTINGS = 15;
    private String assetCodeResult;
    private String serialNumberResult;
    private String equipmentID;
    private String latitudeResult;
    private String longitudeResult;
    private String status;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tire);



        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView certificationStatusImageView = findViewById(R.id.showTire_certificationStatus);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            equipmentID = extras.getString("equipmentID");
            status = extras.getString("status");
        }

//        Intent i = getIntent();
//        Tire tire = (Tire)i.getSerializableExtra("tire");

        RequestQueue mQueue = Volley.newRequestQueue(ShowTireActivity.this);
        String url = "https://www.lawtechthai.com/Assets/showTire.php";
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("equipmentID", equipmentID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ProgressBar circle = findViewById(R.id.showTire_progress);
        circle.setVisibility(View.VISIBLE);
        JsonObjectRequest request;
        request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {
                    circle.setVisibility(View.GONE);
                    try {

                        boolean searchSuccess = response.getBoolean("success");
                        assetCodeResult = response.getString("assetCode");
                        serialNumberResult = response.getString("serialNumber");
                        String operationalStatusResult = response.getString("operationalStatus");
                        String assetTypeResult = response.getString("assetType");
                        String supplierNameResult = response.getString("supplierName");
                        String inspectionExpiryDateResult = response.getString("inspectionExpiryDate");
                        String ownerResult = response.getString("owner");
                        String makerResult = response.getString("maker");
                        String modelResult = response.getString("model");
                        String mileageResult = response.getString("mileage");
                        String maximumMileageSpecificationsResult = response.getString("maximumMileageSpecifications");
                        String serviceTypeResult = response.getString("serviceType");
                        String widthResult = response.getString("width");
                        String aspectRatioResult = response.getString("aspectRatio");
                        String constructionResult = response.getString("construction");
                        String rimDiameterResult = response.getString("rimDiameter");
                        String loadIndexResult = response.getString("loadIndex");
                        String speedRatingResult = response.getString("speedRating");
                        String treadRatingResult = response.getString("treadRating");
                        String tractionRatingResult = response.getString("tractionRating");
                        String temperatureRatingResult = response.getString("temperatureRating");
                        String dateOfManufactureResult = response.getString("dateOfManufacture");
                        String dateInServiceResult = response.getString("dateInService");
                        String vehicleEquipmentIDResult = response.getString("vehicleEquipmentID");
                        String wheelWellResult = response.getString("wheelWell");
                        String tirePressureRangeResult = response.getString("tirePressureRange");
                        String minimumTreadDepthResult = response.getString("minimumTreadDepth");
                        String treadDepthResult = response.getString("treadDepth");
                        latitudeResult = response.getString("latitude");
                        longitudeResult = response.getString("longitude");

                        TextView assetCodeTextView = findViewById(R.id.showTire_assetCode);
                        TextView serialNumberTextView = findViewById(R.id.showTire_serialNumber);
                        TextView supplierNameTextView = findViewById(R.id.showTire_supplierName);
                        TextView ownerTextView = findViewById(R.id.showTire_owner);
                        TextView makerTextView = findViewById(R.id.showTire_maker);
                        TextView modelTextView = findViewById(R.id.showTire_model);
                        TextView mileageTextView = findViewById(R.id.showTire_mileage);
                        TextView installedOnTextView = findViewById(R.id.showTire_installedOn);
                        TextView dateInServiceTextView = findViewById(R.id.showTire_dateInService);
                        TextView serviceTypeTextView = findViewById(R.id.showTire_serviceType);
                        TextView widthTextView = findViewById(R.id.showTire_width);
                        TextView aspectRatioTextView = findViewById(R.id.showTire_aspectRatio);
                        TextView constructionTextView = findViewById(R.id.showTire_construction);
                        TextView rimDiameterTextView = findViewById(R.id.showTire_rimDiameter);
                        TextView loadIndexTextView = findViewById(R.id.showTire_loadIndex);
                        TextView speedRatingTextView = findViewById(R.id.showTire_speedRating);
                        TextView tirePressureRangeTextView = findViewById(R.id.showTire_tirePressureRange);
                        TextView minimumThreadDepthTextView = findViewById(R.id.showTire_minimumThreadDepth);
                        TextView latitudeTextView = findViewById(R.id.showTire_latitude);
                        TextView longitudeTextView = findViewById(R.id.showTire_longitude);
                        ImageView supplierLogoImageView = findViewById(R.id.showTire_supplierLogo);


                        if (searchSuccess) {

                            assetCodeTextView.setText(assetCodeResult);
                            serialNumberTextView.setText(serialNumberResult);
                            supplierNameTextView.setText(supplierNameResult);
                            ownerTextView.setText(ownerResult);
                            makerTextView.setText(makerResult);
                            modelTextView.setText(modelResult);
                            mileageTextView.setText(mileageResult);
                            installedOnTextView.setText(vehicleEquipmentIDResult);
                            dateInServiceTextView.setText(dateInServiceResult);
                            serviceTypeTextView.setText(serviceTypeResult);
                            widthTextView.setText(widthResult);
                            aspectRatioTextView.setText(aspectRatioResult);
                            constructionTextView.setText(constructionResult);
                            rimDiameterTextView.setText(rimDiameterResult);
                            loadIndexTextView.setText(loadIndexResult);
                            speedRatingTextView.setText(speedRatingResult);
                            tirePressureRangeTextView.setText(tirePressureRangeResult);
                            minimumThreadDepthTextView.setText(minimumTreadDepthResult);
                            latitudeTextView.setText(latitudeResult);
                            longitudeTextView.setText(longitudeResult);

//                            checkTireCertificationValidity(nextInspectionDateResult, dateOfManufactureResult);

                            if (checkPermissions()) {
                                checkLocation();
                            }

                            switch (supplierNameResult) {

                                case "ACT":
                                    supplierLogoImageView.setBackgroundResource(R.drawable.act_logo);
                                    break;
                                case "B Quick":
                                    supplierLogoImageView.setBackgroundResource(R.drawable.bquick_logo);
                                    break;
                                case "Cockpit":
                                    supplierLogoImageView.setBackgroundResource(R.drawable.cockpit_logo);
                                    break;

                            }

                        } else {
                            Toast.makeText(this, "Could Not Find Asset", Toast.LENGTH_LONG).show();
                            certificationStatusImageView.setBackgroundResource(R.drawable.notok);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        certificationStatusImageView.setBackgroundResource(R.drawable.notok);

                    }
                }, error -> {
            circle.setVisibility(View.GONE);
            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
        });
        mQueue.getCache().clear();
        mQueue.add(request);

        Button mapViewButton = findViewById(R.id.showTire_map_search);
        mapViewButton.setOnClickListener(v -> {

                    Uri location = Uri.parse("geo:0,0?q=" + latitudeResult + "," + longitudeResult + "(" + assetCodeResult + "-" + serialNumberResult + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        startActivity(mapIntent);
                    }
                }
        );

        Button updateLocation = findViewById(R.id.showTire_updateLocation);
        updateLocation.setOnClickListener(v -> {
            if (checkPermissions()) {
                checkLocation();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.show_menu, menu);

        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);
        menu.getItem(2).setVisible(true);

        return true;

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.showMenu_addButton) {
//
//            RequestQueue queue = Volley.newRequestQueue(this);
//            String url = "https://www.lawtechthai.com/Assets/assetListAdd.php";
//            JSONObject parameter = new JSONObject();
//
//            try {
//                parameter.put("equipmentID", equipmentID);
//                if (inspectionStatus) {
//                    parameter.put("certification", "VALID");
//                } else {
//                    parameter.put("certification", "xxxxx EXPIRED xxxxx");
//                }
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            ProgressBar circle = findViewById(R.id.showTire_progress);
//            circle.setVisibility(View.VISIBLE);
//            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
//                    response -> {
//                        circle.setVisibility(View.GONE);
//                        try {
//                            boolean insertSuccess = response.getBoolean("success");
//                            if (insertSuccess) {
//                                Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show();
//
//                            } else {
//                                Toast.makeText(this, "Asset Already On The List", Toast.LENGTH_SHORT).show();
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }, error -> {
//                circle.setVisibility(View.GONE);
//                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
//            });
//
//            queue.add(request);
//
//        } else if (item.getItemId() == R.id.showMenu_listButton) {
//            Intent i = new Intent(this, AssetListActivity.class);
//            startActivity(i);
//            return true;
//        } else if (item.getItemId() == R.id.showMenu_searchButton) {
//            Intent i = new Intent(this, MainActivity.class);
//            startActivity(i);
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }
//        return true;
//    }


    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return false;
        }
    }

    public void checkLocation() {

        FusedLocationProviderClient mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        @SuppressLint("MissingPermission") Task locationRequest = mFusedLocationClient.getLastLocation();
        locationRequest.addOnFailureListener(this, error -> {
            Toast.makeText(this, "LocationError" + error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            if (error instanceof ResolvableApiException) {
                try {
                    ResolvableApiException r = (ResolvableApiException) error;
                    r.startResolutionForResult(this, REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                }
            }
        });

        locationRequest.addOnSuccessListener(this, (OnSuccessListener<Location>) mylocation -> {

            double assetLat = Double.parseDouble(latitudeResult);
            double assetLon = Double.parseDouble(longitudeResult);

            double myLat = mylocation.getLatitude();
            double myLon = mylocation.getLongitude();

            // lat1 and lng1 are the values of a previously stored location
            if (distance(assetLat, assetLon, myLat, myLon) > .1) { // if distance > 100 m do this

                builder = new AlertDialog.Builder(this);

                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage(R.string.updateLocation_message).setTitle(R.string.updateLocation_title);

                //Setting message manually and performing action on button click
                //builder.setMessage("Your Location is more than 100 m from the last known asset location")
                builder.setCancelable(false)
                        .setPositiveButton("Yes", (dialog, id) -> {

                            String newLat = Double.toString(myLat);
                            String newLon = Double.toString(myLon);
                            TextView latitudeTextView = findViewById(R.id.showTire_latitude);
                            TextView longitudeTextView = findViewById(R.id.showTire_longitude);
                            latitudeResult = newLat;
                            longitudeResult = newLon;

                            RequestQueue queue = Volley.newRequestQueue(this);
                            String url = "https://www.lawtechthai.com/Assets/updateAssetLocation.php";
                            JSONObject parameter = new JSONObject();

                            try {
                                parameter.put("assetCode", assetCodeResult);
                                parameter.put("serialNumber", serialNumberResult);
                                parameter.put("latitude", newLat);
                                parameter.put("longitude", newLon);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ProgressBar circle = findViewById(R.id.showTire_progress);
                            circle.setVisibility(View.VISIBLE);

                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                                    response -> {
                                        try {
                                            boolean insertSuccess = response.getBoolean("success");
                                            if (insertSuccess) {
                                                Toast toast = Toast.makeText(this, "Asset Location Updated", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10);
                                                latitudeTextView.setText(newLat);
                                                longitudeTextView.setText(newLon);
                                                toast.show();

                                            } else {
                                                Toast.makeText(this, "Could Not Update Asset Location", Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_SHORT).show();
                                        }

                                    }, error -> {
                                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

                            });
                            queue.add(request);
                            circle.setVisibility(View.GONE);

                        })
                        .setNegativeButton("No", (dialog, id) -> {
                            //  Action for 'NO' Button
                            dialog.cancel();
                            Toast.makeText(getApplicationContext(), "Location Update Cancelled",
                                    Toast.LENGTH_SHORT).show();
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
//                alert.setTitle("Clear Asset List");
                alert.show();
            } else {
                Toast.makeText(this, "Asset Location same as Scan Location", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371;

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }


    //create object
//                                        tire.setOperationalStatus(response.getString("operationalStatus"));
//                                    tire.setAssetType(response.getString("assetType"));
//                                    tire.setAssetCode(response.getString("assetCode"));
//                                    tire.setSerialNumber(response.getString("serialNumber"));
//                                    tire.setSupplierName(response.getString("supplierName"));
//                                    tire.setInspectionExpiryDate(response.getString("inspectionExpiryDate"));
//                                    tire.setOwner(response.getString("owner"));
//                                    tire.setMaker(response.getString("maker"));
//                                    tire.setModel(response.getString("model"));
//                                    tire.setMileage(Integer.parseInt(response.getString("mileage")));
//                                    tire.setMaximumMileageSpecifications(Integer.parseInt(response.getString("maximumMileageSpecifications")));
//                                    tire.setServiceType(response.getString("serviceType"));
//                                    tire.setWidth(response.getString("width"));
//                                    tire.setAspectRatio(response.getString("aspectRatio"));
//                                    tire.setConstruction(response.getString("construction"));
//                                    tire.setRimDiameter(response.getString("rimDiameter"));
//                                    tire.setLoadIndex(response.getString("loadIndex"));
//                                    tire.setSpeedRating(response.getString("speedRating"));
//                                    tire.setTreadRating(response.getString("treadRating"));
//                                    tire.setTractionRating(response.getString("tractionRating"));
//                                    tire.setTemperatureRating(response.getString("temperatureRating"));
//                                    tire.setDateOfManufacture(response.getString("dateOfManufacture"));
//                                    tire.setDateInService(response.getString("dateInService"));
//                                    tire.setVehicleEquipmentID(response.getString("vehicleEquipmentID"));
//                                    tire.setLatitude(Double.parseDouble(response.getString("latitude")));
//                                    tire.setLongitude(Double.parseDouble(response.getString("longitude")));
//                                    tire.setWheelWell(response.getString("wheelWell"));
//                                    tire.setTirePressureRange(response.getString("tirePressureRange"));
//                                    tire.setMinimumTreadDepthSpecifications(Double.parseDouble(response.getString("minimumTreadDepthSpecifications")));
//                                    tire.setTreadDepth(Double.parseDouble(response.getString("treadDepth")));


}