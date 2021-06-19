package lawtechthai.com.ayunyanRun;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ShowVehicleActivity extends AppCompatActivity {

    private String vehicleAssetCodeResult;
    private String vehicleSerialNumberResult;
    private String vehicleEquipmentID;
    private String latitudeResult;
    private String longitudeResult;
    private boolean inspectionStatus = false;
    private boolean registrationStatus = false;
    private boolean insuranceStatus = false;
    String tireFrontLeftResult;
    String tireFrontRightResult;
    String tireRearLeftResult;
    String tireRearRightResult;
    String tireSpareResult;
    private boolean tireInspectionStatus = false;
    private boolean tireDateOfManufactureStatus = false;
    private boolean tireStatus = false;
    private static final int REQUEST_FINE_LOCATION = 22;
    private static final int REQUEST_CHECK_SETTINGS = 15;
    private AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView certificationStatusImageView = findViewById(R.id.showVehicle_certificationStatus);
        ImageView tireFrontLeftImageView = findViewById(R.id.showVehicle_tireFrontLeft);
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            vehicleEquipmentID = extras.getString("equipmentID");
        }

        RequestQueue mQueue = Volley.newRequestQueue(ShowVehicleActivity.this);
        String url = "https://www.lawtechthai.com/Assets/showVehicle.php";
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("equipmentID", vehicleEquipmentID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressBar circle = findViewById(R.id.showVehicle_progress);
        circle.setVisibility(View.VISIBLE);
        JsonObjectRequest request;
        request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {
                    circle.setVisibility(View.GONE);
                    try {
                        boolean searchSuccess = response.getBoolean("success");
                        vehicleAssetCodeResult = response.getString("assetCode");
                        vehicleSerialNumberResult = response.getString("serialNumber");
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
                        String powerSteeringFluidResult = response.getString("powerSteeringFluid");
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
                        String coolantSpecsResult = response.getString("coolantSpecs");
                        String coolantVolumeResult = response.getString("coolantVolume");
                        String coolantRemarksResult = response.getString("coolantRemarks");
                        String engineOilSpecsResult = response.getString("engineOilSpecs");
                        String engineOilVolumeResult = response.getString("engineOilVolume");
                        String engineOilRemarksResult = response.getString("engineOilRemarks");
                        String transmissionOilSpecsResult = response.getString("transmissionOilSpecs");
                        String transmissionOilVolumeResult = response.getString("transmissionOilVolume");
                        String transmissionOilRemarksResult = response.getString("transmissionOilRemarks");
                        String powerSteeringFluidSpecsResult = response.getString("powerSteeringFluidSpecs");
                        String powerSteeringFluidVolumeResult = response.getString("powerSteeringFluidVolume");
                        String powerSteeringFluidRemarksResult = response.getString("powerSteeringFluidRemarks");
                        String brakeFluidSpecsResult = response.getString("brakeFluidSpecs");
                        String brakeFluidVolumeResult = response.getString("brakeFluidVolume");
                        String brakeFluidRemarksResult = response.getString("brakeFluidRemarks");
                        String washerFluidRemarksResult = response.getString("washerFluidRemarks");
                        tireFrontLeftResult = response.getString("tireFrontLeft");
                        tireFrontRightResult = response.getString("tireFrontRight");
                        tireRearLeftResult = response.getString("tireRearLeft");
                        tireRearRightResult = response.getString("tireRearRight");
                        tireSpareResult = response.getString("tireSpare");
                        latitudeResult = response.getString("latitude");
                        longitudeResult = response.getString("longitude");
                        String imageURLResult = response.getString("imageURL");
                        String inspectorNameResult = response.getString("inspectorName");
                        String jobTitleResult = response.getString("jobTitle");
                        String certifyingCompanyResult = response.getString("certifyingCompany");

                        TextView assetCodeTextView = findViewById(R.id.showVehicle_assetCode);
                        TextView serialNumberTextView = findViewById(R.id.showVehicle_serialNumber);
                        TextView certificationDateTextView = findViewById(R.id.showVehicle_certificationDate);
                        TextView vinTextView = findViewById(R.id.showVehicle_vin);
                        TextView engineNumberTextView = findViewById(R.id.showVehicle_engineNumber);
                        TextView plateNumberTextView = findViewById(R.id.showVehicle_plateNumber);
                        TextView ownerTextView = findViewById(R.id.showVehicle_owner);
                        TextView makerTextView = findViewById(R.id.showVehicle_maker);
                        TextView odometerTextView = findViewById(R.id.showVehicle_odometer);
                        ImageView beltImageView = findViewById(R.id.showVehicle_belt);
                        ImageView hoseImageView = findViewById(R.id.showVehicle_hose);
                        ImageView oilLeakImageView = findViewById(R.id.showVehicle_oilLeak);
                        ImageView airconImageView = findViewById(R.id.showVehicle_aircon);
                        ImageView wiperImageView = findViewById(R.id.showVehicle_wiper);
                        ImageView headHiImageView = findViewById(R.id.showVehicle_headHi);
                        ImageView headLoImageView = findViewById(R.id.showVehicle_headLo);
                        ImageView drivingLightsImageView = findViewById(R.id.showVehicle_drivingLights);
                        ImageView turnSignalImageView = findViewById(R.id.showVehicle_turnSignal);
                        ImageView brakeLightsImageView = findViewById(R.id.showVehicle_brakeLights);
                        ImageView hazardLightsImageView = findViewById(R.id.showVehicle_hazardLights);
                        ImageView doorLocksImageView = findViewById(R.id.showVehicle_doorLocks);
                        ImageView windshieldImageView = findViewById(R.id.showVehicle_windshield);
                        ImageView windowsImageView = findViewById(R.id.showVehicle_windows);
                        ImageView radioImageView = findViewById(R.id.showVehicle_radio);
                        ImageView hornImageView = findViewById(R.id.showVehicle_horn);
                        ImageView fireExtinguisherImageView = findViewById(R.id.showVehicle_fireExtinguisher);
                        ImageView firstAidImageView = findViewById(R.id.showVehicle_firstAid);
                        ImageView coolantImageView = findViewById(R.id.showVehicle_coolant);
                        ImageView engineOilImageView = findViewById(R.id.showVehicle_engineOil);
                        ImageView transmissionOilImageView = findViewById(R.id.showVehicle_transmissionOil);
                        ImageView powerSteeringFluidImageView = findViewById(R.id.showVehicle_powerSteeringFluid);
                        ImageView brakeFluidImageView = findViewById(R.id.showVehicle_brakeFluid);
                        ImageView washerFluidImageView = findViewById(R.id.showVehicle_washerFluid);
                        TextView beltRemarksTextView = findViewById(R.id.showVehicle_beltRemarks);
                        TextView hoseRemarksTextView = findViewById(R.id.showVehicle_hoseRemarks);
                        TextView oilLeakRemarksTextView = findViewById(R.id.showVehicle_oilLeakRemarks);
                        TextView airconRemarksTextView = findViewById(R.id.showVehicle_airconRemarks);
                        TextView wiperRemarksTextView = findViewById(R.id.showVehicle_wiperRemarks);
                        TextView headHiRemarksTextView = findViewById(R.id.showVehicle_headHiRemarks);
                        TextView headLoRemarksTextView = findViewById(R.id.showVehicle_headLoRemarks);
                        TextView drivingLightsRemarksTextView = findViewById(R.id.showVehicle_drivingLightsRemarks);
                        TextView turnSignalRemarksTextView = findViewById(R.id.showVehicle_turnSignalRemarks);
                        TextView brakeLightsRemarksTextView = findViewById(R.id.showVehicle_brakeLightsRemarks);
                        TextView hazardLightsRemarksTextView = findViewById(R.id.showVehicle_hazardLightsRemarks);
                        TextView doorLocksRemarksTextView = findViewById(R.id.showVehicle_doorLocksRemarks);
                        TextView windshieldRemarksTextView = findViewById(R.id.showVehicle_windshieldRemarks);
                        TextView windowsRemarksTextView = findViewById(R.id.showVehicle_windowsRemarks);
                        TextView radioRemarksTextView = findViewById(R.id.showVehicle_radioRemarks);
                        TextView hornRemarksTextView = findViewById(R.id.showVehicle_hornRemarks);
                        TextView fireExtinguisherRemarksTextView = findViewById(R.id.showVehicle_fireExtinguisherRemarks);
                        TextView firstAidRemarksTextView = findViewById(R.id.showVehicle_firstAidRemarks);
                        TextView coolantSpecsTextView = findViewById(R.id.showVehicle_coolantSpecs);
                        TextView coolantVolumeTextView = findViewById(R.id.showVehicle_coolantVolume);
                        TextView coolantRemarksTextView = findViewById(R.id.showVehicle_coolantRemarks);
                        TextView engineOilSpecsTextView = findViewById(R.id.showVehicle_engineOilSpecs);
                        TextView engineOilVolumeTextView = findViewById(R.id.showVehicle_engineOilVolume);
                        TextView engineOilRemarksTextView = findViewById(R.id.showVehicle_engineOilRemarks);
                        TextView transmissionOilSpecsTextView = findViewById(R.id.showVehicle_transmissionOilSpecs);
                        TextView transmissionOilVolumeTextView = findViewById(R.id.showVehicle_transmissionOilVolume);
                        TextView transmissionOilRemarksTextView = findViewById(R.id.showVehicle_transmissionOilRemarks);
                        TextView powerSteeringFluidSpecsTextView = findViewById(R.id.showVehicle_powerSteeringFluidSpecs);
                        TextView powerSteeringFluidVolumeTextView = findViewById(R.id.showVehicle_powerSteeringFluidVolume);
                        TextView powerSteeringFluidRemarksTextView = findViewById(R.id.showVehicle_powerSteeringFluidRemarks);
                        TextView brakeFluidSpecsTextView = findViewById(R.id.showVehicle_brakeFluidSpecs);
                        TextView brakeFluidVolumeTextView = findViewById(R.id.showVehicle_brakeFluidVolume);
                        TextView brakeFluidRemarksTextView = findViewById(R.id.showVehicle_brakeFluidRemarks);
                        TextView washerFluidRemarksTextView = findViewById(R.id.showVehicle_washerFluidRemarks);
                        TextView tireFrontLeftTextView = findViewById(R.id.showVehicle_tireFrontLeftEquipmentID);
                        TextView tireFrontRightTextView = findViewById(R.id.showVehicle_tireFrontRightEquipmentID);
                        TextView tireRearLeftTextView = findViewById(R.id.showVehicle_tireRearLeftEquipmentID);
                        TextView tireRearRightTextView = findViewById(R.id.showVehicle_tireRearRightEquipmentID);
                        TextView tireSpareTextView = findViewById(R.id.showVehicle_tireSpareEquipmentID);
                        TextView latitudeTextView = findViewById(R.id.showVehicle_latitude);
                        TextView longitudeTextView = findViewById(R.id.showVehicle_longitude);
                        TextView imageURLTextView = findViewById(R.id.showVehicle_imageURL);
                        TextView inspectorNameTextView = findViewById(R.id.showVehicle_inspectorName);
                        TextView jobTitleTextView = findViewById(R.id.showVehicle_jobTitle);
                        ImageView certifyingCompanyLogoImageView = findViewById(R.id.showVehicle_certifyingCompanyLogo);

                        if (searchSuccess) {

                            assetCodeTextView.setText(vehicleAssetCodeResult);
                            serialNumberTextView.setText(vehicleSerialNumberResult);
                            certificationDateTextView.setText(certificationDateResult);
                            vinTextView.setText(vinResult);
                            engineNumberTextView.setText(engineNumberResult);
                            plateNumberTextView.setText(plateNumberResult);
                            odometerTextView.setText(odometerResult);
                            ownerTextView.setText(ownerResult);
                            makerTextView.setText(makerResult);
                            beltRemarksTextView.setText(beltRemarksResult);
                            hoseRemarksTextView.setText(hoseRemarksResult);
                            oilLeakRemarksTextView.setText(oilLeakRemarksResult);
                            airconRemarksTextView.setText(airconRemarksResult);
                            wiperRemarksTextView.setText(wiperRemarksResult);
                            headHiRemarksTextView.setText(headHiRemarksResult);
                            headLoRemarksTextView.setText(headLoRemarksResult);
                            drivingLightsRemarksTextView.setText(drivingLightsRemarksResult);
                            turnSignalRemarksTextView.setText(turnSignalRemarksResult);
                            brakeLightsRemarksTextView.setText(brakeLightsRemarksResult);
                            hazardLightsRemarksTextView.setText(hazardLightsRemarksResult);
                            doorLocksRemarksTextView.setText(doorLocksRemarksResult);
                            windshieldRemarksTextView.setText(windshieldRemarksResult);
                            windowsRemarksTextView.setText(windowsRemarksResult);
                            radioRemarksTextView.setText(radioRemarksResult);
                            hornRemarksTextView.setText(hornRemarksResult);
                            fireExtinguisherRemarksTextView.setText(fireExtinguisherRemarksResult);
                            firstAidRemarksTextView.setText(firstAidRemarksResult);
                            coolantSpecsTextView.setText(coolantSpecsResult);
                            coolantVolumeTextView.setText(coolantVolumeResult);
                            coolantRemarksTextView.setText(coolantRemarksResult);
                            engineOilSpecsTextView.setText(engineOilSpecsResult);
                            engineOilVolumeTextView.setText(engineOilVolumeResult);
                            engineOilRemarksTextView.setText(engineOilRemarksResult);
                            transmissionOilSpecsTextView.setText(transmissionOilSpecsResult);
                            transmissionOilVolumeTextView.setText(transmissionOilVolumeResult);
                            transmissionOilRemarksTextView.setText(transmissionOilRemarksResult);
                            powerSteeringFluidSpecsTextView.setText(powerSteeringFluidSpecsResult);
                            powerSteeringFluidVolumeTextView.setText(powerSteeringFluidVolumeResult);
                            powerSteeringFluidRemarksTextView.setText(powerSteeringFluidRemarksResult);
                            brakeFluidSpecsTextView.setText(brakeFluidSpecsResult);
                            brakeFluidVolumeTextView.setText(brakeFluidVolumeResult);
                            brakeFluidRemarksTextView.setText(brakeFluidRemarksResult);
                            washerFluidRemarksTextView.setText(washerFluidRemarksResult);
                            tireFrontLeftTextView.setText(tireFrontLeftResult);
                            tireFrontRightTextView.setText(tireFrontRightResult);
                            tireRearLeftTextView.setText(tireRearLeftResult);
                            tireRearRightTextView.setText(tireRearRightResult);
                            tireSpareTextView.setText(tireSpareResult);

                            if (beltResult.equals("OK")) {
                                beltImageView.setBackgroundResource(R.drawable.ok);

                            } else {
                                beltImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (hoseResult.equals("OK")) {
                                hoseImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                hoseImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (oilLeakResult.equals("OK")) {
                                oilLeakImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                oilLeakImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (airconResult.equals("OK")) {
                                airconImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                airconImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (wiperResult.equals("OK")) {
                                wiperImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                wiperImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (headHiResult.equals("OK")) {
                                headHiImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                headHiImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (headLoResult.equals("OK")) {
                                headLoImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                headLoImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (drivingLightsResult.equals("OK")) {
                                drivingLightsImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                drivingLightsImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (turnSignalResult.equals("OK")) {
                                turnSignalImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                turnSignalImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (brakeLightsResult.equals("OK")) {
                                brakeLightsImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                brakeLightsImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (hazardLightsResult.equals("OK")) {
                                hazardLightsImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                hazardLightsImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (doorLocksResult.equals("OK")) {
                                doorLocksImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                doorLocksImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (windshieldResult.equals("OK")) {
                                windshieldImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                windshieldImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (windowsResult.equals("OK")) {
                                windowsImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                windowsImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (radioResult.equals("OK")) {
                                radioImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                radioImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (hornResult.equals("OK")) {
                                hornImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                hornImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (fireExtinguisherResult.equals("OK")) {
                                fireExtinguisherImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                fireExtinguisherImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (firstAidResult.equals("OK")) {
                                firstAidImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                firstAidImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (coolantResult.equals("OK")) {
                                coolantImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                coolantImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (engineOilResult.equals("OK")) {
                                engineOilImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                engineOilImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (transmissionOilResult.equals("OK")) {
                                transmissionOilImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                transmissionOilImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (powerSteeringFluidResult.equals("OK")) {
                                powerSteeringFluidImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                powerSteeringFluidImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (brakeFluidResult.equals("OK")) {
                                brakeFluidImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                brakeFluidImageView.setBackgroundResource(R.drawable.notok);
                            }

                            if (washerFluidResult.equals("OK")) {
                                washerFluidImageView.setBackgroundResource(R.drawable.ok);
                            } else {
                                washerFluidImageView.setBackgroundResource(R.drawable.notok);
                            }

                            latitudeTextView.setText(latitudeResult);
                            longitudeTextView.setText(longitudeResult);
                            imageURLTextView.setText(imageURLResult);
                            inspectorNameTextView.setText(inspectorNameResult);
                            jobTitleTextView.setText(jobTitleResult);
                            checkCertificationValidity(inspectionExpiryDateResult, registrationExpiryDateResult, insuranceExpiryDateResult);
                            checkTire(tireFrontLeftResult, "Front Left");
                            checkTire(tireFrontRightResult, "Front Right");
                            checkTire(tireRearLeftResult, "Rear Left");
                            checkTire(tireRearRightResult, "Rear Right");
                            checkTire(tireSpareResult, "Spare");


                            if (checkPermissions()) {
                                checkLocation();
                            }

                            switch (certifyingCompanyResult) {

                                case "ACT":
                                    certifyingCompanyLogoImageView.setBackgroundResource(R.drawable.act_logo);
                                    break;
                                case "B Quick":
                                    certifyingCompanyLogoImageView.setBackgroundResource(R.drawable.bquick_logo);
                                    break;
                                case "Cockpit":
                                    certifyingCompanyLogoImageView.setBackgroundResource(R.drawable.cockpit_logo);
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

        Button mapViewButton = findViewById(R.id.showVehicle_map_search);
        mapViewButton.setOnClickListener(v -> {

                    Uri location = Uri.parse("geo:0,0?q=" + latitudeResult + "," + longitudeResult + "(" + vehicleAssetCodeResult + "-" + vehicleSerialNumberResult + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);

                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        startActivity(mapIntent);
                    }
                }
        );

        Button updateLocation = findViewById(R.id.showVehicle_updateLocation);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.showMenu_addButton) {

            RequestQueue queue = Volley.newRequestQueue(this);
            String url = "https://www.lawtechthai.com/Assets/assetListAdd.php";
            JSONObject parameter = new JSONObject();

            try {
                parameter.put("equipmentID", vehicleEquipmentID);
                if (inspectionStatus) {
                    parameter.put("certification", "VALID");
                } else {
                    parameter.put("certification", "xxxxx EXPIRED xxxxx");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ProgressBar circle = findViewById(R.id.showVehicle_progress);
            circle.setVisibility(View.VISIBLE);
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    response -> {
                        circle.setVisibility(View.GONE);
                        try {
                            boolean insertSuccess = response.getBoolean("success");
                            if (insertSuccess) {
                                Toast.makeText(this, "Data Inserted", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(this, "Asset Already On The List", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_SHORT).show();
                        }

                    }, error -> {
                circle.setVisibility(View.GONE);
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            });

            queue.getCache().clear();
            queue.add(request);

        } else if (item.getItemId() == R.id.showMenu_listButton) {
            Intent i = new Intent(this, AssetListActivity.class);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.showMenu_searchButton) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void checkCertificationValidity(String pInspectionExpiryDate, String pRegistrationExpiryDate, String pInsuranceExpiryDate) {

        String pattern = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar todayCalendar = Calendar.getInstance();
        String pToday = sdf.format(todayCalendar.getTime());
        Calendar oneWeekAheadCalendar = Calendar.getInstance();
        oneWeekAheadCalendar.add(Calendar.DAY_OF_YEAR, 7);
        String pOneWeekAhead = sdf.format(oneWeekAheadCalendar.getTime());
        TextView inspectionExpiryDateTextView = findViewById(R.id.showVehicle_inspectionExpiryDate);
        TextView registrationExpiryDateTextView = findViewById(R.id.showVehicle_registrationExpiryDate);
        TextView insuranceExpiryDateTextView = findViewById(R.id.showVehicle_insuranceExpiryDate);
        ImageView inspectionExpiryDateImageView = findViewById(R.id.showVehicle_inspectionExpiryDateStatus);
        ImageView registrationExpiryDateImageView = findViewById(R.id.showVehicle_registrationExpiryDateStatus);
        ImageView insuranceExpiryDateImageView = findViewById(R.id.showVehicle_insuranceExpiryDateStatus);
        ImageView certificationStatusImageView = findViewById(R.id.showVehicle_certificationStatus);
        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        Date inspectionExpiryDate = null;
        Date registrationExpiryDate = null;
        Date insuranceExpiryDate = null;
        Date today = null;
        Date oneWeekAhead = null;
        try {
            inspectionExpiryDate = sdf.parse(pInspectionExpiryDate);
            registrationExpiryDate = sdf.parse(pRegistrationExpiryDate);
            insuranceExpiryDate = sdf.parse(pInsuranceExpiryDate);
            today = sdf.parse(pToday);
            oneWeekAhead = sdf.parse(pOneWeekAhead);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        assert inspectionExpiryDate != null;
        assert registrationExpiryDate != null;
        assert insuranceExpiryDate != null;
        assert today != null;
        assert oneWeekAhead != null;

        if (inspectionExpiryDate.after(oneWeekAhead)) {
            inspectionExpiryDateTextView.setTextColor(getColor(R.color.Green));
            inspectionExpiryDateImageView.setBackgroundResource(R.drawable.ok);
            inspectionStatus = true;
        } else if (inspectionExpiryDate.before(oneWeekAhead) && inspectionExpiryDate.after(today)) {
            inspectionExpiryDateTextView.setTextColor(getColor(R.color.Orange));
            inspectionExpiryDateImageView.setBackgroundResource(R.drawable.warning_icon);
            inspectionStatus = true;
        } else {
            inspectionExpiryDateTextView.setTextColor(getColor(R.color.Red));
            inspectionExpiryDateImageView.setBackgroundResource(R.drawable.notok);
            inspectionStatus = false;
        }
        inspectionExpiryDateTextView.setText(pInspectionExpiryDate);


        if (registrationExpiryDate.after(oneWeekAhead)) {
            registrationExpiryDateTextView.setTextColor(getColor(R.color.Green));
            registrationExpiryDateImageView.setBackgroundResource(R.drawable.ok);
            registrationStatus = true;
        } else if (registrationExpiryDate.before(oneWeekAhead) && registrationExpiryDate.after(today)) {
            registrationExpiryDateTextView.setTextColor(getColor(R.color.Orange));
            registrationExpiryDateImageView.setBackgroundResource(R.drawable.warning_icon);
            registrationStatus = true;
        } else {
            registrationExpiryDateTextView.setTextColor(getColor(R.color.Red));
            registrationExpiryDateImageView.setBackgroundResource(R.drawable.notok);
            registrationStatus = false;
        }
        registrationExpiryDateTextView.setText(pRegistrationExpiryDate);

        if (insuranceExpiryDate.after(oneWeekAhead)) {
            insuranceExpiryDateTextView.setTextColor(getColor(R.color.Green));
            insuranceExpiryDateImageView.setBackgroundResource(R.drawable.ok);
            insuranceStatus = true;
        } else if (insuranceExpiryDate.before(oneWeekAhead) && insuranceExpiryDate.after(today)) {
            insuranceExpiryDateTextView.setTextColor(getColor(R.color.Orange));
            insuranceExpiryDateImageView.setBackgroundResource(R.drawable.warning_icon);
            insuranceStatus = true;
        } else {
            insuranceExpiryDateTextView.setTextColor(getColor(R.color.Red));
            insuranceExpiryDateImageView.setBackgroundResource(R.drawable.notok);
            insuranceStatus = false;
        }
        insuranceExpiryDateTextView.setText(pInsuranceExpiryDate);

        if (inspectionStatus && registrationStatus && insuranceStatus) {
            certificationStatusImageView.setBackgroundResource(R.drawable.valid);
        } else
            certificationStatusImageView.setBackgroundResource(R.drawable.expired);

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(200);
        }
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
                            TextView latitudeTextView = findViewById(R.id.showVehicle_latitude);
                            TextView longitudeTextView = findViewById(R.id.showVehicle_longitude);
                            latitudeResult = newLat;
                            longitudeResult = newLon;

                            RequestQueue queue = Volley.newRequestQueue(this);
                            String url = "https://www.lawtechthai.com/Assets/updateAssetLocation.php";
                            JSONObject parameter = new JSONObject();

                            try {
                                parameter.put("assetCode", vehicleAssetCodeResult);
                                parameter.put("serialNumber", vehicleSerialNumberResult);
                                parameter.put("latitude", newLat);
                                parameter.put("longitude", newLon);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ProgressBar circle = findViewById(R.id.showVehicle_progress);
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

    public void checkTire(String pTireEquipmentID, String pWheelWell) {

        ImageView tireFrontLeftImageView = findViewById(R.id.showVehicle_tireFrontLeft);
        ImageView tireFrontRightImageView = findViewById(R.id.showVehicle_tireFrontRight);
        ImageView tireRearLeftImageView = findViewById(R.id.showVehicle_tireRearLeft);
        ImageView tireRearRightImageView = findViewById(R.id.showVehicle_tireRearRight);
        ImageView tireSpareImageView = findViewById(R.id.showVehicle_tireSpare);

        tireFrontLeftImageView.setBackgroundResource(R.drawable.notok);
        tireFrontRightImageView.setBackgroundResource(R.drawable.notok);
        tireRearLeftImageView.setBackgroundResource(R.drawable.notok);
        tireRearRightImageView.setBackgroundResource(R.drawable.notok);
        tireSpareImageView.setBackgroundResource(R.drawable.notok);


        RequestQueue nQueue = Volley.newRequestQueue(ShowVehicleActivity.this);
        String url = "https://www.lawtechthai.com/Assets/showTiresInstalled.php";
        JSONObject parameter = new JSONObject();
        try {

            parameter.put("vehicleEquipmentID", vehicleEquipmentID);
            parameter.put("tireEquipmentID", pTireEquipmentID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressBar circle = findViewById(R.id.showVehicle_progress);
        circle.setVisibility(View.VISIBLE);
        JsonObjectRequest request1;
        request1 = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {
                    circle.setVisibility(View.GONE);
                    try {

                        boolean searchSuccess = response.getBoolean("success");
                        String inspectionExpiryDateResult = response.getString("inspectionExpiryDate");
                        String dateOfManufactureResult = response.getString("dateOfManufacture");
                        String tireStatusResult = response.getString("tireStatus");

                        if (searchSuccess) {

                            String pattern = "dd/MM/yyyy";
                            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                            Calendar todayCalendar = Calendar.getInstance();
                            String pToday = sdf.format(todayCalendar.getTime());
                            Calendar oneWeekAheadCalendar = Calendar.getInstance();
                            Calendar tenYearsAgoCalendar = Calendar.getInstance();
                            oneWeekAheadCalendar.add(Calendar.DAY_OF_YEAR, 7);
                            tenYearsAgoCalendar.add(Calendar.DAY_OF_YEAR, -3650);
                            String pOneWeekAhead = sdf.format(oneWeekAheadCalendar.getTime());
                            String pTenYearsAgo = sdf.format(tenYearsAgoCalendar.getTime());

                            Date tireNextInspectionDate = null;
                            Date tireDateOfManufacture = null;
                            Date today = null;
                            Date oneWeekAhead = null;
                            Date tenYearsAgo = null;

                            try {
                                tireNextInspectionDate = sdf.parse(inspectionExpiryDateResult);
                                tireDateOfManufacture = sdf.parse(dateOfManufactureResult);
                                today = sdf.parse(pToday);
                                oneWeekAhead = sdf.parse(pOneWeekAhead);
                                tenYearsAgo = sdf.parse(pTenYearsAgo);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            assert tireNextInspectionDate != null;
                            assert tireDateOfManufacture != null;
                            assert today != null;
                            assert oneWeekAhead != null;

                            //check if inspection date is still valid(still have more than 1 week), warning(less than 1 week) or past inspection date
                            if (tireNextInspectionDate.after(oneWeekAhead)) {
                                tireInspectionStatus = true;
                            } else {
                                tireInspectionStatus = false;
                            }

                            if (tireDateOfManufacture.after(tenYearsAgo)) {
                                tireDateOfManufactureStatus = true;
                            } else {
                                tireDateOfManufactureStatus = false;
                            }

                            if (tireInspectionStatus && tireDateOfManufactureStatus) {

                                switch (pWheelWell) {

                                    case "Front Left":
                                        tireFrontLeftImageView.setBackgroundResource(R.drawable.ok);
                                        break;

                                    case "Front Right":
                                        tireFrontRightImageView.setBackgroundResource(R.drawable.ok);
                                        break;

                                    case "Rear Left":
                                        tireRearLeftImageView.setBackgroundResource(R.drawable.ok);
                                        break;

                                    case "Rear Right":
                                        tireRearRightImageView.setBackgroundResource(R.drawable.ok);
                                        break;

                                    case "Spare":
                                        tireSpareImageView.setBackgroundResource(R.drawable.ok);
                                        break;

                                }

                            } else {
                                tireStatus = false;
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

        nQueue.getCache().clear();
        nQueue.add(request1);

    }


    //create vehicle object
//    vehicle.setAssetType(response.getString("assetType"));
//                                    vehicle.setAssetCode(response.getString("assetCode"));
//                                    vehicle.setSerialNumber(response.getString("serialNumber"));
//                                    vehicle.setDriver(response.getString("driver"));
//                                    vehicle.setDriverTrainingExpiryDate(response.getString("driverTrainingExpiryDate"));
//                                    vehicle.setPhone(response.getString("phone"));
//                                    vehicle.setCertificationDate(response.getString("certificationDate"));
//                                    vehicle.setInspectionExpiryDate(response.getString("inspectionExpiryDate"));
//                                    vehicle.setRegistrationExpiryDate(response.getString("registrationExpiryDate"));
//                                    vehicle.setInsuranceExpiryDate(response.getString("insuranceExpiryDate"));
//                                    vehicle.setOwner(response.getString("owner"));
//                                    vehicle.setMaker(response.getString("maker"));
//                                    vehicle.setOdometer(Integer.parseInt(response.getString("odometer")));
//                                    vehicle.setVin(response.getString("vin"));
//                                    vehicle.setEngineNumber(response.getString("engineNumber"));
//                                    vehicle.setPlateNumber(response.getString("plateNumber"));
//                                    vehicle.setBelt(response.getString("belt"));
//                                    vehicle.setHose(response.getString("hose"));
//                                    vehicle.setOilLeak(response.getString("oilLeak"));
//                                    vehicle.setAircon(response.getString("aircon"));
//                                    vehicle.setWiper(response.getString("wiper"));
//                                    vehicle.setHeadHi(response.getString("headHi"));
//                                    vehicle.setHeadLo(response.getString("headLo"));
//                                    vehicle.setDrivingLights(response.getString("drivingLights"));
//                                    vehicle.setTurnSignal(response.getString("turnSignal"));
//                                    vehicle.setBrakeLights(response.getString("brakeLights"));
//                                    vehicle.setHazardLights(response.getString("hazardLights"));
//                                    vehicle.setDoorLocks(response.getString("doorLocks"));
//                                    vehicle.setWindshield(response.getString("windshield"));
//                                    vehicle.setWindows(response.getString("windows"));
//                                    vehicle.setRadio(response.getString("radio"));
//                                    vehicle.setHorn(response.getString("horn"));
//                                    vehicle.setFireExtinguisher(response.getString("fireExtinguisher"));
//                                    vehicle.setFirstAid(response.getString("firstAid"));
//                                    vehicle.setCoolant(response.getString("coolant"));
//                                    vehicle.setEngineOil(response.getString("engineOil"));
//                                    vehicle.setTransmissionOil(response.getString("transmissionOil"));
//                                    vehicle.setPowerSteeringFluid(response.getString("powerSteeringFluid"));
//                                    vehicle.setBrakeFluid(response.getString("brakeFluid"));
//                                    vehicle.setWasherFluid(response.getString("washerFluid"));
//                                    vehicle.setBeltRemarks(response.getString("beltRemarks"));
//                                    vehicle.setHoseRemarks(response.getString("hoseRemarks"));
//                                    vehicle.setOilLeakRemarks(response.getString("oilLeakRemarks"));
//                                    vehicle.setAirconRemarks(response.getString("airconRemarks"));
//                                    vehicle.setWiperRemarks(response.getString("wiperRemarks"));
//                                    vehicle.setHeadHiRemarks(response.getString("headHiRemarks"));
//                                    vehicle.setHeadLoRemarks(response.getString("headLoRemarks"));
//                                    vehicle.setDrivingLightsRemarks(response.getString("drivingLightsRemarks"));
//                                    vehicle.setTurnSignalRemarks(response.getString("turnSignalRemarks"));
//                                    vehicle.setBrakeLightsRemarks(response.getString("brakeLightsRemarks"));
//                                    vehicle.setHazardLightsRemarks(response.getString("hazardLightsRemarks"));
//                                    vehicle.setDoorLocksRemarks(response.getString("doorLocksRemarks"));
//                                    vehicle.setWindshieldRemarks(response.getString("windshieldRemarks"));
//                                    vehicle.setWindowsRemarks(response.getString("windowsRemarks"));
//                                    vehicle.setRadioRemarks(response.getString("radioRemarks"));
//                                    vehicle.setHornRemarks(response.getString("hornRemarks"));
//                                    vehicle.setFireExtinguisherRemarks(response.getString("fireExtinguisherRemarks"));
//                                    vehicle.setFirstAidRemarks(response.getString("firstAidRemarks"));
//                                    vehicle.setCoolantSpecs(response.getString("coolantSpecs"));
//                                    vehicle.setCoolantVolume(response.getString("coolantVolume"));
//                                    vehicle.setCoolantRemarks(response.getString("coolantRemarks"));
//                                    vehicle.setEngineOilSpecs(response.getString("engineOilSpecs"));
//                                    vehicle.setEngineOilVolume(response.getString("engineOilVolume"));
//                                    vehicle.setEngineOilRemarks(response.getString("engineOilRemarks"));
//                                    vehicle.setTransmissionOilSpecs(response.getString("transmissionOilSpecs"));
//                                    vehicle.setTransmissionOilVolume(response.getString("transmissionOilVolume"));
//                                    vehicle.setTransmissionOilRemarks(response.getString("transmissionOilRemarks"));
//                                    vehicle.setPowerSteeringFluidSpecs(response.getString("powerSteeringFluidSpecs"));
//                                    vehicle.setPowerSteeringFluidVolume(response.getString("powerSteeringFluidVolume"));
//                                    vehicle.setPowerSteeringFluidRemarks(response.getString("powerSteeringFluidRemarks"));
//                                    vehicle.setBrakeFluidSpecs(response.getString("brakeFluidSpecs"));
//                                    vehicle.setBrakeFluidVolume(response.getString("brakeFluidVolume"));
//                                    vehicle.setBrakeFluidRemarks(response.getString("brakeFluidRemarks"));
//                                    vehicle.setWasherFluidRemarks(response.getString("washerFluidRemarks"));
//                                    vehicle.setLatitude(Double.parseDouble(response.getString("latitude")));
//                                    vehicle.setLongitude(Double.parseDouble(response.getString("longitude")));
//                                    vehicle.setImageURL(response.getString("imageURL"));
//                                    vehicle.setInspectorName(response.getString("inspectorName"));
//                                    vehicle.setJobTitle(response.getString("jobTitle"));
//                                    vehicle.setCertifyingCompany(response.getString("certifyingCompany"));
//                                    vehicle.setTireFrontLeft(response.getString("tireFrontLeft"));
//                                    vehicle.setTireFrontRight(response.getString("tireFrontRight"));
//                                    vehicle.setTireRearLeft(response.getString("tireRearLeft"));
//                                    vehicle.setTireRearRight(response.getString("tireRearRight"));
//                                    vehicle.setTireSpare(response.getString("tireSpare"));

}