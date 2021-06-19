package lawtechthai.com.ayunyanRun;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import android.support.v7.widget.SwitchCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.Calendar;
import java.util.List;

public class EditVehicleActivity extends AppCompatActivity {

    private String vehicleCode;
    private String vehicleNumber;
    private EditText vehicleCodeEditText;
    private EditText vehicleNumberEditText;
    private String inspectorName;
    private String jobTitle;
    private String certifyingCompany;
    private String latitudeResult;
    private String longitudeResult;
    private EditText latitudeEditText;
    private EditText longitudeEditText;
    private Calendar calendar;
    private int year;
    private int month;
    private int dayOfMonth;
    private static final int REQUEST_FINE_LOCATION = 22;
    private static final int REQUEST_CHECK_SETTINGS = 15;
    private AlertDialog.Builder builder;
    private DatePickerDialog certDatePickerDialog;
    private DatePickerDialog expiryDatePickerDialog;
    private DatePickerDialog registrationDatePickerDialog;
    private DatePickerDialog insuranceExpiryDatePickerDialog;
    private TextView certificationDateTextView;
    private TextView inspectionExpiryDateTextView;
    private TextView registrationExpiryDateTextView;
    private TextView insuranceExpiryDateTextView;
    private EditText odometerEditText;

    private EditText beltRemarksEditText;
    private EditText hoseRemarksEditText;
    private EditText oilLeakRemarksEditText;
    private EditText airconRemarksEditText;
    private EditText wiperRemarksEditText;
    private EditText headHiRemarksEditText;
    private EditText headLoRemarksEditText;
    private EditText drivingLightsRemarksEditText;
    private EditText turnSignalRemarksEditText;
    private EditText brakeLightsRemarksEditText;
    private EditText hazardLightsRemarksEditText;
    private EditText doorLocksRemarksEditText;
    private EditText windshieldRemarksEditText;
    private EditText windowsRemarksEditText;
    private EditText radioRemarksEditText;
    private EditText hornRemarksEditText;
    private EditText fireExtinguisherRemarksEditText;
    private EditText firstAidRemarksEditText;
    private EditText coolantRemarksEditText;
    private EditText engineOilRemarksEditText;
    private EditText transmissionOilRemarksEditText;
    private EditText powerSteeringRemarksEditText;
    private EditText brakeFluidRemarksEditText;
    private EditText washerFluidRemarksEditText;

