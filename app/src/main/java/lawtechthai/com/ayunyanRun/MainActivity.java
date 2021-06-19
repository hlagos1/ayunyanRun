package lawtechthai.com.ayunyanRun;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;


public class MainActivity extends AppCompatActivity implements Serializable {

    private static final int REQUEST_FINE_LOCATION = 22;
    private static final int REQUEST_CHECK_SETTINGS = 15;
    private static int SCAN_REQUEST_CODE = 1;
    TextView statusTextView;
    TextView hintTextView;
    EditText usernameEditText;
    EditText passwordEditText;
    EditText currentOdometerEditText;
    Button leftButton;
    Button rightButton;
    SharedPreferences prefs;
    private String queryEquipmentID = "";
    private String queryAssetType;
    private String queryOperationalStatus;
    private String queryAssetCode;
    private String querySerialNumber;
    private String queryCountryOrInstalledOn;
    private String queryProvinceOfRegistrationOrWheelWell;
    private String queryAssetOwnerName;
    private String queryAssetOwnerPhoneNumber;
    private String queryAssetOwnerEmail;
    private String queryAssetOwnerLineID;
    private String queryLatitude;
    private String queryLongitude;
    private String status;
    private AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        prefs = this.getPreferences(Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        checkPermissions();
        TextView newUser = findViewById(R.id.main_newUser);
        hintTextView = findViewById(R.id.main_searchHint);

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            equipmentID = extras.getString("equipmentID");
//        }

        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
//      finish();// stops app if phone does not have NFC, disable so that non NFC phones can work, also set manifest: android:required="false"
        }
        readFromIntent(getIntent());

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

//      Register New User
        newUser.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, UserRegistrationActivity.class);
            startActivity(i);
        });

//      Search for equipment using Search Box
        EditText equipmentIDSearch = findViewById(R.id.main_equipmentIDSearch);
        ImageView search = findViewById(R.id.main_searchIcon);
        search.setOnClickListener(v -> {

            if (equipmentIDSearch.getText().toString().toUpperCase().isEmpty()) {
                Toast.makeText(this, "Please Enter Serial Number", Toast.LENGTH_SHORT).show();
                return;
            }
            hintTextView.setText(R.string.searching);
            queryEquipmentID = equipmentIDSearch.getText().toString().toUpperCase();
            equipmentIDSearch.getText().clear();
            closeKeyBoard();
            searchDatabase(queryEquipmentID);

        });