    private SwitchCompat beltSwitch;
    private SwitchCompat hoseSwitch;
    private SwitchCompat oilLeakSwitch;
    private SwitchCompat airconSwitch;
    private SwitchCompat wiperSwitch;
    private SwitchCompat headHiSwitch;
    private SwitchCompat headLoSwitch;
    private SwitchCompat drivingLightsSwitch;
    private SwitchCompat turnSignalSwitch;
    private SwitchCompat brakeLightsSwitch;
    private SwitchCompat hazardLightsSwitch;
    private SwitchCompat doorLocksSwitch;
    private SwitchCompat windshieldSwitch;
    private SwitchCompat windowsSwitch;
    private SwitchCompat radioSwitch;
    private SwitchCompat hornSwitch;
    private SwitchCompat fireExtinguisherSwitch;
    private SwitchCompat firstAidSwitch;
    private SwitchCompat coolantSwitch;
    private SwitchCompat engineOilSwitch;
    private SwitchCompat transmissionOilSwitch;
    private SwitchCompat powerSteeringSwitch;
    private SwitchCompat brakeFluidSwitch;
    private SwitchCompat washerFluidSwitch;
    private static int SCAN_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_vehicle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        vehicleCodeEditText = findViewById(R.id.editVehicle_vehicleCode);
        vehicleNumberEditText = findViewById(R.id.editVehicle_vehicleNumber);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            inspectorName = extras.getString("inspectorName");
            jobTitle = extras.getString("jobTitle");
            certifyingCompany = extras.getString("certifyingCompany");
        }

        Button scan = findViewById(R.id.editVehicle_scan);
        scan.setOnClickListener(v -> scanCode());

        Button search = findViewById(R.id.editVehicle_search);
        search.setOnClickListener(v -> {

            vehicleCode = vehicleCodeEditText.getText().toString();
            vehicleNumber = vehicleNumberEditText.getText().toString();
            search(vehicleCode, vehicleNumber);

        });


        certificationDateTextView = findViewById(R.id.editVehicle_certificationDate);
        certificationDateTextView.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            certDatePickerDialog = new DatePickerDialog(this,
                    (datePicker, year, month, day) -> certificationDateTextView.setText(day + "/" + (month + 1) + "/" + year), year, month, dayOfMonth);
            certDatePickerDialog.show();
        });

        inspectionExpiryDateTextView = findViewById(R.id.editVehicle_inspectionExpiryDate);
        inspectionExpiryDateTextView.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            expiryDatePickerDialog = new DatePickerDialog(this,
                    (datePicker, year, month, day) -> inspectionExpiryDateTextView.setText(day + "/" + (month + 1) + "/" + year), year, month, dayOfMonth);
            expiryDatePickerDialog.show();
        });


        registrationExpiryDateTextView = findViewById(R.id.editVehicle_registrationExpiryDate);
        registrationExpiryDateTextView.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            registrationDatePickerDialog = new DatePickerDialog(this,
                    (datePicker, year, month, day) -> registrationExpiryDateTextView.setText(day + "/" + (month + 1) + "/" + year), year, month, dayOfMonth);
            registrationDatePickerDialog.show();
        });

        insuranceExpiryDateTextView = findViewById(R.id.editVehicle_insuranceExpiryDate);
        insuranceExpiryDateTextView.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            insuranceExpiryDatePickerDialog = new DatePickerDialog(this,
                    (datePicker, year, month, day) -> insuranceExpiryDateTextView.setText(day + "/" + (month + 1) + "/" + year), year, month, dayOfMonth);
            insuranceExpiryDatePickerDialog.show();
        });

        ImageView mapViewButton = findViewById(R.id.editVehicle_mapDisplay);
        mapViewButton.setOnClickListener(v -> {

                    String latitude = latitudeEditText.getText().toString();
                    String longitude = longitudeEditText.getText().toString();

                    Uri location = Uri.parse("geo:0,0?q=" + latitude + "," + longitude + "(" + vehicleCode + "-" + vehicleNumber + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        startActivity(mapIntent);
                    }
                }
        );

        Button updateLocation = findViewById(R.id.editVehicle_updateLocation);
        updateLocation.setOnClickListener(v -> {
            if (checkPermissions()) {
                checkLocation();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_menu, menu);

        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.editMenu_updateButton) {

            String certificationDate = certificationDateTextView.getText().toString();
            String inspectionExpiryDate = inspectionExpiryDateTextView.getText().toString();
            String registrationExpiryDate = registrationExpiryDateTextView.getText().toString();
            String insuranceExpiryDate = insuranceExpiryDateTextView.getText().toString();
            String odometer = odometerEditText.getText().toString();
            String beltRemarks = beltRemarksEditText.getText().toString();
            String hoseRemarks = hoseRemarksEditText.getText().toString();
            String oilLeakRemarks = oilLeakRemarksEditText.getText().toString();
            String airconRemarks = airconRemarksEditText.getText().toString();
            String wiperRemarks = wiperRemarksEditText.getText().toString();
            String headHiRemarks = headHiRemarksEditText.getText().toString();
            String headLoRemarks = headLoRemarksEditText.getText().toString();
            String drivingLightsRemarks = drivingLightsRemarksEditText.getText().toString();
            String turnSignalRemarks = turnSignalRemarksEditText.getText().toString();
            String brakeLightsRemarks = brakeLightsRemarksEditText.getText().toString();
            String hazardLightsRemarks = hazardLightsRemarksEditText.getText().toString();
            String doorLocksRemarks = doorLocksRemarksEditText.getText().toString();
            String windshieldRemarks = windshieldRemarksEditText.getText().toString();
            String windowsRemarks = windowsRemarksEditText.getText().toString();
            String radioRemarks = radioRemarksEditText.getText().toString();
            String hornRemarks = hornRemarksEditText.getText().toString();
            String fireExtinguisherRemarks = fireExtinguisherRemarksEditText.getText().toString();
            String firstAidRemarks = firstAidRemarksEditText.getText().toString();
            String coolantRemarks = coolantRemarksEditText.getText().toString();
            String engineOilRemarks = engineOilRemarksEditText.getText().toString();
            String transmissionOilRemarks = transmissionOilRemarksEditText.getText().toString();
            String powerSteeringRemarks = powerSteeringRemarksEditText.getText().toString();
            String brakeFluidRemarks = brakeFluidRemarksEditText.getText().toString();
            String washerFluidRemarks = washerFluidRemarksEditText.getText().toString();

            String beltString;
            String hoseString;
            String oilLeakString;
            String airconString;
            String wiperString;
            String headHiString;
            String headLoString;
            String drivingLightsString;
            String turnSignalString;
            String brakeLightsString;
            String hazardLightsString;
            String doorLocksString;
            String windshieldString;
            String windowsString;
            String radioString;
            String hornString;
            String fireExtinguisherString;
            String firstAidString;
            String coolantString;
            String engineOilString;
            String transmissionOilString;
            String powerSteeringString;
            String brakeFluidString;
            String washerFluidString;

            String latitude = latitudeEditText.getText().toString();
            String longitude = longitudeEditText.getText().toString();
            if (beltSwitch.isChecked()) {
                beltString = "OK";

            } else {
                beltString = "Not OK";
            }

            if (hoseSwitch.isChecked()) {
                hoseString = "OK";
            } else {
                hoseString = "Not OK";
            }

            if (oilLeakSwitch.isChecked()) {
                oilLeakString = "OK";
            } else {
                oilLeakString = "Not OK";
            }

            if (airconSwitch.isChecked()) {
                airconString = "OK";
            } else {
                airconString = "Not OK";
            }

            if (wiperSwitch.isChecked()) {
                wiperString = "OK";
            } else {
                wiperString = "Not OK";
            }

            if (headHiSwitch.isChecked()) {
                headHiString = "OK";
            } else {
                headHiString = "Not OK";
            }

            if (headLoSwitch.isChecked()) {
                headLoString = "OK";
            } else {
                headLoString = "Not OK";
            }

            if (drivingLightsSwitch.isChecked()) {
                drivingLightsString = "OK";
            } else {
                drivingLightsString = "Not OK";
            }

            if (turnSignalSwitch.isChecked()) {
                turnSignalString = "OK";
            } else {
                turnSignalString = "Not OK";
            }

            if (brakeLightsSwitch.isChecked()) {
                brakeLightsString = "OK";
            } else {
                brakeLightsString = "Not OK";
            }

            if (hazardLightsSwitch.isChecked()) {
                hazardLightsString = "OK";
            } else {
                hazardLightsString = "Not OK";
            }

            if (doorLocksSwitch.isChecked()) {
                doorLocksString = "OK";
            } else {
                doorLocksString = "Not OK";
            }

            if (windshieldSwitch.isChecked()) {
                windshieldString = "OK";
            } else {
                windshieldString = "Not OK";
            }

            if (windowsSwitch.isChecked()) {
                windowsString = "OK";
            } else {
                windowsString = "Not OK";
            }

            if (radioSwitch.isChecked()) {
                radioString = "OK";
            } else {
                radioString = "Not OK";
            }

            if (hornSwitch.isChecked()) {
                hornString = "OK";
            } else {
                hornString = "Not OK";
            }

            if (fireExtinguisherSwitch.isChecked()) {
                fireExtinguisherString = "OK";
            } else {
                fireExtinguisherString = "Not OK";
            }

            if (firstAidSwitch.isChecked()) {
                firstAidString = "OK";
            } else {
                firstAidString = "Not OK";
            }

            if (coolantSwitch.isChecked()) {
                coolantString = "OK";
            } else {
                coolantString = "Not OK";
            }

            if (engineOilSwitch.isChecked()) {
                engineOilString = "OK";
            } else {
                engineOilString = "Not OK";
            }

            if (transmissionOilSwitch.isChecked()) {
                transmissionOilString = "OK";
            } else {
                transmissionOilString = "Not OK";
            }

            if (powerSteeringSwitch.isChecked()) {
                powerSteeringString = "OK";
            } else {
                powerSteeringString = "Not OK";
            }

            if (brakeFluidSwitch.isChecked()) {
                brakeFluidString = "OK";
            } else {
                brakeFluidString = "Not OK";
            }

            if (washerFluidSwitch.isChecked()) {
                washerFluidString = "OK";
            } else {
                washerFluidString = "Not OK";
            }

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://www.lawtechthai.com/Assets/updateVehicleCertification.php";
            JSONObject parameter = new JSONObject();

            try {
                parameter.put("assetCode", vehicleCode);
                parameter.put("serialNumber", vehicleNumber);
                parameter.put("certificationDate", certificationDate);
                parameter.put("inspectionExpiryDate", inspectionExpiryDate);
                parameter.put("registrationExpiryDate", registrationExpiryDate);
                parameter.put("insuranceExpiryDate", insuranceExpiryDate);
                parameter.put("odometer", odometer);

                parameter.put("belt", beltString);
                parameter.put("hose", hoseString);
                parameter.put("oilLeak", oilLeakString);
                parameter.put("aircon", airconString);
                parameter.put("wiper", wiperString);
                parameter.put("headHi", headHiString);
                parameter.put("headLo", headLoString);
                parameter.put("drivingLights", drivingLightsString);
                parameter.put("turnSignal", turnSignalString);
                parameter.put("brakeLights", brakeLightsString);
                parameter.put("hazardLights", hazardLightsString);
                parameter.put("doorLocks", doorLocksString);
                parameter.put("windshield", windshieldString);
                parameter.put("windows", windowsString);
                parameter.put("radio", radioString);
                parameter.put("horn", hornString);
                parameter.put("fireExtinguisher", fireExtinguisherString);
                parameter.put("firstAid", firstAidString);
                parameter.put("coolant", coolantString);
                parameter.put("engineOil", engineOilString);
                parameter.put("transmissionOil", transmissionOilString);
                parameter.put("powerSteering", powerSteeringString);
                parameter.put("brakeFluid", brakeFluidString);
                parameter.put("washerFluid", washerFluidString);

                parameter.put("beltRemarks", beltRemarks);
                parameter.put("hoseRemarks", hoseRemarks);
                parameter.put("oilLeakRemarks", oilLeakRemarks);
                parameter.put("airconRemarks", airconRemarks);
                parameter.put("wiperRemarks", wiperRemarks);
                parameter.put("headHiRemarks", headHiRemarks);
                parameter.put("headLoRemarks", headLoRemarks);
                parameter.put("drivingLightsRemarks", drivingLightsRemarks);
                parameter.put("turnSignalRemarks", turnSignalRemarks);
                parameter.put("brakeLightsRemarks", brakeLightsRemarks);
                parameter.put("hazardLightsRemarks", hazardLightsRemarks);
                parameter.put("doorLocksRemarks", doorLocksRemarks);
                parameter.put("windshieldRemarks", windshieldRemarks);
                parameter.put("windowsRemarks", windowsRemarks);
                parameter.put("radioRemarks", radioRemarks);
                parameter.put("hornRemarks", hornRemarks);
                parameter.put("fireExtinguisherRemarks", fireExtinguisherRemarks);
                parameter.put("firstAidRemarks", firstAidRemarks);
                parameter.put("coolantRemarks", coolantRemarks);
                parameter.put("engineOilRemarks", engineOilRemarks);
                parameter.put("transmissionOilRemarks", transmissionOilRemarks);
                parameter.put("powerSteeringRemarks", powerSteeringRemarks);
                parameter.put("brakeFluidRemarks", brakeFluidRemarks);
                parameter.put("washerFluidRemarks", washerFluidRemarks);

                parameter.put("latitude", latitude);
                parameter.put("longitude", longitude);
                parameter.put("inspectorName", inspectorName);
                parameter.put("jobTitle", jobTitle);
                parameter.put("certifyingCompany", certifyingCompany);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ProgressBar circle = findViewById(R.id.editVehicle_progress);
            circle.setVisibility(View.VISIBLE);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    response -> {
                        try {
                            boolean insertSuccess = response.getBoolean("success");
                            if (insertSuccess) {
                                Toast toast = Toast.makeText(this, "Re Certification Success", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10);
                                toast.show();

                            } else {
                                Toast.makeText(this, "Could Not Update Asset", Toast.LENGTH_SHORT).show();
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

        } else if (item.getItemId() == R.id.editMenu_cancelButton) {
            Intent i = new Intent(this, MainActivity.class);//fix later
            startActivity(i);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

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

            String latitude = latitudeEditText.getText().toString();
            String longitude = longitudeEditText.getText().toString();

            double assetLat = Double.parseDouble(latitude);
            double assetLon = Double.parseDouble(longitude);

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
                            latitudeEditText.setText(newLat);
                            longitudeEditText.setText(newLon);
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

    private void search(String pVehicleCode, String pVehicleNumber) {
        RequestQueue mQueue = Volley.newRequestQueue(EditVehicleActivity.this);
        String url = "https://www.lawtechthai.com/Assets/showVehicle.php";
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("assetCode", pVehicleCode);
            parameter.put("serialNumber", pVehicleNumber);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressBar circle = findViewById(R.id.editVehicle_progress);
        circle.setVisibility(View.VISIBLE);
        JsonObjectRequest request;
        request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {
                    circle.setVisibility(View.GONE);
                    try {

                        boolean searchSuccess = response.getBoolean("success");
                        String certificationDateResult = response.getString("certificationDate");
                        String inspectionExpiryDateResult = response.getString("inspectionExpiryDate");
                        String registrationExpiryDateResult = response.getString("registrationExpiryDate");
                        String insuranceExpiryDateResult = response.getString("insuranceExpiryDate");
                        String ownerResult = response.getString("owner");
                        String makerResult = response.getString("maker");
                        String odometerResult = response.getString("odometer");
                        String vinResult = response.getString("vin");
                        String engineNumberResult = response.getString("engineNumber");
                        String plateNumberResult = response.getString("plateNumber");
                        String beltResult = response.getString("belt");
                        String hoseResult = response.getString("hose");
                        String oilLeakResult = response.getString("oilLeak");
                        String airconResult = response.getString("aircon");
                        String wiperResult = response.getString("wiper");
                        String headHiResult = response.getString("headHi");
                        String headLoResult = response.getString("headLo");
                        String drivingLightsResult = response.getString("drivingLights");
                        String turnSignalResult = response.getString("turnSignal");
                        String brakeLightsResult = response.getString("brakeLights");
                        String hazardLightsResult = response.getString("hazardLights");
                        String doorLocksResult = response.getString("doorLocks");
                        String windshieldResult = response.getString("windshield");
                        String windowsResult = response.getString("windows");
                        String radioResult = response.getString("radio");
                        String hornResult = response.getString("horn");
                        String fireExtinguisherResult = response.getString("fireExtinguisher");
                        String firstAidResult = response.getString("firstAid");
                        String coolantResult = response.getString("coolant");
                        String engineOilResult = response.getString("engineOil");
                        String transmissionOilResult = response.getString("transmissionOil");
                        String powerSteeringResult = response.getString("powerSteering");
                        String brakeFluidResult = response.getString("brakeFluid");
                        String washerFluidResult = response.getString("washerFluid");
                        String beltRemarksResult = response.getString("beltRemarks");
                        String hoseRemarksResult = response.getString("hoseRemarks");
                        String oilLeakRemarksResult = response.getString("oilLeakRemarks");
                        String airconRemarksResult = response.getString("airconRemarks");
                        String wiperRemarksResult = response.getString("wiperRemarks");
                        String headHiRemarksResult = response.getString("headHiRemarks");
                        String headLoRemarksResult = response.getString("headLoRemarks");
                        String drivingLightsRemarksResult = response.getString("drivingLightsRemarks");
                        String turnSignalRemarksResult = response.getString("turnSignalRemarks");
                        String brakeLightsRemarksResult = response.getString("brakeLightsRemarks");
                        String hazardLightsRemarksResult = response.getString("hazardLightsRemarks");
                        String doorLocksRemarksResult = response.getString("doorLocksRemarks");
                        String windshieldRemarksResult = response.getString("windshieldRemarks");
                        String windowsRemarksResult = response.getString("windowsRemarks");
                        String radioRemarksResult = response.getString("radioRemarks");
                        String hornRemarksResult = response.getString("hornRemarks");
                        String fireExtinguisherRemarksResult = response.getString("fireExtinguisherRemarks");
                        String firstAidRemarksResult = response.getString("firstAidRemarks");
                        String coolantRemarksResult = response.getString("coolantRemarks");
                        String engineOilRemarksResult = response.getString("engineOilRemarks");
                        String transmissionOilRemarksResult = response.getString("transmissionOilRemarks");
                        String powerSteeringRemarksResult = response.getString("powerSteeringRemarks");
                        String brakeFluidRemarksResult = response.getString("brakeFluidRemarks");
                        String washerFluidRemarksResult = response.getString("washerFluidRemarks");
                        latitudeResult = response.getString("latitude");
                        longitudeResult = response.getString("longitude");
                        String imageURLResult = response.getString("imageURL");
                        String lastInspectorNameResult = response.getString("inspectorName");
                        String lastJobTitleResult = response.getString("jobTitle");
                        String lastCertifyingCompanyResult = response.getString("certifyingCompany");

                        TextView vinTextView = findViewById(R.id.editVehicle_vin);
                        TextView engineNumberTextView = findViewById(R.id.editVehicle_engineNumber);
                        TextView plateNumberTextView = findViewById(R.id.editVehicle_plateNumber);
                        TextView ownerTextView = findViewById(R.id.editVehicle_owner);
                        TextView makerTextView = findViewById(R.id.editVehicle_maker);
                        odometerEditText = findViewById(R.id.editVehicle_odometer);

                        beltSwitch = findViewById(R.id.editVehicle_beltSwitch);
                        hoseSwitch = findViewById(R.id.editVehicle_hoseSwitch);
                        oilLeakSwitch = findViewById(R.id.editVehicle_oilLeakSwitch);
                        airconSwitch = findViewById(R.id.editVehicle_airconSwitch);
                        wiperSwitch = findViewById(R.id.editVehicle_wiperSwitch);
                        headHiSwitch = findViewById(R.id.editVehicle_headHiSwitch);
                        headLoSwitch = findViewById(R.id.editVehicle_headLoSwitch);
                        drivingLightsSwitch = findViewById(R.id.editVehicle_drivingLightsSwitch);
                        turnSignalSwitch = findViewById(R.id.editVehicle_turnSignalSwitch);
                        brakeLightsSwitch = findViewById(R.id.editVehicle_brakeLightsSwitch);
                        hazardLightsSwitch = findViewById(R.id.editVehicle_hazardLightsSwitch);
                        doorLocksSwitch = findViewById(R.id.editVehicle_doorLocksSwitch);
                        windshieldSwitch = findViewById(R.id.editVehicle_windshieldSwitch);
                        windowsSwitch = findViewById(R.id.editVehicle_windowsSwitch);
                        radioSwitch = findViewById(R.id.editVehicle_radioSwitch);
                        hornSwitch = findViewById(R.id.editVehicle_hornSwitch);
                        fireExtinguisherSwitch = findViewById(R.id.editVehicle_fireExtinguisherSwitch);
                        firstAidSwitch = findViewById(R.id.editVehicle_firstAidSwitch);
                        coolantSwitch = findViewById(R.id.editVehicle_coolantSwitch);
                        engineOilSwitch = findViewById(R.id.editVehicle_engineOilSwitch);
                        transmissionOilSwitch = findViewById(R.id.editVehicle_transmissionOilSwitch);
                        powerSteeringSwitch = findViewById(R.id.editVehicle_powerSteeringSwitch);
                        brakeFluidSwitch = findViewById(R.id.editVehicle_brakeFluidSwitch);
                        washerFluidSwitch = findViewById(R.id.editVehicle_washerFluidSwitch);
                        beltRemarksEditText = findViewById(R.id.editVehicle_beltRemarks);
                        hoseRemarksEditText = findViewById(R.id.editVehicle_hoseRemarks);
                        oilLeakRemarksEditText = findViewById(R.id.editVehicle_oilLeakRemarks);
                        airconRemarksEditText = findViewById(R.id.editVehicle_airconRemarks);
                        wiperRemarksEditText = findViewById(R.id.editVehicle_wiperRemarks);
                        headHiRemarksEditText = findViewById(R.id.editVehicle_headHiRemarks);
                        headLoRemarksEditText = findViewById(R.id.editVehicle_headLoRemarks);
                        drivingLightsRemarksEditText = findViewById(R.id.editVehicle_drivingLightsRemarks);
                        turnSignalRemarksEditText = findViewById(R.id.editVehicle_turnSignalRemarks);
                        brakeLightsRemarksEditText = findViewById(R.id.editVehicle_brakeLightsRemarks);
                        hazardLightsRemarksEditText = findViewById(R.id.editVehicle_hazardLightsRemarks);
                        doorLocksRemarksEditText = findViewById(R.id.editVehicle_doorLocksRemarks);
                        windshieldRemarksEditText = findViewById(R.id.editVehicle_windshieldRemarks);
                        windowsRemarksEditText = findViewById(R.id.editVehicle_windowsRemarks);
                        radioRemarksEditText = findViewById(R.id.editVehicle_radioRemarks);
                        hornRemarksEditText = findViewById(R.id.editVehicle_hornRemarks);
                        fireExtinguisherRemarksEditText = findViewById(R.id.editVehicle_fireExtinguisherRemarks);
                        firstAidRemarksEditText = findViewById(R.id.editVehicle_firstAidRemarks);
                        coolantRemarksEditText = findViewById(R.id.editVehicle_coolantRemarks);
                        engineOilRemarksEditText = findViewById(R.id.editVehicle_engineOilRemarks);
                        transmissionOilRemarksEditText = findViewById(R.id.editVehicle_transmissionOilRemarks);
                        powerSteeringRemarksEditText = findViewById(R.id.editVehicle_powerSteeringRemarks);
                        brakeFluidRemarksEditText = findViewById(R.id.editVehicle_brakeFluidRemarks);
                        washerFluidRemarksEditText = findViewById(R.id.editVehicle_washerFluidRemarks);
                        latitudeEditText = findViewById(R.id.editVehicle_latitude);
                        longitudeEditText = findViewById(R.id.editVehicle_longitude);
                        TextView imageURLTextView = findViewById(R.id.editVehicle_imageURL);
                        TextView lastInspectorNameTextView = findViewById(R.id.editVehicle_lastInspectorName);
                        TextView lastJobTitleTextView = findViewById(R.id.editVehicle_lastJobTitle);
                        ImageView lastCertifyingCompanyLogo = findViewById(R.id.editVehicle_lastCertifyingCompanyLogo);

                        if (searchSuccess) {

                            certificationDateTextView.setText(certificationDateResult);
                            inspectionExpiryDateTextView.setText(inspectionExpiryDateResult);
                            registrationExpiryDateTextView.setText(registrationExpiryDateResult);
                            insuranceExpiryDateTextView.setText(insuranceExpiryDateResult);
                            vinTextView.setText(vinResult);
                            engineNumberTextView.setText(engineNumberResult);
                            plateNumberTextView.setText(plateNumberResult);
                            odometerEditText.setText(odometerResult);
                            ownerTextView.setText(ownerResult);
                            makerTextView.setText(makerResult);

                            beltRemarksEditText.setText(beltRemarksResult);
                            hoseRemarksEditText.setText(hoseRemarksResult);
                            oilLeakRemarksEditText.setText(oilLeakRemarksResult);
                            airconRemarksEditText.setText(airconRemarksResult);
                            wiperRemarksEditText.setText(wiperRemarksResult);
                            headHiRemarksEditText.setText(headHiRemarksResult);
                            headLoRemarksEditText.setText(headLoRemarksResult);
                            drivingLightsRemarksEditText.setText(drivingLightsRemarksResult);
                            turnSignalRemarksEditText.setText(turnSignalRemarksResult);
                            brakeLightsRemarksEditText.setText(brakeLightsRemarksResult);
                            hazardLightsRemarksEditText.setText(hazardLightsRemarksResult);
                            doorLocksRemarksEditText.setText(doorLocksRemarksResult);
                            windshieldRemarksEditText.setText(windshieldRemarksResult);
                            windowsRemarksEditText.setText(windowsRemarksResult);
                            radioRemarksEditText.setText(radioRemarksResult);
                            hornRemarksEditText.setText(hornRemarksResult);
                            fireExtinguisherRemarksEditText.setText(fireExtinguisherRemarksResult);
                            firstAidRemarksEditText.setText(firstAidRemarksResult);
                            coolantRemarksEditText.setText(coolantRemarksResult);
                            engineOilRemarksEditText.setText(engineOilRemarksResult);
                            transmissionOilRemarksEditText.setText(transmissionOilRemarksResult);
                            powerSteeringRemarksEditText.setText(powerSteeringRemarksResult);
                            brakeFluidRemarksEditText.setText(brakeFluidRemarksResult);
                            washerFluidRemarksEditText.setText(washerFluidRemarksResult);

                            if (beltResult.equals("OK")) {
                                beltSwitch.setChecked(true);
                            } else {
                                beltSwitch.setChecked(false);
                            }

                            if (hoseResult.equals("OK")) {
                                hoseSwitch.setChecked(true);
                            } else {
                                hoseSwitch.setChecked(false);
                            }

                            if (oilLeakResult.equals("OK")) {
                                oilLeakSwitch.setChecked(true);
                            } else {
                                oilLeakSwitch.setChecked(false);
                            }

                            if (airconResult.equals("OK")) {
                                airconSwitch.setChecked(true);
                            } else {
                                airconSwitch.setChecked(false);
                            }

                            if (wiperResult.equals("OK")) {
                                wiperSwitch.setChecked(true);
                            } else {
                                wiperSwitch.setChecked(false);
                            }

                            if (headHiResult.equals("OK")) {
                                headHiSwitch.setChecked(true);
                            } else {
                                headHiSwitch.setChecked(false);
                            }

                            if (headLoResult.equals("OK")) {
                                headLoSwitch.setChecked(true);
                            } else {
                                headLoSwitch.setChecked(false);
                            }

                            if (drivingLightsResult.equals("OK")) {
                                drivingLightsSwitch.setChecked(true);
                            } else {
                                drivingLightsSwitch.setChecked(false);
                            }

                            if (turnSignalResult.equals("OK")) {
                                turnSignalSwitch.setChecked(true);
                            } else {
                                turnSignalSwitch.setChecked(false);
                            }

                            if (brakeLightsResult.equals("OK")) {
                                brakeLightsSwitch.setChecked(true);
                            } else {
                                brakeLightsSwitch.setChecked(false);
                            }

                            if (hazardLightsResult.equals("OK")) {
                                hazardLightsSwitch.setChecked(true);
                            } else {
                                hazardLightsSwitch.setChecked(false);
                            }

                            if (doorLocksResult.equals("OK")) {
                                doorLocksSwitch.setChecked(true);
                            } else {
                                doorLocksSwitch.setChecked(false);
                            }

                            if (windshieldResult.equals("OK")) {
                                windshieldSwitch.setChecked(true);
                            } else {
                                windshieldSwitch.setChecked(false);
                            }

                            if (windowsResult.equals("OK")) {
                                windowsSwitch.setChecked(true);
                            } else {
                                windowsSwitch.setChecked(false);
                            }

                            if (radioResult.equals("OK")) {
                                radioSwitch.setChecked(true);
                            } else {
                                radioSwitch.setChecked(false);
                            }

                            if (hornResult.equals("OK")) {
                                hornSwitch.setChecked(true);
                            } else {
                                hornSwitch.setChecked(false);
                            }

                            if (fireExtinguisherResult.equals("OK")) {
                                fireExtinguisherSwitch.setChecked(true);
                            } else {
                                fireExtinguisherSwitch.setChecked(false);
                            }

                            if (firstAidResult.equals("OK")) {
                                firstAidSwitch.setChecked(true);
                            } else {
                                firstAidSwitch.setChecked(false);
                            }

                            if (coolantResult.equals("OK")) {
                                coolantSwitch.setChecked(true);
                            } else {
                                coolantSwitch.setChecked(false);
                            }

                            if (engineOilResult.equals("OK")) {
                                engineOilSwitch.setChecked(true);
                            } else {
                                engineOilSwitch.setChecked(false);
                            }

                            if (transmissionOilResult.equals("OK")) {
                                transmissionOilSwitch.setChecked(true);
                            } else {
                                transmissionOilSwitch.setChecked(false);
                            }

                            if (powerSteeringResult.equals("OK")) {
                                powerSteeringSwitch.setChecked(true);
                            } else {
                                powerSteeringSwitch.setChecked(false);
                            }

                            if (brakeFluidResult.equals("OK")) {
                                brakeFluidSwitch.setChecked(true);
                            } else {
                                brakeFluidSwitch.setChecked(false);
                            }

                            if (washerFluidResult.equals("OK")) {
                                washerFluidSwitch.setChecked(true);
                            } else {
                                washerFluidSwitch.setChecked(false);
                            }

                            latitudeEditText.setText(latitudeResult);
                            longitudeEditText.setText(longitudeResult);
                            imageURLTextView.setText(imageURLResult);
                            lastInspectorNameTextView.setText(lastInspectorNameResult);
                            lastJobTitleTextView.setText(lastJobTitleResult);

                            if (checkPermissions()) {
                                checkLocation();
                            }

                            switch (lastCertifyingCompanyResult) {

                                case "ACT":
                                    lastCertifyingCompanyLogo.setBackgroundResource(R.drawable.act_logo);
                                    break;
                                case "B Quick":
                                    lastCertifyingCompanyLogo.setBackgroundResource(R.drawable.bquick_logo);
                                    break;
                                case "Cockpit":
                                    lastCertifyingCompanyLogo.setBackgroundResource(R.drawable.cockpit_logo);
                                    break;

                            }

                        } else {
                            Toast.makeText(this, "Could Not Find Asset", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            circle.setVisibility(View.GONE);
            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
        });
        mQueue.add(request);

    }

    protected void scanCode() {
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage("com.google.zxing.client.android");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, SCAN_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SCAN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                String contents = intent.getStringExtra("SCAN_RESULT");

                JSONObject json;
                try {
                    json = new JSONObject(contents);
                    String vehicleCode = json.getString("assetCode");
                    String vehicleNumber = json.getString("serialNumber");
                    vehicleCodeEditText.setText(vehicleCode);
                    vehicleNumberEditText.setText(vehicleNumber);
                    search(vehicleCode,vehicleNumber);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Unrecognized QR code", Toast.LENGTH_SHORT).show();
                    finish();
                }

            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }
}