//        Search for equipment using QR Code
        ImageView scan = findViewById(R.id.main_qrscanIcon);
        scan.setOnClickListener(v -> {
            scanCode();

        });

        usernameEditText = findViewById(R.id.main_username);
        passwordEditText = findViewById(R.id.main_password);
        currentOdometerEditText = findViewById(R.id.main_currentOdometer);

        String email = prefs.getString("username", "");
        String password = prefs.getString("password", "");

        usernameEditText.setText(email);
        passwordEditText.setText(password);

    }

    public void searchDatabase(String pEquipmentID) {

        TextView assetCodeTextView = findViewById(R.id.main_assetCode);
        TextView serialNumberTextView = findViewById(R.id.main_serialNumber);
        TextView countryOrInstalledOnTextView = findViewById(R.id.main_countryOrInstalledOn);
        TextView provincialCodeTextView = findViewById(R.id.main_provincialCode);
        TextView provinceOfRegistrationOrWheelWell = findViewById(R.id.main_provinceOfRegistrationOrWheelWell);
        findViewById(R.id.main_actionButtons).setVisibility(View.VISIBLE);
        findViewById(R.id.main_login).setVisibility(View.VISIBLE);
        findViewById(R.id.main_instructions).setVisibility(View.GONE);
        LinearLayout showVehicleLinearLayout = findViewById(R.id.main_showVehicle);
        LinearLayout showTireLinearLayout = findViewById(R.id.main_showTire);
        RelativeLayout locationRelativeLayout = findViewById(R.id.main_location);
        TextView disclaimer = findViewById(R.id.main_disclaimer);
        leftButton = findViewById(R.id.main_leftButton);
        rightButton = findViewById(R.id.main_rightButton);
        statusTextView = findViewById(R.id.main_statusText);
        assetCodeTextView.setText("");
        serialNumberTextView.setText("");
        provinceOfRegistrationOrWheelWell.setText("");
        statusTextView.setText("");

        RequestQueue mQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://www.lawtechthai.com/Assets/searchDatabase.php";
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("equipmentID", pEquipmentID);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressBar circle = findViewById(R.id.main_progress);
        circle.setVisibility(View.VISIBLE);
        JsonObjectRequest request;
        request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {
                    circle.setVisibility(View.GONE);
                    try {

                        boolean searchSuccess = response.getBoolean("success");
                        queryAssetType = response.getString("assetType");
                        queryOperationalStatus = response.getString("operationalStatus");
                        queryAssetCode = response.getString("assetCode");
                        querySerialNumber = response.getString("serialNumber");
                        queryAssetOwnerName = response.getString("assetOwnerName");
                        queryAssetOwnerPhoneNumber = response.getString("assetOwnerPhoneNumber");
                        queryAssetOwnerEmail = response.getString("assetOwnerEmail");
                        queryAssetOwnerLineID = response.getString("assetOwnerLineID");
                        queryLatitude = response.getString("latitude");
                        queryLongitude = response.getString("longitude");

                        if (searchSuccess) {
                            hintTextView.setVisibility(View.GONE);
                            assetCodeTextView.setText(queryAssetCode);
                            serialNumberTextView.setText(querySerialNumber);
                            disclaimer.setVisibility(View.GONE);
                            locationRelativeLayout.setVisibility(View.VISIBLE);

                            if (checkPermissions()) {
                                checkLocation(queryLatitude, queryLongitude);
                            }

                            switch (queryAssetType) {

                                case "vehicle":
                                    queryCountryOrInstalledOn = "THAILAND";
                                    queryProvinceOfRegistrationOrWheelWell = response.getString("provinceOfRegistration");
                                    if (response.getString("publicPrivate").equals("public")) {
                                        findViewById(R.id.main_plateNumberBackground).setBackgroundResource(R.drawable.platenumberyellow);
                                    }
                                    provincialCodeTextView.setText(response.getString(response.getString("provincialCode")));
                                    leftButton.setBackgroundResource(R.drawable.drive);
                                    leftButton.setText("DRIVE");
                                    rightButton.setBackgroundResource(R.drawable.maintenance);
                                    rightButton.setText("MAINTENANCE");
                                    showTireLinearLayout.setVisibility(View.GONE);
                                    showVehicleLinearLayout.setVisibility(View.VISIBLE);
                                    showVehicle(response);
                                    break;

                                case "tire":
                                    queryCountryOrInstalledOn = response.getString("installedOn");
                                    queryProvinceOfRegistrationOrWheelWell = response.getString("wheelWell");
                                    leftButton.setBackgroundResource(R.drawable.tire_rotation);
                                    leftButton.setText("CHANGE");
                                    rightButton.setBackgroundResource(R.drawable.tire_inspection);
                                    rightButton.setText("INSPECT");
                                    showVehicleLinearLayout.setVisibility(View.GONE);
                                    showTireLinearLayout.setVisibility(View.VISIBLE);
                                    currentOdometerEditText.setVisibility(View.GONE);
                                    showTire(response);
                                    break;

                                default:
                                    status = "UNKNOWN";
                                    break;
                            }
                            countryOrInstalledOnTextView.setText(queryCountryOrInstalledOn);
                            provinceOfRegistrationOrWheelWell.setText(queryProvinceOfRegistrationOrWheelWell);
                            statusTextView.setText(status);

                            switch (status) {

                                case "READY":
                                    statusTextView.setTextColor(Color.WHITE);
                                    statusTextView.setBackgroundResource(R.color.Green);
                                    break;
                                case "WARNING":
                                    statusTextView.setTextColor(Color.WHITE);
                                    statusTextView.setBackgroundResource(R.color.Orange);
//                                    vibrate(200, 3);
                                    break;
                                case "NOT READY":
                                    statusTextView.setTextColor(Color.YELLOW);
                                    statusTextView.setBackgroundResource(R.color.Red);
//                                    vibrate(500, 4);
                                    break;
                                case "UNDER REPAIR":
                                    statusTextView.setTextColor(Color.YELLOW);
                                    statusTextView.setBackgroundResource(R.color.Blue);
//                                    vibrate(200, 3);
                                    break;
                                case "JUNK":
                                    statusTextView.setTextColor(Color.WHITE);
                                    statusTextView.setBackgroundResource(R.color.Black);
//                                    vibrate(500, 4);
                                    break;
                                default:
                                    statusTextView.setTextColor(Color.WHITE);
                                    statusTextView.setBackgroundResource(R.color.Red);
                                    break;
                            }

                        } else {
                            Toast.makeText(this, "Could Not Find Asset", Toast.LENGTH_LONG).show();
                            statusTextView.setText("UNKNOWN");
                            statusTextView.setTextColor(Color.parseColor("White"));
                            statusTextView.setBackgroundColor(Color.parseColor("Red"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        statusTextView.setText("ERROR");
                        statusTextView.setTextColor(Color.parseColor("White"));
                        statusTextView.setBackgroundColor(Color.parseColor("Red"));
//                        vibrate(500, 3);
                    }
                }, error ->

        {
            circle.setVisibility(View.GONE);
            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
//            vibrate(500, 3);

        });
        mQueue.getCache().clear();
        mQueue.add(request);

        Button mapViewButton = findViewById(R.id.main_mapSearch);
        mapViewButton.setOnClickListener(v -> {

                    Uri assetLocation = Uri.parse("geo:0,0?q=" + queryLatitude + "," + queryLongitude + "(" + queryAssetCode + "-" + querySerialNumber + ")");
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, assetLocation);

                    PackageManager packageManager = getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(mapIntent, 0);
                    boolean isIntentSafe = activities.size() > 0;

                    if (isIntentSafe) {
                        startActivity(mapIntent);
                    }
                }
        );

        Button updateLocation = findViewById(R.id.main_updateLocation);
        updateLocation.setOnClickListener(v -> {
            if (checkPermissions()) {
                checkLocation(queryLatitude, queryLongitude);
            }
        });

        findViewById(R.id.main_assetOwnerEmailButton).setOnClickListener(v -> sendEmail(queryAssetOwnerEmail, queryAssetOwnerName, queryEquipmentID));
        findViewById(R.id.main_assetOwnerPhoneNumberButton).setOnClickListener(v -> dialContactPhone(queryAssetOwnerPhoneNumber));
        findViewById(R.id.main_assetOwnerLineIDButton).setOnClickListener(v -> sendLine(queryAssetOwnerLineID));

    }

    private void showVehicle(JSONObject response) throws JSONException {

        Vehicle vehicle = new Vehicle(response);
        vehicle.checkStatus();

        switch (queryOperationalStatus) {
            case "ACTIVE":
                status = vehicle.getStatus();
                break;

            case "UNDER REPAIR":
                status = "UNDER REPAIR";
                break;
            case "JUNK":
                status = "JUNK";
                break;
            default:
                status = "UNKNOWN";
                break;
        }

        Button certificationStatusIcon = findViewById(R.id.main_showVehicle_certificationStatusIcon);
        TextView certificationExpiryDateTextView = findViewById(R.id.main_showVehicle_certificationExpiryDate);
        certificationExpiryDateTextView.setText(vehicle.getCertificationExpiryDate());
        TextView certificationPerformedByTextView = findViewById(R.id.main_showVehicle_certificationPerformedBy);
        certificationPerformedByTextView.setText(vehicle.getCertificationPerformedBy());

        if (vehicle.getCertificationStatus().equals("READY")) {
            certificationExpiryDateTextView.setTextColor(Color.parseColor("#008000"));
            certificationStatusIcon.setBackgroundResource(R.drawable.certificationicongreen);
        } else if (vehicle.getCertificationStatus().equals("WARNING")) {
            certificationExpiryDateTextView.setTextColor(Color.parseColor("#ff6700"));
            certificationStatusIcon.setBackgroundResource(R.drawable.certificationiconorange);
        } else if (vehicle.getCertificationStatus().equals("NOT READY")) {
            certificationExpiryDateTextView.setTextColor(Color.parseColor("#a01d22"));
            certificationStatusIcon.setBackgroundResource(R.drawable.certificationiconred);
        } else certificationStatusIcon.setBackgroundResource(R.drawable.questionmark);

        Button maintenanceStatusIcon = findViewById(R.id.main_showVehicle_maintenanceStatusIcon);
        TextView maintenanceExpiryDateTextView = findViewById(R.id.main_showVehicle_maintenanceExpiryDate);
        maintenanceExpiryDateTextView.setText(vehicle.getMaintenanceExpiryDate());
        TextView maintenancePerformedByTextView = findViewById(R.id.main_showVehicle_maintenancePerformedBy);
        maintenancePerformedByTextView.setText(vehicle.getMaintenancePerformedBy());

        if (vehicle.getMaintenanceStatus().equals("READY")) {
            maintenanceExpiryDateTextView.setTextColor(Color.parseColor("#008000"));
            maintenanceStatusIcon.setBackgroundResource(R.drawable.maintenanceicongreen);
        } else if (vehicle.getMaintenanceStatus().equals("WARNING")) {
            maintenanceExpiryDateTextView.setTextColor(Color.parseColor("#ff6700"));
            maintenanceStatusIcon.setBackgroundResource(R.drawable.maintenanceiconorange);
        } else if (vehicle.getMaintenanceStatus().equals("NOT READY")) {
            maintenanceExpiryDateTextView.setTextColor(Color.parseColor("#a01d22"));
            maintenanceStatusIcon.setBackgroundResource(R.drawable.maintenanceiconred);
        } else maintenanceStatusIcon.setBackgroundResource(R.drawable.questionmark);

        Button registrationStatusIcon = findViewById(R.id.main_showVehicle_registrationStatusIcon);
        TextView registrationExpiryDateTextView = findViewById(R.id.main_showVehicle_registrationExpiryDate);
        registrationExpiryDateTextView.setText(vehicle.getRegistrationExpiryDate());
        TextView registrationPerformedByTextView = findViewById(R.id.main_showVehicle_registrationPerformedBy);
        registrationPerformedByTextView.setText(vehicle.getRegistrationPerformedBy());

        if (vehicle.getRegistrationStatus().equals("READY")) {
            registrationExpiryDateTextView.setTextColor(Color.parseColor("#008000"));
            registrationStatusIcon.setBackgroundResource(R.drawable.registrationicongreen);
        } else if (vehicle.getRegistrationStatus().equals("WARNING")) {
            registrationExpiryDateTextView.setTextColor(Color.parseColor("#ff6700"));
            registrationStatusIcon.setBackgroundResource(R.drawable.registrationiconorange);
        } else if (vehicle.getRegistrationStatus().equals("NOT READY")) {
            registrationExpiryDateTextView.setTextColor(Color.parseColor("#a01d22"));
            registrationStatusIcon.setBackgroundResource(R.drawable.registrationiconred);
        } else registrationStatusIcon.setBackgroundResource(R.drawable.questionmark);

        Button insuranceStatusIcon = findViewById(R.id.main_showVehicle_insuranceStatusIcon);
        TextView insuranceExpiryDateTextView = findViewById(R.id.main_showVehicle_insuranceExpiryDate);
        insuranceExpiryDateTextView.setText(vehicle.getInsuranceExpiryDate());
        TextView insurancePerformedByTextView = findViewById(R.id.main_showVehicle_insurancePerformedBy);
        insurancePerformedByTextView.setText(vehicle.getInsurancePerformedBy());

        if (vehicle.getInsuranceStatus().equals("READY")) {
            insuranceExpiryDateTextView.setTextColor(Color.parseColor("#008000"));
            insuranceStatusIcon.setBackgroundResource(R.drawable.insuranceicongreen);
        } else if (vehicle.getInsuranceStatus().equals("WARNING")) {
            insuranceExpiryDateTextView.setTextColor(Color.parseColor("#ff6700"));
            insuranceStatusIcon.setBackgroundResource(R.drawable.insuranceiconorange);
        } else if (vehicle.getInsuranceStatus().equals("NOT READY")) {
            insuranceExpiryDateTextView.setTextColor(Color.parseColor("#a01d22"));
            insuranceStatusIcon.setBackgroundResource(R.drawable.insuranceiconred);
        } else insuranceStatusIcon.setBackgroundResource(R.drawable.questionmark);

        TextView vinTextView = findViewById(R.id.main_showVehicle_vin);
        TextView engineNumberTextView = findViewById(R.id.main_showVehicle_engineNumber);
        TextView plateNumberTextView = findViewById(R.id.main_showVehicle_plateNumber);
        TextView assetOwnerNameTextView = findViewById(R.id.main_showVehicle_assetOwnerName);
        TextView assetOwnerPhoneNumberTextView = findViewById(R.id.main_showVehicle_assetOwnerPhoneNumber);
        TextView assetOwnerEmailTextView = findViewById(R.id.main_showVehicle_assetOwnerEmail);
        TextView assetOwnerLineIDTextView = findViewById(R.id.main_showVehicle_assetOwnerLineID);
        TextView makerTextView = findViewById(R.id.main_showVehicle_maker);
        TextView odometerTextView = findViewById(R.id.main_showVehicle_odometer);
        TextView beltRemarksTextView = findViewById(R.id.main_showVehicle_beltRemarks);
        TextView hoseRemarksTextView = findViewById(R.id.main_showVehicle_hoseRemarks);
        TextView oilLeakRemarksTextView = findViewById(R.id.main_showVehicle_oilLeakRemarks);
        TextView airconRemarksTextView = findViewById(R.id.main_showVehicle_airconRemarks);
        TextView wiperRemarksTextView = findViewById(R.id.main_showVehicle_wiperRemarks);
        TextView headHiRemarksTextView = findViewById(R.id.main_showVehicle_headHiRemarks);
        TextView headLoRemarksTextView = findViewById(R.id.main_showVehicle_headLoRemarks);
        TextView drivingLightsRemarksTextView = findViewById(R.id.main_showVehicle_drivingLightsRemarks);
        TextView turnSignalRemarksTextView = findViewById(R.id.main_showVehicle_turnSignalRemarks);
        TextView brakeLightsRemarksTextView = findViewById(R.id.main_showVehicle_brakeLightsRemarks);
        TextView hazardLightsRemarksTextView = findViewById(R.id.main_showVehicle_hazardLightsRemarks);
        TextView doorLocksRemarksTextView = findViewById(R.id.main_showVehicle_doorLocksRemarks);
        TextView windshieldRemarksTextView = findViewById(R.id.main_showVehicle_windshieldRemarks);
        TextView windowsRemarksTextView = findViewById(R.id.main_showVehicle_windowsRemarks);
        TextView radioRemarksTextView = findViewById(R.id.main_showVehicle_radioRemarks);
        TextView hornRemarksTextView = findViewById(R.id.main_showVehicle_hornRemarks);
        TextView fireExtinguisherRemarksTextView = findViewById(R.id.main_showVehicle_fireExtinguisherRemarks);
        TextView firstAidRemarksTextView = findViewById(R.id.main_showVehicle_firstAidRemarks);
        TextView coolantSpecsTextView = findViewById(R.id.main_showVehicle_coolantSpecs);
        TextView coolantVolumeTextView = findViewById(R.id.main_showVehicle_coolantVolume);
        TextView coolantRemarksTextView = findViewById(R.id.main_showVehicle_coolantRemarks);
        TextView engineOilSpecsTextView = findViewById(R.id.main_showVehicle_engineOilSpecs);
        TextView engineOilVolumeTextView = findViewById(R.id.main_showVehicle_engineOilVolume);
        TextView engineOilRemarksTextView = findViewById(R.id.main_showVehicle_engineOilRemarks);
        TextView transmissionOilSpecsTextView = findViewById(R.id.main_showVehicle_transmissionOilSpecs);
        TextView transmissionOilVolumeTextView = findViewById(R.id.main_showVehicle_transmissionOilVolume);
        TextView transmissionOilRemarksTextView = findViewById(R.id.main_showVehicle_transmissionOilRemarks);
        TextView powerSteeringFluidSpecsTextView = findViewById(R.id.main_showVehicle_powerSteeringFluidSpecs);
        TextView powerSteeringFluidVolumeTextView = findViewById(R.id.main_showVehicle_powerSteeringFluidVolume);
        TextView powerSteeringFluidRemarksTextView = findViewById(R.id.main_showVehicle_powerSteeringFluidRemarks);
        TextView brakeFluidSpecsTextView = findViewById(R.id.main_showVehicle_brakeFluidSpecs);
        TextView brakeFluidVolumeTextView = findViewById(R.id.main_showVehicle_brakeFluidVolume);
        TextView brakeFluidRemarksTextView = findViewById(R.id.main_showVehicle_brakeFluidRemarks);
        TextView washerFluidRemarksTextView = findViewById(R.id.main_showVehicle_washerFluidRemarks);
        TextView tireOneTextView = findViewById(R.id.main_showVehicle_tireOneEquipmentID);
        TextView tireTwoTextView = findViewById(R.id.main_showVehicle_tireTwoEquipmentID);
        TextView tireThreeTextView = findViewById(R.id.main_showVehicle_tireThreeEquipmentID);
        TextView tireFourTextView = findViewById(R.id.main_showVehicle_tireFourEquipmentID);
        TextView tireFiveTextView = findViewById(R.id.main_showVehicle_tireFiveEquipmentID);
        TextView tireSixTextView = findViewById(R.id.main_showVehicle_tireSixEquipmentID);
        TextView tireSevenTextView = findViewById(R.id.main_showVehicle_tireSevenEquipmentID);
        TextView tireEightTextView = findViewById(R.id.main_showVehicle_tireEightEquipmentID);
        TextView tireNineTextView = findViewById(R.id.main_showVehicle_tireNineEquipmentID);
        TextView tireTenTextView = findViewById(R.id.main_showVehicle_tireTenEquipmentID);
        TextView tireElevenTextView = findViewById(R.id.main_showVehicle_tireElevenEquipmentID);
        TextView tireTwelveTextView = findViewById(R.id.main_showVehicle_tireTwelveEquipmentID);
        TextView tireThirteenTextView = findViewById(R.id.main_showVehicle_tireThirteenEquipmentID);
        TextView tireFourteenTextView = findViewById(R.id.main_showVehicle_tireFourteenEquipmentID);
        TextView tireFifteenTextView = findViewById(R.id.main_showVehicle_tireFifteenEquipmentID);
        TextView tireSixteenTextView = findViewById(R.id.main_showVehicle_tireSixteenEquipmentID);
        TextView tireSeventeenTextView = findViewById(R.id.main_showVehicle_tireSeventeenEquipmentID);
        TextView tireEighteenTextView = findViewById(R.id.main_showVehicle_tireEighteenEquipmentID);
        TextView tireNineteenTextView = findViewById(R.id.main_showVehicle_tireNineteenEquipmentID);
        TextView tireTwentyTextView = findViewById(R.id.main_showVehicle_tireTwentyEquipmentID);
        TextView tireTwentyOneTextView = findViewById(R.id.main_showVehicle_tireTwentyOneEquipmentID);
        TextView tireTwentyTwoTextView = findViewById(R.id.main_showVehicle_tireTwentyTwoEquipmentID);
        TextView latitudeTextView = findViewById(R.id.main_latitude);
        TextView longitudeTextView = findViewById(R.id.main_longitude);

        vinTextView.setText(vehicle.getVin());
        engineNumberTextView.setText(vehicle.getEngineNumber());
        plateNumberTextView.setText(vehicle.getPlateNumber());
        assetOwnerNameTextView.setText(vehicle.getAssetOwnerName());
        assetOwnerPhoneNumberTextView.setText(vehicle.getAssetOwnerPhoneNumber());
        assetOwnerEmailTextView.setText(vehicle.getAssetOwnerEmail());
        assetOwnerLineIDTextView.setText(vehicle.getAssetOwnerLineID());
        makerTextView.setText(vehicle.getMaker());
        odometerTextView.setText(Integer.toString(vehicle.getOdometer()));
        beltRemarksTextView.setText(vehicle.getBeltRemarks());
        hoseRemarksTextView.setText(vehicle.getHoseRemarks());
        oilLeakRemarksTextView.setText(vehicle.getOilLeakRemarks());
        airconRemarksTextView.setText(vehicle.getAirconRemarks());
        wiperRemarksTextView.setText(vehicle.getWiperRemarks());
        headHiRemarksTextView.setText(vehicle.getHeadHiRemarks());
        headLoRemarksTextView.setText(vehicle.getHeadLoRemarks());
        drivingLightsRemarksTextView.setText(vehicle.getDrivingLightsRemarks());
        turnSignalRemarksTextView.setText(vehicle.getTurnSignalRemarks());
        brakeLightsRemarksTextView.setText(vehicle.getBrakeLightsRemarks());
        hazardLightsRemarksTextView.setText(vehicle.getHazardLightsRemarks());
        doorLocksRemarksTextView.setText(vehicle.getDoorLocksRemarks());
        windshieldRemarksTextView.setText(vehicle.getWindshieldRemarks());
        windowsRemarksTextView.setText(vehicle.getWindowsRemarks());
        radioRemarksTextView.setText(vehicle.getRadioRemarks());
        hornRemarksTextView.setText(vehicle.getHornRemarks());
        fireExtinguisherRemarksTextView.setText(vehicle.getFireExtinguisherRemarks());
        firstAidRemarksTextView.setText(vehicle.getFirstAidRemarks());
        coolantSpecsTextView.setText(vehicle.getCoolantSpecs());
        coolantVolumeTextView.setText(vehicle.getCoolantVolume());
        coolantRemarksTextView.setText(vehicle.getCoolantRemarks());
        engineOilSpecsTextView.setText(vehicle.getEngineOilSpecs());
        engineOilVolumeTextView.setText(vehicle.getEngineOilVolume());
        engineOilRemarksTextView.setText(vehicle.getEngineOilRemarks());
        transmissionOilSpecsTextView.setText(vehicle.getTransmissionOilSpecs());
        transmissionOilVolumeTextView.setText(vehicle.getTransmissionOilVolume());
        transmissionOilRemarksTextView.setText(vehicle.getTransmissionOilRemarks());
        powerSteeringFluidSpecsTextView.setText(vehicle.getPowerSteeringFluidSpecs());
        powerSteeringFluidVolumeTextView.setText(vehicle.getPowerSteeringFluidVolume());
        powerSteeringFluidRemarksTextView.setText(vehicle.getPowerSteeringFluidRemarks());
        brakeFluidSpecsTextView.setText(vehicle.getBrakeFluidSpecs());
        brakeFluidVolumeTextView.setText(vehicle.getBrakeFluidVolume());
        brakeFluidRemarksTextView.setText(vehicle.getBrakeFluidRemarks());
        washerFluidRemarksTextView.setText(vehicle.getWasherFluidRemarks());
        tireOneTextView.setText(vehicle.getTireOne());
        tireTwoTextView.setText(vehicle.getTireTwo());
        tireThreeTextView.setText(vehicle.getTireThree());
        tireFourTextView.setText(vehicle.getTireFour());
        tireFiveTextView.setText(vehicle.getTireFive());
        tireSixTextView.setText(vehicle.getTireSix());
        tireSevenTextView.setText(vehicle.getTireSeven());
        tireEightTextView.setText(vehicle.getTireEight());
        tireNineTextView.setText(vehicle.getTireNine());
        tireTenTextView.setText(vehicle.getTireTen());
        tireElevenTextView.setText(vehicle.getTireEleven());
        tireTwelveTextView.setText(vehicle.getTireTwelve());
        tireThirteenTextView.setText(vehicle.getTireThirteen());
        tireFourteenTextView.setText(vehicle.getTireFourteen());
        tireFifteenTextView.setText(vehicle.getTireFifteen());
        tireSixteenTextView.setText(vehicle.getTireSixteen());
        tireSeventeenTextView.setText(vehicle.getTireSeventeen());
        tireEighteenTextView.setText(vehicle.getTireEighteen());
        tireNineteenTextView.setText(vehicle.getTireNineteen());
        tireTwentyTextView.setText(vehicle.getTireTwenty());
        tireTwentyOneTextView.setText(vehicle.getTireTwentyOne());
        tireTwentyTwoTextView.setText(vehicle.getTireTwentyTwo());
        latitudeTextView.setText(Double.toString(vehicle.getLatitude()));
        longitudeTextView.setText(Double.toString(vehicle.getLongitude()));

        ImageView beltImageView = findViewById(R.id.main_showVehicle_belt);
        ImageView hoseImageView = findViewById(R.id.main_showVehicle_hose);
        ImageView oilLeakImageView = findViewById(R.id.main_showVehicle_oilLeak);
        ImageView airconImageView = findViewById(R.id.main_showVehicle_aircon);
        ImageView wiperImageView = findViewById(R.id.main_showVehicle_wiper);
        ImageView headHiImageView = findViewById(R.id.main_showVehicle_headHi);
        ImageView headLoImageView = findViewById(R.id.main_showVehicle_headLo);
        ImageView drivingLightsImageView = findViewById(R.id.main_showVehicle_drivingLights);
        ImageView turnSignalImageView = findViewById(R.id.main_showVehicle_turnSignal);
        ImageView brakeLightsImageView = findViewById(R.id.main_showVehicle_brakeLights);
        ImageView hazardLightsImageView = findViewById(R.id.main_showVehicle_hazardLights);
        ImageView doorLocksImageView = findViewById(R.id.main_showVehicle_doorLocks);
        ImageView windshieldImageView = findViewById(R.id.main_showVehicle_windshield);
        ImageView windowsImageView = findViewById(R.id.main_showVehicle_windows);
        ImageView radioImageView = findViewById(R.id.main_showVehicle_radio);
        ImageView hornImageView = findViewById(R.id.main_showVehicle_horn);
        ImageView fireExtinguisherImageView = findViewById(R.id.main_showVehicle_fireExtinguisher);
        ImageView firstAidImageView = findViewById(R.id.main_showVehicle_firstAid);
        ImageView coolantImageView = findViewById(R.id.main_showVehicle_coolant);
        ImageView engineOilImageView = findViewById(R.id.main_showVehicle_engineOil);
        ImageView transmissionOilImageView = findViewById(R.id.main_showVehicle_transmissionOil);
        ImageView powerSteeringFluidImageView = findViewById(R.id.main_showVehicle_powerSteeringFluid);
        ImageView brakeFluidImageView = findViewById(R.id.main_showVehicle_brakeFluid);
        ImageView washerFluidImageView = findViewById(R.id.main_showVehicle_washerFluid);

        if (vehicle.getBelt().equals("OK")) {
            beltImageView.setBackgroundResource(R.drawable.ok);

        } else {
            beltImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getHose().equals("OK")) {
            hoseImageView.setBackgroundResource(R.drawable.ok);
        } else {
            hoseImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getOilLeak().equals("OK")) {
            oilLeakImageView.setBackgroundResource(R.drawable.ok);
        } else {
            oilLeakImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getAircon().equals("OK")) {
            airconImageView.setBackgroundResource(R.drawable.ok);
        } else {
            airconImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getWiper().equals("OK")) {
            wiperImageView.setBackgroundResource(R.drawable.ok);
        } else {
            wiperImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getHeadHi().equals("OK")) {
            headHiImageView.setBackgroundResource(R.drawable.ok);
        } else {
            headHiImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getHeadLo().equals("OK")) {
            headLoImageView.setBackgroundResource(R.drawable.ok);
        } else {
            headLoImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getDrivingLights().equals("OK")) {
            drivingLightsImageView.setBackgroundResource(R.drawable.ok);
        } else {
            drivingLightsImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getTurnSignal().equals("OK")) {
            turnSignalImageView.setBackgroundResource(R.drawable.ok);
        } else {
            turnSignalImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getBrakeLights().equals("OK")) {
            brakeLightsImageView.setBackgroundResource(R.drawable.ok);
        } else {
            brakeLightsImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getHazardLights().equals("OK")) {
            hazardLightsImageView.setBackgroundResource(R.drawable.ok);
        } else {
            hazardLightsImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getDoorLocks().equals("OK")) {
            doorLocksImageView.setBackgroundResource(R.drawable.ok);
        } else {
            doorLocksImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getWindshield().equals("OK")) {
            windshieldImageView.setBackgroundResource(R.drawable.ok);
        } else {
            windshieldImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getWindows().equals("OK")) {
            windowsImageView.setBackgroundResource(R.drawable.ok);
        } else {
            windowsImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getRadio().equals("OK")) {
            radioImageView.setBackgroundResource(R.drawable.ok);
        } else {
            radioImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getHorn().equals("OK")) {
            hornImageView.setBackgroundResource(R.drawable.ok);
        } else {
            hornImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getFireExtinguisher().equals("OK")) {
            fireExtinguisherImageView.setBackgroundResource(R.drawable.ok);
        } else {
            fireExtinguisherImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getFirstAid().equals("OK")) {
            firstAidImageView.setBackgroundResource(R.drawable.ok);
        } else {
            firstAidImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getCoolant().equals("OK")) {
            coolantImageView.setBackgroundResource(R.drawable.ok);
        } else {
            coolantImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getEngineOil().equals("OK")) {
            engineOilImageView.setBackgroundResource(R.drawable.ok);
        } else {
            engineOilImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getTransmissionOil().equals("OK")) {
            transmissionOilImageView.setBackgroundResource(R.drawable.ok);
        } else {
            transmissionOilImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getPowerSteeringFluid().equals("OK")) {
            powerSteeringFluidImageView.setBackgroundResource(R.drawable.ok);
        } else {
            powerSteeringFluidImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getBrakeFluid().equals("OK")) {
            brakeFluidImageView.setBackgroundResource(R.drawable.ok);
        } else {
            brakeFluidImageView.setBackgroundResource(R.drawable.notok);
        }

        if (vehicle.getWasherFluid().equals("OK")) {
            washerFluidImageView.setBackgroundResource(R.drawable.ok);
        } else {
            washerFluidImageView.setBackgroundResource(R.drawable.notok);
        }

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String currentOdometer = currentOdometerEditText.getText().toString();

        leftButton.setOnClickListener(v -> {


                    if (queryEquipmentID.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Search Equipment ID", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (username.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Type Email Address", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (password.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (currentOdometer.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Current Odometer", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.apply();//this writes it to the phone, phone remembers data even on App exit//

                    doLogInRequest(username, password, "Drive", currentOdometer);

                }
        );

        rightButton.setOnClickListener(v -> {


                    if (username.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Type Email Address", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (password.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (currentOdometer.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Current Odometer", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.apply();//this writes it to the phone, phone remembers data even on App exit//

                    doLogInRequest(username, password, "Maintenance", currentOdometer);

                }
        );


        //Set Icon visibilities for quickview buttons

        String url = "https://www.lawtechthai.com/Assets/searchTire.php";
        JSONObject parameter = new JSONObject();
        JsonObjectRequest request;

        //Check Tires 1-5 status and set icon color

        //Tire One
        Tire tireOne = new Tire();
        TextView tireOneTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireOneTireStatusIcon);
        tireOneTireStatusIconTextView.setTextColor(Color.WHITE);

        RequestQueue tireOneQueue = Volley.newRequestQueue(MainActivity.this);
        try {
            parameter.put("equipmentID", vehicle.getTireOne());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                responseTireOne -> {
                    try {

                        boolean searchSuccess = responseTireOne.getBoolean("success");
                        if (searchSuccess) {
                            tireOne.buildTire(responseTireOne);
                            tireOne.checkStatus();

                            //Set Certification Status

                            if (tireOne.getCertificationStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireOneCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireOne.getCertificationStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireOneCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireOne.getCertificationStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireOneCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireOneCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set Mileage Status

                            if (tireOne.getMileageStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireOneMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireOne.getMileageStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireOneMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireOne.getMileageStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireOneMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireOneMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set TreadDepth Status

                            if (tireOne.getTreadDepthStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireOneTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireOne.getTreadDepthStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireOneTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireOne.getTreadDepthStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireOneTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireOneTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set TireAge Status

                            if (tireOne.getTireAgeStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireOneTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireOne.getTireAgeStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireOneTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireOne.getTireAgeStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireOneTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireOneTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set Status
                            if (tireOne.getStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tractorTireOneIcon).setBackgroundResource(R.color.Green);
                                findViewById(R.id.main_showVehicle_frontLeftWheelIcon).setBackgroundResource(R.color.Green);
                                tireOneTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                tireOneTireStatusIconTextView.setText("READY");
                            } else if (tireOne.getStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tractorTireOneIcon).setBackgroundResource(R.color.Orange);
                                findViewById(R.id.main_showVehicle_frontLeftWheelIcon).setBackgroundResource(R.color.Orange);
                                tireOneTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                tireOneTireStatusIconTextView.setText("WARNING");
                            } else if (tireOne.getStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tractorTireOneIcon).setBackgroundResource(R.color.Red);
                                findViewById(R.id.main_showVehicle_frontLeftWheelIcon).setBackgroundResource(R.color.Red);
                                tireOneTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                tireOneTireStatusIconTextView.setText("NOT READY");
                            } else {
                                findViewById(R.id.main_showVehicle_tractorTireOneIcon).setBackgroundResource(R.color.Black);
                                findViewById(R.id.main_showVehicle_frontLeftWheelIcon).setBackgroundResource(R.color.Black);
                                tireOneTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                tireOneTireStatusIconTextView.setText("UNKNOWN");
                            }
                            tireOneTireStatusIconTextView.setOnClickListener(v -> {
                                searchDatabase(tireOne.getEquipmentID());
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                    }
                }, error ->

        {
            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

        });
        tireOneQueue.getCache().clear();
        tireOneQueue.add(request);

        //Tire Two
        Tire tireTwo = new Tire();
        TextView tireTwoTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireTwoTireStatusIcon);
        tireTwoTireStatusIconTextView.setTextColor(Color.WHITE);

        RequestQueue tireTwoQueue = Volley.newRequestQueue(MainActivity.this);
        try {
            parameter.put("equipmentID", vehicle.getTireTwo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                responseTireTwo -> {
                    try {

                        boolean searchSuccess = responseTireTwo.getBoolean("success");
                        if (searchSuccess) {
                            tireTwo.buildTire(responseTireTwo);
                            tireTwo.checkStatus();

                            //Set Certification Status

                            if (tireTwo.getCertificationStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireTwoCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireTwo.getCertificationStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireTwoCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireTwo.getCertificationStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireTwoCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireTwoCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set Mileage Status

                            if (tireTwo.getMileageStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireTwoMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireTwo.getMileageStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireTwoMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireTwo.getMileageStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireTwoMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireTwoMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set TreadDepth Status

                            if (tireTwo.getTreadDepthStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireTwoTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireTwo.getTreadDepthStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireTwoTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireTwo.getTreadDepthStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireTwoTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireTwoTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set TireAge Status

                            if (tireTwo.getTireAgeStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireTwoTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireTwo.getTireAgeStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireTwoTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireTwo.getTireAgeStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireTwoTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireTwoTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set Status
                            if (tireTwo.getStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tractorTireTwoIcon).setBackgroundResource(R.color.Green);
                                findViewById(R.id.main_showVehicle_frontRightWheelIcon).setBackgroundResource(R.color.Green);
                                tireTwoTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                tireTwoTireStatusIconTextView.setText("READY");
                            } else if (tireTwo.getStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tractorTireTwoIcon).setBackgroundResource(R.color.Orange);
                                findViewById(R.id.main_showVehicle_frontRightWheelIcon).setBackgroundResource(R.color.Orange);
                                tireTwoTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                tireTwoTireStatusIconTextView.setText("WARNING");
                            } else if (tireTwo.getStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tractorTireTwoIcon).setBackgroundResource(R.color.Red);
                                findViewById(R.id.main_showVehicle_frontRightWheelIcon).setBackgroundResource(R.color.Red);
                                tireTwoTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                tireTwoTireStatusIconTextView.setText("NOT READY");
                            } else {
                                findViewById(R.id.main_showVehicle_tractorTireTwoIcon).setBackgroundResource(R.color.Black);
                                findViewById(R.id.main_showVehicle_frontRightWheelIcon).setBackgroundResource(R.color.Black);
                                tireTwoTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                tireTwoTireStatusIconTextView.setText("UNKNOWN");
                            }
                            tireTwoTireStatusIconTextView.setOnClickListener(v -> {
                                searchDatabase(tireTwo.getEquipmentID());
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                    }
                }, error ->

        {
            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

        });
        tireTwoQueue.getCache().clear();
        tireTwoQueue.add(request);

        //Tire Three
        Tire tireThree = new Tire();
        TextView tireThreeTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireThreeTireStatusIcon);
        tireThreeTireStatusIconTextView.setTextColor(Color.WHITE);

        RequestQueue tireThreeQueue = Volley.newRequestQueue(MainActivity.this);
        try {
            parameter.put("equipmentID", vehicle.getTireThree());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                responseTireThree -> {
                    try {

                        boolean searchSuccess = responseTireThree.getBoolean("success");
                        if (searchSuccess) {
                            tireThree.buildTire(responseTireThree);
                            tireThree.checkStatus();

                            //Set Certification Status

                            if (tireThree.getCertificationStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireThreeCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireThree.getCertificationStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireThreeCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireThree.getCertificationStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireThreeCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireThreeCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set Mileage Status

                            if (tireThree.getMileageStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireThreeMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireThree.getMileageStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireThreeMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireThree.getMileageStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireThreeMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireThreeMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set TreadDepth Status

                            if (tireThree.getTreadDepthStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireThreeTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireThree.getTreadDepthStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireThreeTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireThree.getTreadDepthStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireThreeTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireThreeTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set TireAge Status

                            if (tireThree.getTireAgeStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireThreeTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireThree.getTireAgeStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireThreeTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireThree.getTireAgeStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireThreeTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireThreeTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set Status
                            if (tireThree.getStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tractorTireThreeIcon).setBackgroundResource(R.color.Green);
                                findViewById(R.id.main_showVehicle_rearLeftWheelIcon).setBackgroundResource(R.color.Green);
                                tireThreeTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                tireThreeTireStatusIconTextView.setText("READY");
                            } else if (tireThree.getStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tractorTireThreeIcon).setBackgroundResource(R.color.Orange);
                                findViewById(R.id.main_showVehicle_rearLeftWheelIcon).setBackgroundResource(R.color.Orange);
                                tireThreeTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                tireThreeTireStatusIconTextView.setText("WARNING");
                            } else if (tireThree.getStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tractorTireThreeIcon).setBackgroundResource(R.color.Red);
                                findViewById(R.id.main_showVehicle_rearLeftWheelIcon).setBackgroundResource(R.color.Red);
                                tireThreeTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                tireThreeTireStatusIconTextView.setText("NOT READY");
                            } else {
                                findViewById(R.id.main_showVehicle_tractorTireThreeIcon).setBackgroundResource(R.color.Black);
                                findViewById(R.id.main_showVehicle_rearLeftWheelIcon).setBackgroundResource(R.color.Black);
                                tireThreeTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                tireThreeTireStatusIconTextView.setText("UNKNOWN");
                            }
                            tireThreeTireStatusIconTextView.setOnClickListener(v -> {
                                searchDatabase(tireThree.getEquipmentID());
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                    }
                }, error ->

        {
            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

        });
        tireThreeQueue.getCache().clear();
        tireThreeQueue.add(request);

        //Tire Four
        Tire tireFour = new Tire();
        TextView tireFourTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireFourTireStatusIcon);
        tireFourTireStatusIconTextView.setTextColor(Color.WHITE);

        RequestQueue tireFourQueue = Volley.newRequestQueue(MainActivity.this);
        try {
            parameter.put("equipmentID", vehicle.getTireFour());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                responseTireFour -> {
                    try {

                        boolean searchSuccess = responseTireFour.getBoolean("success");
                        if (searchSuccess) {
                            tireFour.buildTire(responseTireFour);
                            tireFour.checkStatus();

                            //Set Certification Status

                            if (tireFour.getCertificationStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireFourCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireFour.getCertificationStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireFourCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireFour.getCertificationStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireFourCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireFourCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set Mileage Status

                            if (tireFour.getMileageStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireFourMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireFour.getMileageStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireFourMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireFour.getMileageStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireFourMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireFourMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set TreadDepth Status

                            if (tireFour.getTreadDepthStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireFourTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireFour.getTreadDepthStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireFourTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireFour.getTreadDepthStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireFourTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireFourTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set TireAge Status

                            if (tireFour.getTireAgeStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireFourTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireFour.getTireAgeStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireFourTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireFour.getTireAgeStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireFourTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireFourTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set Status
                            if (tireFour.getStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tractorTireFourIcon).setBackgroundResource(R.color.Green);
                                findViewById(R.id.main_showVehicle_rearRightWheelIcon).setBackgroundResource(R.color.Green);
                                tireFourTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                tireFourTireStatusIconTextView.setText("READY");
                            } else if (tireFour.getStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tractorTireFourIcon).setBackgroundResource(R.color.Orange);
                                findViewById(R.id.main_showVehicle_rearRightWheelIcon).setBackgroundResource(R.color.Orange);
                                tireFourTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                tireFourTireStatusIconTextView.setText("WARNING");
                            } else if (tireFour.getStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tractorTireFourIcon).setBackgroundResource(R.color.Red);
                                findViewById(R.id.main_showVehicle_rearRightWheelIcon).setBackgroundResource(R.color.Red);
                                tireFourTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                tireFourTireStatusIconTextView.setText("NOT READY");
                            } else {
                                findViewById(R.id.main_showVehicle_tractorTireFourIcon).setBackgroundResource(R.color.Black);
                                findViewById(R.id.main_showVehicle_rearRightWheelIcon).setBackgroundResource(R.color.Black);
                                tireFourTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                tireFourTireStatusIconTextView.setText("UNKNOWN");
                            }
                            tireFourTireStatusIconTextView.setOnClickListener(v -> {
                                searchDatabase(tireFour.getEquipmentID());
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                    }
                }, error ->

        {
            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

        });
        tireFourQueue.getCache().clear();
        tireFourQueue.add(request);

        //Tire Five
        Tire tireFive = new Tire();
        TextView tireFiveTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireFiveTireStatusIcon);
        tireFiveTireStatusIconTextView.setTextColor(Color.WHITE);

        RequestQueue tireFiveQueue = Volley.newRequestQueue(MainActivity.this);
        try {
            parameter.put("equipmentID", vehicle.getTireFive());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                responseTireFive -> {
                    try {

                        boolean searchSuccess = responseTireFive.getBoolean("success");
                        if (searchSuccess) {
                            tireFive.buildTire(responseTireFive);
                            tireFive.checkStatus();

                            //Set Certification Status

                            if (tireFive.getCertificationStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireFiveCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireFive.getCertificationStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireFiveCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireFive.getCertificationStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireFiveCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireFiveCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set Mileage Status

                            if (tireFive.getMileageStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireFiveMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireFive.getMileageStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireFiveMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireFive.getMileageStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireFiveMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireFiveMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set TreadDepth Status

                            if (tireFive.getTreadDepthStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireFiveTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireFive.getTreadDepthStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireFiveTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireFive.getTreadDepthStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireFiveTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireFiveTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set TireAge Status

                            if (tireFive.getTireAgeStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tireFiveTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                            } else if (tireFive.getTireAgeStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tireFiveTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                            } else if (tireFive.getTireAgeStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tireFiveTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                            } else
                                findViewById(R.id.main_showVehicle_tireFiveTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                            //Set Status
                            if (tireFive.getStatus().equals("READY")) {
                                findViewById(R.id.main_showVehicle_tractorTireFiveIcon).setBackgroundResource(R.color.Green);
                                findViewById(R.id.main_showVehicle_spareWheelIcon).setBackgroundResource(R.color.Green);
                                tireFiveTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                tireFiveTireStatusIconTextView.setText("READY");
                            } else if (tireFive.getStatus().equals("WARNING")) {
                                findViewById(R.id.main_showVehicle_tractorTireFiveIcon).setBackgroundResource(R.color.Orange);
                                findViewById(R.id.main_showVehicle_spareWheelIcon).setBackgroundResource(R.color.Orange);
                                tireFiveTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                tireFiveTireStatusIconTextView.setText("WARNING");
                            } else if (tireFive.getStatus().equals("NOT READY")) {
                                findViewById(R.id.main_showVehicle_tractorTireFiveIcon).setBackgroundResource(R.color.Red);
                                findViewById(R.id.main_showVehicle_spareWheelIcon).setBackgroundResource(R.color.Red);
                                tireFiveTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                tireFiveTireStatusIconTextView.setText("NOT READY");
                            } else {
                                findViewById(R.id.main_showVehicle_tractorTireFiveIcon).setBackgroundResource(R.color.Black);
                                findViewById(R.id.main_showVehicle_spareWheelIcon).setBackgroundResource(R.color.Black);
                                tireFiveTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                tireFiveTireStatusIconTextView.setText("UNKNOWN");
                            }
                            tireFiveTireStatusIconTextView.setOnClickListener(v -> {
                                searchDatabase(tireFive.getEquipmentID());
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                    }
                }, error ->

        {
            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

        });
        tireFiveQueue.getCache().clear();
        tireFiveQueue.add(request);

        if (vehicle.getNumberOfTires() == 4) {
            findViewById(R.id.main_showVehicle_fourWheelerFrontTireIcon).setVisibility(View.VISIBLE);
            findViewById(R.id.main_showVehicle_fourWheelerRearTireIcon).setVisibility(View.VISIBLE);
            findViewById(R.id.main_showVehicle_tractorHeadIcon).setVisibility(View.GONE);
            findViewById(R.id.main_showVehicle_trailerIcon).setVisibility(View.GONE);
            findViewById(R.id.main_showVehicle_fourWheelerLabel).setVisibility(View.VISIBLE);
            findViewById(R.id.main_showVehicle_twentyTwoWheelerLabel).setVisibility(View.GONE);
            findViewById(R.id.main_showVehicle_tiresSixToTwentyTwoEquipmentID).setVisibility(View.GONE);

        } else if (vehicle.getNumberOfTires() == 22) {
            findViewById(R.id.main_showVehicle_fourWheelerFrontTireIcon).setVisibility(View.GONE);
            findViewById(R.id.main_showVehicle_fourWheelerRearTireIcon).setVisibility(View.GONE);
            findViewById(R.id.main_showVehicle_tractorHeadIcon).setVisibility(View.VISIBLE);
            findViewById(R.id.main_showVehicle_trailerIcon).setVisibility(View.VISIBLE);
            findViewById(R.id.main_showVehicle_fourWheelerLabel).setVisibility(View.GONE);
            findViewById(R.id.main_showVehicle_twentyTwoWheelerLabel).setVisibility(View.VISIBLE);
            findViewById(R.id.main_showVehicle_tiresSixToTwentyTwoEquipmentID).setVisibility(View.VISIBLE);

            //Tire Six
            Tire tireSix = new Tire();
            TextView tireSixTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireSixTireStatusIcon);
            tireSixTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireSixQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireSix());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireSix -> {
                        try {

                            boolean searchSuccess = responseTireSix.getBoolean("success");
                            if (searchSuccess) {
                                tireSix.buildTire(responseTireSix);
                                tireSix.checkStatus();

                                //Set Certification Status

                                if (tireSix.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSix.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSixCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSix.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSixCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireSix.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSix.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSixMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSix.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSixMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireSix.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSix.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSixTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSix.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSixTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireSix.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSix.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSixTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSix.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSixTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireSix.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tractorTireSixIcon).setBackgroundResource(R.color.Green);
                                    tireSixTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireSixTireStatusIconTextView.setText("READY");
                                } else if (tireSix.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tractorTireSixIcon).setBackgroundResource(R.color.Orange);
                                    tireSixTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireSixTireStatusIconTextView.setText("WARNING");
                                } else if (tireSix.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tractorTireSixIcon).setBackgroundResource(R.color.Red);
                                    tireSixTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireSixTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_tractorTireSixIcon).setBackgroundResource(R.color.Black);
                                    tireSixTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireSixTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireSixTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireSix.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireSixQueue.getCache().clear();
            tireSixQueue.add(request);

            //Tire Seven
            Tire tireSeven = new Tire();
            TextView tireSevenTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireSevenTireStatusIcon);
            tireSevenTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireSevenQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireSeven());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireSeven -> {
                        try {

                            boolean searchSuccess = responseTireSeven.getBoolean("success");
                            if (searchSuccess) {
                                tireSeven.buildTire(responseTireSeven);
                                tireSeven.checkStatus();

                                //Set Certification Status

                                if (tireSeven.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSevenCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSeven.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSevenCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSeven.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSevenCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSevenCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireSeven.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSevenMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSeven.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSevenMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSeven.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSevenMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSevenMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireSeven.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSevenTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSeven.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSevenTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSeven.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSevenTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSevenTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireSeven.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSevenTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSeven.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSevenTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSeven.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSevenTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSevenTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireSeven.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tractorTireSevenIcon).setBackgroundResource(R.color.Green);
                                    tireSevenTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireSevenTireStatusIconTextView.setText("READY");
                                } else if (tireSeven.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tractorTireSevenIcon).setBackgroundResource(R.color.Orange);
                                    tireSevenTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireSevenTireStatusIconTextView.setText("WARNING");
                                } else if (tireSeven.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tractorTireSevenIcon).setBackgroundResource(R.color.Red);
                                    tireSevenTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireSevenTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_tractorTireSevenIcon).setBackgroundResource(R.color.Black);
                                    tireSevenTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireSevenTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireSevenTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireSeven.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireSevenQueue.getCache().clear();
            tireSevenQueue.add(request);

            //Tire Eight
            Tire tireEight = new Tire();
            TextView tireEightTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireEightTireStatusIcon);
            tireEightTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireEightQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireEight());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireEight -> {
                        try {

                            boolean searchSuccess = responseTireEight.getBoolean("success");
                            if (searchSuccess) {
                                tireEight.buildTire(responseTireEight);
                                tireEight.checkStatus();

                                //Set Certification Status

                                if (tireEight.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireEightCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEight.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireEightCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEight.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireEightCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireEightCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireEight.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireEightMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEight.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireEightMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEight.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireEightMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireEightMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireEight.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireEightTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEight.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireEightTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEight.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireEightTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireEightTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireEight.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireEightTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEight.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireEightTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEight.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireEightTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireEightTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireEight.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tractorTireEightIcon).setBackgroundResource(R.color.Green);
                                    tireEightTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireEightTireStatusIconTextView.setText("READY");
                                } else if (tireEight.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tractorTireEightIcon).setBackgroundResource(R.color.Orange);
                                    tireEightTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireEightTireStatusIconTextView.setText("WARNING");
                                } else if (tireEight.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tractorTireEightIcon).setBackgroundResource(R.color.Red);
                                    tireEightTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireEightTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_tractorTireEightIcon).setBackgroundResource(R.color.Black);
                                    tireEightTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireEightTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireEightTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireEight.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireEightQueue.getCache().clear();
            tireEightQueue.add(request);

            //Tire Nine
            Tire tireNine = new Tire();
            TextView tireNineTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireNineTireStatusIcon);
            tireNineTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireNineQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireNine());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireNine -> {
                        try {

                            boolean searchSuccess = responseTireNine.getBoolean("success");
                            if (searchSuccess) {
                                tireNine.buildTire(responseTireNine);
                                tireNine.checkStatus();

                                //Set Certification Status

                                if (tireNine.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireNine.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireNineCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireNine.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireNineCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireNine.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireNine.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireNineMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireNine.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireNineMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireNine.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireNine.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireNineTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireNine.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireNineTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireNine.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireNine.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireNineTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireNine.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireNineTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireNine.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tractorTireNineIcon).setBackgroundResource(R.color.Green);
                                    tireNineTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireNineTireStatusIconTextView.setText("READY");
                                } else if (tireNine.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tractorTireNineIcon).setBackgroundResource(R.color.Orange);
                                    tireNineTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireNineTireStatusIconTextView.setText("WARNING");
                                } else if (tireNine.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tractorTireNineIcon).setBackgroundResource(R.color.Red);
                                    tireNineTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireNineTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_tractorTireNineIcon).setBackgroundResource(R.color.Black);
                                    tireNineTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireNineTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireNineTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireNine.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireNineQueue.getCache().clear();
            tireNineQueue.add(request);

            //Tire Ten
            Tire tireTen = new Tire();
            TextView tireTenTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireTenTireStatusIcon);
            tireTenTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireTenQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireTen());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireTen -> {
                        try {

                            boolean searchSuccess = responseTireTen.getBoolean("success");
                            if (searchSuccess) {
                                tireTen.buildTire(responseTireTen);
                                tireTen.checkStatus();

                                //Set Certification Status

                                if (tireTen.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTenCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTen.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTenCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTen.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTenCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTenCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireTen.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTenMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTen.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTenMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTen.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTenMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTenMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireTen.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTenTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTen.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTenTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTen.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTenTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTenTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireTen.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTenTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTen.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTenTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTen.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTenTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTenTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireTen.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tractorTireTenIcon).setBackgroundResource(R.color.Green);
                                    tireTenTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireTenTireStatusIconTextView.setText("READY");
                                } else if (tireTen.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tractorTireTenIcon).setBackgroundResource(R.color.Orange);
                                    tireTenTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireTenTireStatusIconTextView.setText("WARNING");
                                } else if (tireTen.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tractorTireTenIcon).setBackgroundResource(R.color.Red);
                                    tireTenTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireTenTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_tractorTireTenIcon).setBackgroundResource(R.color.Black);
                                    tireTenTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireTenTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireTenTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireTen.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireTenQueue.getCache().clear();
            tireTenQueue.add(request);

            //Tire Eleven
            Tire tireEleven = new Tire();
            TextView tireElevenTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireElevenTireStatusIcon);
            tireElevenTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireElevenQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireEleven());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireEleven -> {
                        try {

                            boolean searchSuccess = responseTireEleven.getBoolean("success");
                            if (searchSuccess) {
                                tireEleven.buildTire(responseTireEleven);
                                tireEleven.checkStatus();

                                //Set Certification Status

                                if (tireEleven.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireElevenCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEleven.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireElevenCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEleven.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireElevenCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireElevenCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireEleven.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireElevenMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEleven.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireElevenMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEleven.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireElevenMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireElevenMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireEleven.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireElevenTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEleven.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireElevenTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEleven.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireElevenTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireElevenTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireEleven.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireElevenTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEleven.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireElevenTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEleven.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireElevenTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireElevenTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireEleven.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireElevenTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireElevenTireStatusIconTextView.setText("READY");
                                } else if (tireEleven.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireElevenTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireElevenTireStatusIconTextView.setText("WARNING");
                                } else if (tireEleven.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireElevenTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireElevenTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireElevenTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireElevenTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireElevenTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireEleven.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireElevenQueue.getCache().clear();
            tireElevenQueue.add(request);

            //Tire Twelve
            Tire tireTwelve = new Tire();
            TextView tireTwelveTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireTwelveTireStatusIcon);
            tireTwelveTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireTwelveQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireTwelve());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireTwelve -> {
                        try {

                            boolean searchSuccess = responseTireTwelve.getBoolean("success");
                            if (searchSuccess) {
                                tireTwelve.buildTire(responseTireTwelve);
                                tireTwelve.checkStatus();

                                //Set Certification Status

                                if (tireTwelve.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwelve.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwelve.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwelveCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireTwelve.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwelve.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwelve.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwelveMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireTwelve.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwelve.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwelve.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwelveTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireTwelve.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwelve.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwelve.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwelveTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwelveTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireTwelve.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireTwelveTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireTwelveTireStatusIconTextView.setText("READY");
                                } else if (tireTwelve.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireTwelveTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireTwelveTireStatusIconTextView.setText("WARNING");
                                } else if (tireTwelve.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireTwelveTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireTwelveTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireTwelveTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireTwelveTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireTwelveTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireTwelve.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireTwelveQueue.getCache().clear();
            tireTwelveQueue.add(request);

            //Tire Thirteen
            Tire tireThirteen = new Tire();
            TextView tireThirteenTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireThirteenTireStatusIcon);
            tireThirteenTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireThirteenQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireThirteen());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireThirteen -> {
                        try {

                            boolean searchSuccess = responseTireThirteen.getBoolean("success");
                            if (searchSuccess) {
                                tireThirteen.buildTire(responseTireThirteen);
                                tireThirteen.checkStatus();

                                //Set Certification Status

                                if (tireThirteen.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireThirteen.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireThirteen.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireThirteenCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireThirteen.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireThirteen.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireThirteen.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireThirteenMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireThirteen.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireThirteen.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireThirteen.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireThirteenTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireThirteen.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireThirteen.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireThirteen.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireThirteenTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireThirteenTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireThirteen.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireThirteenTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireThirteenTireStatusIconTextView.setText("READY");
                                } else if (tireThirteen.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireThirteenTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireThirteenTireStatusIconTextView.setText("WARNING");
                                } else if (tireThirteen.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireThirteenTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireThirteenTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireThirteenTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireThirteenTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireThirteenTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireThirteen.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireThirteenQueue.getCache().clear();
            tireThirteenQueue.add(request);

            //Tire Fourteen
            Tire tireFourteen = new Tire();
            TextView tireFourteenTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireFourteenTireStatusIcon);
            tireFourteenTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireFourteenQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireFourteen());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireFourteen -> {
                        try {

                            boolean searchSuccess = responseTireFourteen.getBoolean("success");
                            if (searchSuccess) {
                                tireFourteen.buildTire(responseTireFourteen);
                                tireFourteen.checkStatus();

                                //Set Certification Status

                                if (tireFourteen.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireFourteen.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireFourteen.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireFourteenCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireFourteen.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireFourteen.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireFourteen.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireFourteenMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireFourteen.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireFourteen.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireFourteen.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireFourteenTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireFourteen.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireFourteen.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireFourteen.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireFourteenTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireFourteenTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireFourteen.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireFourteenTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireFourteenTireStatusIconTextView.setText("READY");
                                } else if (tireFourteen.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireFourteenTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireFourteenTireStatusIconTextView.setText("WARNING");
                                } else if (tireFourteen.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireFourteenTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireFourteenTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireFourteenTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireFourteenTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireFourteenTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireFourteen.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireFourteenQueue.getCache().clear();
            tireFourteenQueue.add(request);

            //Tire Fifteen
            Tire tireFifteen = new Tire();
            TextView tireFifteenTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireFifteenTireStatusIcon);
            tireFifteenTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireFifteenQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireFifteen());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireFifteen -> {
                        try {

                            boolean searchSuccess = responseTireFifteen.getBoolean("success");
                            if (searchSuccess) {
                                tireFifteen.buildTire(responseTireFifteen);
                                tireFifteen.checkStatus();

                                //Set Certification Status

                                if (tireFifteen.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireFifteen.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireFifteen.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireFifteenCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireFifteen.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireFifteen.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireFifteen.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireFifteenMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireFifteen.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireFifteen.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireFifteen.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireFifteenTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireFifteen.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireFifteen.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireFifteen.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireFifteenTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireFifteenTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireFifteen.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireFifteenTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireFifteenTireStatusIconTextView.setText("READY");
                                } else if (tireFifteen.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireFifteenTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireFifteenTireStatusIconTextView.setText("WARNING");
                                } else if (tireFifteen.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireFifteenTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireFifteenTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireFifteenTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireFifteenTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireFifteenTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireFifteen.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireFifteenQueue.getCache().clear();
            tireFifteenQueue.add(request);

            //Tire Sixteen
            Tire tireSixteen = new Tire();
            TextView tireSixteenTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireSixteenTireStatusIcon);
            tireSixteenTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireSixteenQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireSixteen());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireSixteen -> {
                        try {

                            boolean searchSuccess = responseTireSixteen.getBoolean("success");
                            if (searchSuccess) {
                                tireSixteen.buildTire(responseTireSixteen);
                                tireSixteen.checkStatus();

                                //Set Certification Status

                                if (tireSixteen.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSixteen.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSixteen.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSixteenCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireSixteen.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSixteen.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSixteen.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSixteenMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireSixteen.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSixteen.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSixteen.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSixteenTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireSixteen.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSixteen.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSixteen.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSixteenTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSixteenTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireSixteen.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireSixteenTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireSixteenTireStatusIconTextView.setText("READY");
                                } else if (tireSixteen.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireSixteenTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireSixteenTireStatusIconTextView.setText("WARNING");
                                } else if (tireSixteen.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireSixteenTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireSixteenTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireSixteenTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireSixteenTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireSixteenTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireSixteen.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireSixteenQueue.getCache().clear();
            tireSixteenQueue.add(request);

            //Tire Seventeen
            Tire tireSeventeen = new Tire();
            TextView tireSeventeenTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireSeventeenTireStatusIcon);
            tireSeventeenTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireSeventeenQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireSeventeen());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireSeventeen -> {
                        try {

                            boolean searchSuccess = responseTireSeventeen.getBoolean("success");
                            if (searchSuccess) {
                                tireSeventeen.buildTire(responseTireSeventeen);
                                tireSeventeen.checkStatus();

                                //Set Certification Status

                                if (tireSeventeen.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSeventeen.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSeventeen.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSeventeenCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireSeventeen.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSeventeen.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSeventeen.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSeventeenMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireSeventeen.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSeventeen.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSeventeen.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSeventeenTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireSeventeen.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireSeventeen.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireSeventeen.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireSeventeenTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireSeventeenTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireSeventeen.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireSeventeenTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireSeventeenTireStatusIconTextView.setText("READY");
                                } else if (tireSeventeen.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireSeventeenTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireSeventeenTireStatusIconTextView.setText("WARNING");
                                } else if (tireSeventeen.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireSeventeenTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireSeventeenTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireSeventeenTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireSeventeenTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireSeventeenTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireSeventeen.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireSeventeenQueue.getCache().clear();
            tireSeventeenQueue.add(request);

            //Tire Eighteen
            Tire tireEighteen = new Tire();
            TextView tireEighteenTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireEighteenTireStatusIcon);
            tireEighteenTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireEighteenQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireEighteen());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireEighteen -> {
                        try {

                            boolean searchSuccess = responseTireEighteen.getBoolean("success");
                            if (searchSuccess) {
                                tireEighteen.buildTire(responseTireEighteen);
                                tireEighteen.checkStatus();

                                //Set Certification Status

                                if (tireEighteen.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEighteen.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEighteen.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireEighteenCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireEighteen.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEighteen.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEighteen.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireEighteenMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireEighteen.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEighteen.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEighteen.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireEighteenTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireEighteen.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireEighteen.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireEighteen.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireEighteenTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireEighteenTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireEighteen.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireEighteenTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireEighteenTireStatusIconTextView.setText("READY");
                                } else if (tireEighteen.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireEighteenTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireEighteenTireStatusIconTextView.setText("WARNING");
                                } else if (tireEighteen.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireEighteenTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireEighteenTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireEighteenTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireEighteenTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireEighteenTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireEighteen.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireEighteenQueue.getCache().clear();
            tireEighteenQueue.add(request);

            //Tire Nineteen
            Tire tireNineteen = new Tire();
            TextView tireNineteenTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireNineteenTireStatusIcon);
            tireNineteenTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireNineteenQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireNineteen());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireNineteen -> {
                        try {

                            boolean searchSuccess = responseTireNineteen.getBoolean("success");
                            if (searchSuccess) {
                                tireNineteen.buildTire(responseTireNineteen);
                                tireNineteen.checkStatus();

                                //Set Certification Status

                                if (tireNineteen.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireNineteen.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireNineteen.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireNineteenCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireNineteen.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireNineteen.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireNineteen.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireNineteenMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireNineteen.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireNineteen.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireNineteen.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireNineteenTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireNineteen.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireNineteen.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireNineteen.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireNineteenTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireNineteenTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireNineteen.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireNineteenTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireNineteenTireStatusIconTextView.setText("READY");
                                } else if (tireNineteen.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireNineteenTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireNineteenTireStatusIconTextView.setText("WARNING");
                                } else if (tireNineteen.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireNineteenTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireNineteenTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireNineteenTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireNineteenTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireNineteenTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireNineteen.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireNineteenQueue.getCache().clear();
            tireNineteenQueue.add(request);

            //Tire Twenty
            Tire tireTwenty = new Tire();
            TextView tireTwentyTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireTwentyTireStatusIcon);
            tireTwentyTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireTwentyQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireTwenty());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireTwenty -> {
                        try {

                            boolean searchSuccess = responseTireTwenty.getBoolean("success");
                            if (searchSuccess) {
                                tireTwenty.buildTire(responseTireTwenty);
                                tireTwenty.checkStatus();

                                //Set Certification Status

                                if (tireTwenty.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwenty.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwenty.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireTwenty.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwenty.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwenty.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireTwenty.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwenty.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwenty.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireTwenty.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwenty.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwenty.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireTwenty.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireTwentyTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireTwentyTireStatusIconTextView.setText("READY");
                                } else if (tireTwenty.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireTwentyTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireTwentyTireStatusIconTextView.setText("WARNING");
                                } else if (tireTwenty.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireTwentyTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireTwentyTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireTwentyTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireTwentyTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireTwentyTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireTwenty.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireTwentyQueue.getCache().clear();
            tireTwentyQueue.add(request);

            //Tire Twenty One
            Tire tireTwentyOne = new Tire();
            TextView tireTwentyOneTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireTwentyOneTireStatusIcon);
            tireTwentyOneTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireTwentyOneQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireTwentyOne());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireTwentyOne -> {
                        try {

                            boolean searchSuccess = responseTireTwentyOne.getBoolean("success");
                            if (searchSuccess) {
                                tireTwentyOne.buildTire(responseTireTwentyOne);
                                tireTwentyOne.checkStatus();

                                //Set Certification Status

                                if (tireTwentyOne.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwentyOne.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwentyOne.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyOneCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireTwentyOne.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwentyOne.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwentyOne.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyOneMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireTwentyOne.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwentyOne.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwentyOne.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyOneTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireTwentyOne.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwentyOne.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwentyOne.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyOneTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyOneTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireTwentyOne.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireTwentyOneTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireTwentyOneTireStatusIconTextView.setText("READY");
                                } else if (tireTwentyOne.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireTwentyOneTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireTwentyOneTireStatusIconTextView.setText("WARNING");
                                } else if (tireTwentyOne.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireTwentyOneTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireTwentyOneTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireTwentyOneTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireTwentyOneTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireTwentyOneTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireTwentyOne.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireTwentyOneQueue.getCache().clear();
            tireTwentyOneQueue.add(request);

            //Tire Twenty Two
            Tire tireTwentyTwo = new Tire();
            TextView tireTwentyTwoTireStatusIconTextView = findViewById(R.id.main_showVehicle_tireTwentyTwoTireStatusIcon);
            tireTwentyTwoTireStatusIconTextView.setTextColor(Color.WHITE);

            RequestQueue tireTwentyTwoQueue = Volley.newRequestQueue(MainActivity.this);
            try {
                parameter.put("equipmentID", vehicle.getTireTwentyTwo());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                    responseTireTwentyTwo -> {
                        try {

                            boolean searchSuccess = responseTireTwentyTwo.getBoolean("success");
                            if (searchSuccess) {
                                tireTwentyTwo.buildTire(responseTireTwentyTwo);
                                tireTwentyTwo.checkStatus();

                                //Set Certification Status

                                if (tireTwentyTwo.getCertificationStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoCertificationIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwentyTwo.getCertificationStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoCertificationIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwentyTwo.getCertificationStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoCertificationIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoCertificationIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Mileage Status

                                if (tireTwentyTwo.getMileageStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoMileageIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwentyTwo.getMileageStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoMileageIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwentyTwo.getMileageStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoMileageIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoMileageIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TreadDepth Status

                                if (tireTwentyTwo.getTreadDepthStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoTreadDepthIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwentyTwo.getTreadDepthStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoTreadDepthIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwentyTwo.getTreadDepthStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoTreadDepthIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoTreadDepthIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set TireAge Status

                                if (tireTwentyTwo.getTireAgeStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoTireAgeIcon).setBackgroundResource(R.drawable.greenicon_small);
                                } else if (tireTwentyTwo.getTireAgeStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoTireAgeIcon).setBackgroundResource(R.drawable.warningicon_small);
                                } else if (tireTwentyTwo.getTireAgeStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoTireAgeIcon).setBackgroundResource(R.drawable.stopicon_small);
                                } else
                                    findViewById(R.id.main_showVehicle_tireTwentyTwoTireAgeIcon).setBackgroundResource(R.drawable.questionmark_icon);

                                //Set Status
                                if (tireTwentyTwo.getStatus().equals("READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Green);
                                    tireTwentyTwoTireStatusIconTextView.setBackgroundResource(R.color.Green);
                                    tireTwentyTwoTireStatusIconTextView.setText("READY");
                                } else if (tireTwentyTwo.getStatus().equals("WARNING")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Orange);
                                    tireTwentyTwoTireStatusIconTextView.setBackgroundResource(R.color.Orange);
                                    tireTwentyTwoTireStatusIconTextView.setText("WARNING");
                                } else if (tireTwentyTwo.getStatus().equals("NOT READY")) {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Red);
                                    tireTwentyTwoTireStatusIconTextView.setBackgroundResource(R.color.Red);
                                    tireTwentyTwoTireStatusIconTextView.setText("NOT READY");
                                } else {
                                    findViewById(R.id.main_showVehicle_trailerElevenIcon).setBackgroundResource(R.color.Black);
                                    tireTwentyTwoTireStatusIconTextView.setBackgroundResource(R.color.Black);
                                    tireTwentyTwoTireStatusIconTextView.setText("UNKNOWN");
                                }
                                tireTwentyTwoTireStatusIconTextView.setOnClickListener(v -> {
                                    searchDatabase(tireTwentyTwo.getEquipmentID());
                                });
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                        }
                    }, error ->

            {
                Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();

            });
            tireTwentyTwoQueue.getCache().clear();
            tireTwentyTwoQueue.add(request);


//Overide Vehicle Status with Tire Status


//            else if (tireOne.getStatus().equals("NOT READY") ||
//                    tireTwo.getStatus().equals("NOT READY") ||
//                    tireThree.getStatus().equals("NOT READY") ||
//                    tireFour.getStatus().equals("NOT READY") ||
//                    tireFive.getStatus().equals("NOT READY") ||
//                    tireSix.getStatus().equals("NOT READY") ||
//                    tireSeven.getStatus().equals("NOT READY") ||
//                    tireEight.getStatus().equals("NOT READY") ||
//                    tireNine.getStatus().equals("NOT READY") ||
//                    tireTen.getStatus().equals("NOT READY") ||
//                    tireEleven.getStatus().equals("NOT READY") ||
//                    tireTwelve.getStatus().equals("NOT READY") ||
//                    tireThirteen.getStatus().equals("NOT READY") ||
//                    tireFourteen.getStatus().equals("NOT READY") ||
//                    tireFifteen.getStatus().equals("NOT READY") ||
//                    tireSixteen.getStatus().equals("NOT READY") ||
//                    tireSeventeen.getStatus().equals("NOT READY") ||
//                    tireEighteen.getStatus().equals("NOT READY") ||
//                    tireNineteen.getStatus().equals("NOT READY") ||
//                    tireTwenty.getStatus().equals("NOT READY") ||
//                    tireTwentyOne.getStatus().equals("NOT READY") ||
//                    tireTwentyTwo.getStatus().equals("NOT READY")
//            ) {
//                status = "NOT READY";
//            } else if (tireOne.getStatus().equals("UNKNOWN") ||
//                    tireTwo.getStatus().equals("UNKNOWN") ||
//                    tireThree.getStatus().equals("UNKNOWN") ||
//                    tireFour.getStatus().equals("UNKNOWN") ||
//                    tireFive.getStatus().equals("UNKNOWN") ||
//                    tireSix.getStatus().equals("UNKNOWN") ||
//                    tireSeven.getStatus().equals("UNKNOWN") ||
//                    tireEight.getStatus().equals("UNKNOWN") ||
//                    tireNine.getStatus().equals("UNKNOWN") ||
//                    tireTen.getStatus().equals("UNKNOWN") ||
//                    tireEleven.getStatus().equals("UNKNOWN") ||
//                    tireTwelve.getStatus().equals("UNKNOWN") ||
//                    tireThirteen.getStatus().equals("UNKNOWN") ||
//                    tireFourteen.getStatus().equals("UNKNOWN") ||
//                    tireFifteen.getStatus().equals("UNKNOWN") ||
//                    tireSixteen.getStatus().equals("UNKNOWN") ||
//                    tireSeventeen.getStatus().equals("UNKNOWN") ||
//                    tireEighteen.getStatus().equals("UNKNOWN") ||
//                    tireNineteen.getStatus().equals("UNKNOWN") ||
//                    tireTwenty.getStatus().equals("UNKNOWN") ||
//                    tireTwentyOne.getStatus().equals("UNKNOWN") ||
//                    tireTwentyTwo.getStatus().equals("UNKNOWN")
//            ) {
//                status = "UNKNOWN";
//            }

        }
    }

    private void showTire(JSONObject response) throws JSONException {
        Tire tire = new Tire();
        tire.buildTire(response);
        tire.checkStatus();

        switch (queryOperationalStatus) {
            case "ACTIVE":
                tire.checkStatus();
                status = tire.getStatus();
                break;

            case "UNDER REPAIR":
                status = "UNDER REPAIR";
                break;

            case "JUNK":
                status = "JUNK";
                break;

            default:
                status = "UNKNOWN";
                break;
        }

        Button certificationIcon = findViewById(R.id.main_showTireCertificationIcon);
        Button mileageIcon = findViewById(R.id.main_showTireMileageIcon);
        Button tireAgeIcon = findViewById(R.id.main_showTireTireAgeIcon);
        Button treadDepthIcon = findViewById(R.id.main_showTireTreadDepthIcon);
        TextView installedOnIcon = findViewById(R.id.main_showTireInstalledOnIcon);
        TextView wheelWellIcon = findViewById(R.id.main_showTireWheelWellIcon);

        TextView certificationExpiryDateTextView = findViewById(R.id.main_showTireCertificationExpiryDate);
        TextView ownerTextView = findViewById(R.id.main_showTireOwner);
        TextView supplierNameTextView = findViewById(R.id.main_showTireSupplierName);
        TextView makerTextView = findViewById(R.id.main_showTireMaker);
        TextView modelTextView = findViewById(R.id.main_showTireModel);
        TextView mileageTextView = findViewById(R.id.main_showTireMileage);
        TextView treadDepthTextView = findViewById(R.id.main_showTireTreadDepth);
        TextView installedOnTextView = findViewById(R.id.main_showTireInstalledOn);
        TextView wheelWellTextView = findViewById(R.id.main_showTireWheelWell);
        TextView dateInServiceTextView = findViewById(R.id.main_showTireDateInService);
        TextView dateOfManufactureTextView = findViewById(R.id.main_showTireDateOfManufacture);
        TextView serviceTypeTextView = findViewById(R.id.main_showTireServiceType);
        TextView widthTextView = findViewById(R.id.main_showTireWidth);
        TextView aspectRatioTextView = findViewById(R.id.main_showTireAspectRatio);
        TextView constructionTextView = findViewById(R.id.main_showTireConstruction);
        TextView rimDiameterTextView = findViewById(R.id.main_showTireRimDiameter);
        TextView loadIndexTextView = findViewById(R.id.main_showTireLoadIndex);
        TextView speedRatingTextView = findViewById(R.id.main_showTireSpeedRating);
        TextView treadRatingTextView = findViewById(R.id.main_showTireTreadRating);
        TextView tractionRatingTextView = findViewById(R.id.main_showTireTractionRating);
        TextView temperatureRatingTextView = findViewById(R.id.main_showTireTemperatureRating);
        TextView coldTirePressureSpecifications = findViewById(R.id.main_showTireColdTirePressureSpecifications);
        TextView latitudeTextView = findViewById(R.id.main_latitude);
        TextView longitudeTextView = findViewById(R.id.main_longitude);

        String certificationRemark = "";
        if (tire.getCertificationStatus().equals("NOT READY")) {
            certificationIcon.setBackgroundResource(R.drawable.certificationiconred);
            certificationExpiryDateTextView.setTextColor(Color.parseColor("#FF0000"));
            certificationRemark = "\n* Certification EXPIRED";
        } else if (tire.getCertificationStatus().equals("WARNING")) {
            certificationIcon.setBackgroundResource(R.drawable.certificationiconorange);
            certificationExpiryDateTextView.setBackgroundResource(R.color.Orange);
            certificationRemark = "\n* Certification ALMOST expired";
        } else certificationIcon.setBackgroundResource(R.drawable.certificationicongreen);

        String mileageRemark = "";
        if (tire.getMileageStatus().equals("NOT READY")) {
            mileageIcon.setBackgroundResource(R.drawable.mileageiconred);
            mileageTextView.setTextColor(Color.parseColor("#FF0000"));
            mileageRemark = "\n* Mileage EXCEEDS limit";
        } else if (tire.getMileageStatus().equals("WARNING")) {
            mileageIcon.setBackgroundResource(R.drawable.mileageorange);
            mileageTextView.setTextColor(Color.parseColor("#FF8C00"));
            mileageRemark = "\n* Mileage NEAR limit";
        } else mileageIcon.setBackgroundResource(R.drawable.mileageicongreen);
        mileageIcon.setText(Integer.toString(tire.getMileage() / 1000));

        String tireAgeRemark = "";
        if (tire.getTireAgeStatus().equals("NOT READY")) {
            tireAgeIcon.setBackgroundResource(R.drawable.tireageiconred);
            dateOfManufactureTextView.setTextColor(Color.parseColor("#FF0000"));
            tireAgeRemark = "\n* Tire is MORE THAN 10 yrs old";
        } else if (tire.getTireAgeStatus().equals("WARNING")) {
            tireAgeIcon.setBackgroundResource(R.drawable.tireageiconorange);
            dateOfManufactureTextView.setTextColor(Color.parseColor("#FF8C00"));
            tireAgeRemark = "\n* Tire is ALMOST 10 yrs old";
        } else tireAgeIcon.setBackgroundResource(R.drawable.tireageicongreen);
        tireAgeIcon.setText(Long.toString(tire.tireAge()));

        String treadDepthRemark = "";
        if (tire.getTreadDepthStatus().equals("NOT READY")) {
            treadDepthIcon.setBackgroundResource(R.drawable.treaddepthred);
            treadDepthTextView.setTextColor(Color.parseColor("#FF0000"));
            treadDepthRemark = "\n* Tread is LESS THAN limit";
        } else if (tire.getTreadDepthStatus().equals("WARNING")) {
            treadDepthIcon.setBackgroundResource(R.drawable.treaddepthyellow);
            treadDepthTextView.setTextColor(Color.parseColor("#FF8C00"));
            treadDepthRemark = "\n* Tread is ALMOST at limit";
        } else treadDepthIcon.setBackgroundResource(R.drawable.treaddepthgreen);
        treadDepthIcon.setText(Double.toString(tire.getTreadDepth()));

        status = tire.getStatus();

        installedOnIcon.setText(tire.getInstalledOn());
        wheelWellIcon.setText(tire.getWheelWell());

        certificationExpiryDateTextView.setText(tire.getCertificationExpiryDate() + certificationRemark);
        ownerTextView.setText(tire.getAssetOwnerName());
        supplierNameTextView.setText(tire.getSupplierName());
        makerTextView.setText(tire.getMaker());
        modelTextView.setText(tire.getModel());
        String s = null;
        try {
            s = String.format("%,d", tire.getMileage()) + mileageRemark;
        } catch (NumberFormatException e) {
        }
        mileageTextView.setText(s);
        String tread = (tire.getTreadDepth()) + treadDepthRemark;
        treadDepthTextView.setText(tread);
        installedOnTextView.setText(tire.getInstalledOn());
        wheelWellTextView.setText(tire.getWheelWell());
        dateInServiceTextView.setText(tire.getDateInService());
        dateOfManufactureTextView.setText(tire.getDateOfManufacture() + tireAgeRemark);
        serviceTypeTextView.setText(tire.getServiceType());
        widthTextView.setText(tire.getWidth());
        aspectRatioTextView.setText(tire.getAspectRatio());
        constructionTextView.setText(tire.getConstruction());
        rimDiameterTextView.setText(tire.getRimDiameter());
        loadIndexTextView.setText(tire.getLoadIndex());
        speedRatingTextView.setText(tire.getSpeedRating());
        treadRatingTextView.setText(tire.getTreadRating());
        tractionRatingTextView.setText(tire.getTractionRating());
        temperatureRatingTextView.setText(tire.getTemperatureRating());
        coldTirePressureSpecifications.setText(tire.getColdTirePressureSpecifications());
        latitudeTextView.setText(Double.toString(tire.getLatitude()));
        longitudeTextView.setText(Double.toString(tire.getLongitude()));

        leftButton.setOnClickListener(v -> {

                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    if (queryEquipmentID.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Search Equipment ID", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (username.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Type Email Address", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (password.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.apply();//this writes it to the phone, phone remembers data even on App exit//

                    doLogInRequest(username, password, "Change");

                }
        );

        rightButton.setOnClickListener(v -> {

                    String username = usernameEditText.getText().toString();
                    String password = passwordEditText.getText().toString();
                    String currentOdometer = currentOdometerEditText.getText().toString();

                    if (username.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Type Email Address", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (password.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (currentOdometer.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Current Odometer", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    SharedPreferences.Editor editor = prefs.edit();

                    editor.putString("username", username);
                    editor.putString("password", password);
                    editor.apply();//this writes it to the phone, phone remembers data even on App exit//

                    doLogInRequest(username, password, "Inspect");

                }
        );

    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
            return false;
        }
    }

    private void doLogInRequest(String pUsername, String pPassword, String pTask, String pCurrentOdometer) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.lawtechthai.com/login.php";
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("username", pUsername);
            parameter.put("password", pPassword);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {

                    try {
                        boolean logInSuccess = response.getBoolean("success");
                        String userType = response.getString("userType");
                        String company = response.getString("company");
                        String firstname = response.getString("firstname");
                        String lastname = response.getString("lastname");
                        String jobTitle = response.getString("jobTitle");

                        if (logInSuccess) {

                            if (pTask.equals("Drive")) {
                                String name = firstname + " " + lastname;
                                Intent i = new Intent(this, DriveVehicleActivity.class);
                                i.putExtra("company", company);
                                i.putExtra("name", name);
                                i.putExtra("jobTitle", jobTitle);
                                i.putExtra("currentOdometer", pCurrentOdometer);
                                i.putExtra("equipmentID", queryEquipmentID);
                                i.putExtra("status", status);
                                startActivity(i);
                            } else if (pTask.equals("Maintenance") && userType.equals("Inspector")) {
                                String name = firstname + " " + lastname;
                                Intent i = new Intent(this, EditVehicleActivity.class);
                                i.putExtra("company", company);
                                i.putExtra("name", name);
                                i.putExtra("jobTitle", jobTitle);
                                i.putExtra("currentOdometer", pCurrentOdometer);
                                i.putExtra("status", status);
                                startActivity(i);
                            }
                        } else {
                            Toast.makeText(this, "Username/Log In Password Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_SHORT).show();
                    }

                }, error -> Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show());
        queue.getCache().clear();
        queue.add(request);

    }

    private void doLogInRequest(String pUsername, String pPassword, String pTask) {

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.lawtechthai.com/login.php";
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("username", pUsername);
            parameter.put("password", pPassword);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {

                    try {
                        boolean logInSuccess = response.getBoolean("success");
                        String userType = response.getString("userType");
                        String company = response.getString("company");
                        String firstname = response.getString("firstname");
                        String lastname = response.getString("lastname");
                        String jobTitle = response.getString("jobTitle");

                        if (logInSuccess) {

                            if (pTask.equals("Change")) {
                                String name = firstname + " " + lastname;
                                Intent i = new Intent(this, ShowTireActivity.class);
                                i.putExtra("company", company);
                                i.putExtra("name", name);
                                i.putExtra("jobTitle", jobTitle);
                                i.putExtra("equipmentID", queryEquipmentID);
                                i.putExtra("status", status);
                                startActivity(i);
                            } else if (pTask.equals("Repair") && userType.equals("Inspector")) {
                                String name = firstname + " " + lastname;
                                Intent i = new Intent(this, ShowTireActivity.class);
                                i.putExtra("company", company);
                                i.putExtra("name", name);
                                i.putExtra("jobTitle", jobTitle);
                                i.putExtra("status", status);
                                startActivity(i);
                            }
                        } else {
                            Toast.makeText(this, "Username/Log In Password Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_SHORT).show();
                    }

                }, error -> Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show());
        queue.getCache().clear();
        queue.add(request);
    }

    private void readFromIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs = null;
            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];
                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }
            }

            buildTagViews(msgs);
        }
    }

    private void buildTagViews(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0) return;

//        String tagId = new String(msgs[0].getRecords()[0].getType());
        byte[] payload = msgs[0].getRecords()[0].getPayload();
        String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16"; // Get the Text Encoding
        int languageCodeLength = payload[0] & 0063; // Get the Language Code, e.g. "en"
        // String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

        try {
            // Get the Text
            queryEquipmentID = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("UnsupportedEncoding", e.toString());
        }

        searchDatabase(queryEquipmentID);

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

                queryEquipmentID = intent.getStringExtra("SCAN_RESULT");

                searchDatabase(queryEquipmentID);

            } else if (resultCode == RESULT_CANCELED) {
                finish();
            }
        }
    }

    public void checkLocation(String queryLatitude, String queryLongitude) {

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

            double assetLat = Double.parseDouble(queryLatitude);
            double assetLon = Double.parseDouble(queryLongitude);
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

                            RequestQueue queue = Volley.newRequestQueue(this);
                            String url = "https://www.lawtechthai.com/Assets/updateAssetLocation.php";
                            JSONObject parameter = new JSONObject();

                            try {
                                parameter.put("equipmentID", queryEquipmentID);
                                parameter.put("assetType", queryAssetType);
                                parameter.put("latitude", newLat);
                                parameter.put("longitude", newLon);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            ProgressBar circle = findViewById(R.id.main_progress);
                            circle.setVisibility(View.VISIBLE);

                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                                    response -> {
                                        try {
                                            boolean insertSuccess = response.getBoolean("success");
                                            if (insertSuccess) {
                                                Toast toast = Toast.makeText(this, "Asset Location Updated", Toast.LENGTH_LONG);
                                                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 10);
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
                            queue.getCache().clear();
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

    private void vibrate(int length, int times) {

        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= 26) {
            while (times > 0) {
                vibrator.vibrate(VibrationEffect.createOneShot(length, VibrationEffect.DEFAULT_AMPLITUDE));
                times--;
            }
        } else {
            vibrator.vibrate(0);
        }
    }

    private void closeKeyBoard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void dialContactPhone(final String phoneNumber) {
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null)));
    }

    private void sendEmail(String pContactEmail, String pContactPerson, String pEquipmentId) {

        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Request for Maintenance " + pEquipmentId);
        intent.putExtra(Intent.EXTRA_TEXT, "Dear K " + pContactPerson + ", \n\nPlease inspect the following equipment: \n\n" + "Equipment ID: " + pEquipmentId);
        intent.setData(Uri.parse("mailto: " + pContactEmail)); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);

    }

    private void sendLine(String lineID) {
        String sendText = "line://ti/p/~" + lineID;
        Intent intent = null;
        try {
            intent = Intent.parseUri(sendText, Intent.URI_INTENT_SCHEME);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        startActivity(intent);
    }

}
