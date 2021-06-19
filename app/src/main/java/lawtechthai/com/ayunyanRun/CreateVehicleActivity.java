package lawtechthai.com.ayunyanRun;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

public class CreateVehicleActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int REQUEST_CHECK_SETTINGS = 15;
    private static final int REQUEST_FINE_LOCATION = 22;
    private TextView mTextViewCertificationDate;
    private TextView mTextViewExpiryDate;
    private TextView mTextViewRegistrationDate;
    private TextView mTextViewInsuranceExpiryDate;
    private Spinner spinner;
    private int year;
    private int month;
    private int dayOfMonth;
    private Calendar calendar;
    private String inspectorName;
    private String certifyingCompany;
    private String ownerText;
    private String beltString;
    private String hoseString;
    private String oilLeakString;
    private String airconString;
    private String wiperString;
    private String headHiString;
    private String headLoString;
    private String drivingLightsString;
    private String turnSignalString;
    private String brakeLightsString;
    private String hazardLightsString;
    private String doorLocksString;
    private String windshieldString;
    private String windowsString;
    private String radioString;
    private String hornString;
    private String fireExtinguisherString;
    private String firstAidString;
    private String coolantString;
    private String engineOilString;
    private String transmissionOilString;
    private String powerSteeringString;
    private String brakeFluidString;
    private String washerFluidString;
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
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_vehicle);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            inspectorName = extras.getString("inspectorName");
            certifyingCompany = extras.getString("certifyingCompany");
        }

        beltSwitch = findViewById(R.id.createVehicle_beltSwitch);
        hoseSwitch = findViewById(R.id.createVehicle_hoseSwitch);
        oilLeakSwitch = findViewById(R.id.createVehicle_oilLeakSwitch);
        airconSwitch = findViewById(R.id.createVehicle_airconSwitch);
        wiperSwitch = findViewById(R.id.createVehicle_wiperSwitch);
        headHiSwitch = findViewById(R.id.createVehicle_headHiSwitch);
        headLoSwitch = findViewById(R.id.createVehicle_headLoSwitch);
        drivingLightsSwitch = findViewById(R.id.createVehicle_drivingLightsSwitch);
        turnSignalSwitch = findViewById(R.id.createVehicle_turnSignalSwitch);
        brakeLightsSwitch = findViewById(R.id.createVehicle_brakeLightsSwitch);
        hazardLightsSwitch = findViewById(R.id.createVehicle_hazardLightsSwitch);
        doorLocksSwitch = findViewById(R.id.createVehicle_doorLocksSwitch);
        windshieldSwitch = findViewById(R.id.createVehicle_windshieldSwitch);
        windowsSwitch = findViewById(R.id.createVehicle_windowsSwitch);
        radioSwitch = findViewById(R.id.createVehicle_radioSwitch);
        hornSwitch = findViewById(R.id.createVehicle_hornSwitch);
        fireExtinguisherSwitch = findViewById(R.id.createVehicle_fireExtinguisherSwitch);
        firstAidSwitch = findViewById(R.id.createVehicle_firstAidSwitch);
        coolantSwitch = findViewById(R.id.createVehicle_coolantSwitch);
        engineOilSwitch = findViewById(R.id.createVehicle_engineOilSwitch);
        transmissionOilSwitch = findViewById(R.id.createVehicle_transmissionOilSwitch);
        powerSteeringSwitch = findViewById(R.id.createVehicle_powerSteeringSwitch);
        brakeFluidSwitch = findViewById(R.id.createVehicle_brakeFluidSwitch);
        washerFluidSwitch = findViewById(R.id.createVehicle_washerFluidSwitch);
        TextView certifiedByTextView = findViewById(R.id.createVehicle_certifiedByText);
        certifiedByTextView.setText(inspectorName);
        ImageView companyLogo = findViewById(R.id.createVehicle_certifyingCompanyLogo);
        switch (certifyingCompany) {

            case "ACT":
                companyLogo.setBackgroundResource(R.drawable.act_logo);
                break;
            case "B Quick":
                companyLogo.setBackgroundResource(R.drawable.bquick_logo);
                break;
            case "Cockpit":
                companyLogo.setBackgroundResource(R.drawable.cockpit_logo);
                break;
        }

        Button selectCertificationDate = findViewById(R.id.createVehicle_certificationDateButton);
        mTextViewCertificationDate = findViewById(R.id.createVehicle_certificationDate);

        selectCertificationDate.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog certDatePickerDialog = new DatePickerDialog(this,
                    (datePicker, year, month, day) -> mTextViewCertificationDate.setText(day + "/" + (month + 1) + "/" + year), year, month, dayOfMonth);
            certDatePickerDialog.show();
        });

        Button selectExpiryDate = findViewById(R.id.createVehicle_expiryDateButton);
        mTextViewExpiryDate = findViewById(R.id.createVehicle_expiryDate);

        selectExpiryDate.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog expiryDatePickerDialog = new DatePickerDialog(this,
                    (datePicker, year, month, day) -> mTextViewExpiryDate.setText(day + "/" + (month + 1) + "/" + year), year, month, dayOfMonth);
            expiryDatePickerDialog.show();
        });

        Button selectRegistrationDate = findViewById(R.id.createVehicle_registrationDateButton);
        mTextViewRegistrationDate = findViewById(R.id.createVehicle_registrationDate);

        selectRegistrationDate.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog registrationDatePickerDialog = new DatePickerDialog(this,
                    (datePicker, year, month, day) -> mTextViewRegistrationDate.setText(day + "/" + (month + 1) + "/" + year), year, month, dayOfMonth);
            registrationDatePickerDialog.show();
        });

        Button selectInsuranceExpiryDate = findViewById(R.id.createVehicle_insuranceExpiryDateButton);
        mTextViewInsuranceExpiryDate = findViewById(R.id.createVehicle_insuranceExpiryDate);

        selectInsuranceExpiryDate.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog insuranceExpiryDatePickerDialog = new DatePickerDialog(this,
                    (datePicker, year, month, day) -> mTextViewInsuranceExpiryDate.setText(day + "/" + (month + 1) + "/" + year), year, month, dayOfMonth);
            insuranceExpiryDatePickerDialog.show();
        });

        spinner = findViewById(R.id.createVehicle_companySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ownerCompanies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Button currentLocation = findViewById(R.id.createVehicle_locationButton);
        currentLocation.setOnClickListener(v -> {
                    if (checkPermissions()) {
                        findLocation();
                    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.assetcreate_menu, menu);

        menu.getItem(0).setEnabled(true);
        menu.getItem(1).setEnabled(true);
        menu.getItem(2).setEnabled(true);
        menu.getItem(3).setEnabled(true);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        EditText mEditTextAssetCode = findViewById(R.id.createVehicle_assetCode);
        EditText mEditTextSerialNumber = findViewById(R.id.createVehicle_serialNumber);
        EditText mEditTextVIN = findViewById(R.id.createVehicle_vin);
        EditText mEditTextEngineNumber = findViewById(R.id.createVehicle_engineNumber);
        EditText mEditTextPlateNumber = findViewById(R.id.createVehicle_plateNumber);
        EditText mEditTextMaker = findViewById(R.id.createVehicle_maker);
        EditText mEditTextOdometer = findViewById(R.id.createVehicle_odometer);
        EditText mEditTextBeltRemarks = findViewById(R.id.createVehicle_beltRemarks);
        EditText mEditTextHoseRemarks = findViewById(R.id.createVehicle_hoseRemarks);
        EditText mEditTextOilLeakRemarks = findViewById(R.id.createVehicle_oilLeakRemarks);
        EditText mEditTextAirconRemarks = findViewById(R.id.createVehicle_airconRemarks);
        EditText mEditTextWiperRemarks = findViewById(R.id.createVehicle_wiperRemarks);
        EditText mEditTextHeadHiRemarks = findViewById(R.id.createVehicle_headHiRemarks);
        EditText mEditTextHeadLoRemarks = findViewById(R.id.createVehicle_headLoRemarks);
        EditText mEditTextDrivingLightsRemarks = findViewById(R.id.createVehicle_drivingLightsRemarks);
        EditText mEditTextTurnSignalRemarks = findViewById(R.id.createVehicle_turnSignalRemarks);
        EditText mEditTextBrakeLightsRemarks = findViewById(R.id.createVehicle_brakeLightsRemarks);
        EditText mEditTextHazardLightsRemarks = findViewById(R.id.createVehicle_hazardLightsRemarks);
        EditText mEditTextDoorLocksRemarks = findViewById(R.id.createVehicle_doorLocksRemarks);
        EditText mEditTextWindshieldRemarks = findViewById(R.id.createVehicle_windshieldRemarks);
        EditText mEditTextWindowsRemarks = findViewById(R.id.createVehicle_windowsRemarks);
        EditText mEditTextRadioRemarks = findViewById(R.id.createVehicle_radioRemarks);
        EditText mEditTextHornRemarks = findViewById(R.id.createVehicle_hornRemarks);
        EditText mEditTextFireExtinguisherRemarks = findViewById(R.id.createVehicle_fireExtinguisherRemarks);
        EditText mEditTextFirstAidRemarks = findViewById(R.id.createVehicle_firstAidRemarks);
        EditText mEditTextCoolantRemarks = findViewById(R.id.createVehicle_coolantRemarks);
        EditText mEditTextEngineOilRemarks = findViewById(R.id.createVehicle_engineOilRemarks);
        EditText mEditTextTransmissionOilRemarks = findViewById(R.id.createVehicle_transmissionOilRemarks);
        EditText mEditTextPowerSteeringRemarks = findViewById(R.id.createVehicle_powerSteeringRemarks);
        EditText mEditTextBrakeFluidRemarks = findViewById(R.id.createVehicle_brakeFluidRemarks);
        EditText mEditTextWasherFluidRemarks = findViewById(R.id.createVehicle_washerFluidRemarks);
        EditText mEditTextLatitude = findViewById(R.id.createVehicle_latitude);
        EditText mEditTextLongitude = findViewById(R.id.createVehicle_longitude);
        EditText mEditTextImageURL = findViewById(R.id.createVehicle_imageURL);

        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (item.getItemId() == R.id.assetCreate_menu_createButton) {

            String assetCodeText = mEditTextAssetCode.getText().toString();
            String serialNumberText = mEditTextSerialNumber.getText().toString();
            String certificationDateText = mTextViewCertificationDate.getText().toString();
            String expiryDateText = mTextViewExpiryDate.getText().toString();
            String registrationDateText = mTextViewRegistrationDate.getText().toString();
            String insuranceExpiryDateText = mTextViewInsuranceExpiryDate.getText().toString();
            String vinText = mEditTextVIN.getText().toString();
            String engineNumber = mEditTextEngineNumber.getText().toString();
            String plateNumber = mEditTextPlateNumber.getText().toString();
            String makerText = mEditTextMaker.getText().toString();
            String odometerText = mEditTextOdometer.getText().toString();
            String beltRemarks = mEditTextBeltRemarks.getText().toString();
            String hoseRemarks = mEditTextHoseRemarks.getText().toString();
            String oilLeakRemarks = mEditTextOilLeakRemarks.getText().toString();
            String airconRemarks = mEditTextAirconRemarks.getText().toString();
            String wiperRemarks = mEditTextWiperRemarks.getText().toString();
            String headHiRemarks = mEditTextHeadHiRemarks.getText().toString();
            String headLoRemarks = mEditTextHeadLoRemarks.getText().toString();
            String drivingLightsRemarks = mEditTextDrivingLightsRemarks.getText().toString();
            String turnSignalRemarks = mEditTextTurnSignalRemarks.getText().toString();
            String brakeLightsRemarks = mEditTextBrakeLightsRemarks.getText().toString();
            String hazardLightsRemarks = mEditTextHazardLightsRemarks.getText().toString();
            String doorLocksRemarks = mEditTextDoorLocksRemarks.getText().toString();
            String windshieldRemarks = mEditTextWindshieldRemarks.getText().toString();
            String windowsRemarks = mEditTextWindowsRemarks.getText().toString();
            String radioRemarks = mEditTextRadioRemarks.getText().toString();
            String hornRemarks = mEditTextHornRemarks.getText().toString();
            String fireExtinguisherRemarks = mEditTextFireExtinguisherRemarks.getText().toString();
            String firstAidRemarks = mEditTextFirstAidRemarks.getText().toString();
            String coolantRemarks = mEditTextCoolantRemarks.getText().toString();
            String engineOilRemarks = mEditTextEngineOilRemarks.getText().toString();
            String transmissionOilRemarks = mEditTextTransmissionOilRemarks.getText().toString();
            String powerSteeringRemarks = mEditTextPowerSteeringRemarks.getText().toString();
            String brakeFluidRemarks = mEditTextBrakeFluidRemarks.getText().toString();
            String washerFluidRemarks = mEditTextWasherFluidRemarks.getText().toString();
            String latitudeText = mEditTextLatitude.getText().toString();
            String longitudeText = mEditTextLongitude.getText().toString();
            String imageURLText = mEditTextImageURL.getText().toString();

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

            if (assetCodeText.isEmpty()) {
                Toast.makeText(this, "Please Enter Asset Code", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (serialNumberText.isEmpty()) {
                Toast.makeText(this, "Please Enter Serial Number", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (certificationDateText.isEmpty()) {
                Toast.makeText(this, "Please Select Certification Date", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (expiryDateText.isEmpty()) {
                Toast.makeText(this, "Please Select Expiry Date", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (registrationDateText.isEmpty()) {
                Toast.makeText(this, "Please Select Registration Date", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (insuranceExpiryDateText.isEmpty()) {
                Toast.makeText(this, "Please Select Insurance Expiry Date", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (vinText.isEmpty()) {
                Toast.makeText(this, "Please Enter VIN", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (engineNumber.isEmpty()) {
                Toast.makeText(this, "Please Enter Engine Number", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (plateNumber.isEmpty()) {
                Toast.makeText(this, "Please Enter Plate Number", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (ownerText.equals("-select-")) {
                Toast.makeText(this, "Please Select the Owner of this Asset", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (makerText.isEmpty()) {
                Toast.makeText(this, "Please Type in the Maker of this Asset", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (odometerText.isEmpty()) {
                Toast.makeText(this, "Please Enter Current Odometer (km)", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }

            if (latitudeText.isEmpty()) {
                Toast.makeText(this, "Please Enter Latitude", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (longitudeText.isEmpty()) {
                Toast.makeText(this, "Please Enter Longitude", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (imageURLText.isEmpty()) {
                Toast.makeText(this, "Please Enter imageURL", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            createAsset(assetCodeText,
                    serialNumberText,
                    certificationDateText,
                    expiryDateText,
                    registrationDateText,
                    insuranceExpiryDateText,
                    vinText,
                    engineNumber,
                    plateNumber,
                    ownerText,
                    makerText,
                    odometerText,
                    beltString,
                    hoseString,
                    oilLeakString,
                    airconString,
                    wiperString,
                    headHiString,
                    headLoString,
                    drivingLightsString,
                    turnSignalString,
                    brakeLightsString,
                    hazardLightsString,
                    doorLocksString,
                    windshieldString,
                    windowsString,
                    radioString,
                    hornString,
                    fireExtinguisherString,
                    firstAidString,
                    coolantString,
                    engineOilString,
                    transmissionOilString,
                    powerSteeringString,
                    brakeFluidString,
                    washerFluidString,
                    beltRemarks,
                    hoseRemarks,
                    oilLeakRemarks,
                    airconRemarks,
                    wiperRemarks,
                    headHiRemarks,
                    headLoRemarks,
                    drivingLightsRemarks,
                    turnSignalRemarks,
                    brakeLightsRemarks,
                    hazardLightsRemarks,
                    doorLocksRemarks,
                    windshieldRemarks,
                    windowsRemarks,
                    radioRemarks,
                    hornRemarks,
                    fireExtinguisherRemarks,
                    firstAidRemarks,
                    coolantRemarks,
                    engineOilRemarks,
                    transmissionOilRemarks,
                    powerSteeringRemarks,
                    brakeFluidRemarks,
                    washerFluidRemarks,
                    latitudeText,
                    longitudeText,
                    imageURLText
            );
            return true;

        } else if (item.getItemId() == R.id.assetCreate_menu_cancelButton) {
            Intent i = new Intent(this, MainActivity.class);//fix later -redirected to main
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.assetCreate_menu_clearButton) {

            builder = new AlertDialog.Builder(this);

            //Uncomment the below code to Set the message and title from the strings.xml file
//            builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);

            //Setting message manually and performing action on button click
            builder.setMessage("Cannot Be Undone Please Confirm")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {

                        mEditTextAssetCode.getText().clear();
                        mEditTextSerialNumber.getText().clear();
                        mTextViewCertificationDate.setText("");
                        mTextViewExpiryDate.setText("");
                        mTextViewRegistrationDate.setText("");
                        mTextViewInsuranceExpiryDate.setText("");
                        mEditTextMaker.getText().clear();
                        mEditTextOdometer.getText().clear();
                        mEditTextVIN.getText().clear();
                        mEditTextEngineNumber.getText().clear();
                        mEditTextPlateNumber.getText().clear();
                        spinner.setSelection(0);
                        beltSwitch.setChecked(false);
                        hoseSwitch.setChecked(false);
                        oilLeakSwitch.setChecked(false);
                        airconSwitch.setChecked(false);
                        wiperSwitch.setChecked(false);
                        headHiSwitch.setChecked(false);
                        headLoSwitch.setChecked(false);
                        drivingLightsSwitch.setChecked(false);
                        turnSignalSwitch.setChecked(false);
                        brakeLightsSwitch.setChecked(false);
                        hazardLightsSwitch.setChecked(false);
                        doorLocksSwitch.setChecked(false);
                        windshieldSwitch.setChecked(false);
                        windowsSwitch.setChecked(false);
                        radioSwitch.setChecked(false);
                        hornSwitch.setChecked(false);
                        fireExtinguisherSwitch.setChecked(false);
                        firstAidSwitch.setChecked(false);
                        coolantSwitch.setChecked(false);
                        engineOilSwitch.setChecked(false);
                        transmissionOilSwitch.setChecked(false);
                        powerSteeringSwitch.setChecked(false);
                        brakeFluidSwitch.setChecked(false);
                        washerFluidSwitch.setChecked(false);
                        mEditTextBeltRemarks.getText().clear();
                        mEditTextHoseRemarks.getText().clear();
                        mEditTextOilLeakRemarks.getText().clear();
                        mEditTextAirconRemarks.getText().clear();
                        mEditTextWiperRemarks.getText().clear();
                        mEditTextHeadHiRemarks.getText().clear();
                        mEditTextHeadLoRemarks.getText().clear();
                        mEditTextDrivingLightsRemarks.getText().clear();
                        mEditTextTurnSignalRemarks.getText().clear();
                        mEditTextBrakeLightsRemarks.getText().clear();
                        mEditTextHazardLightsRemarks.getText().clear();
                        mEditTextDoorLocksRemarks.getText().clear();
                        mEditTextWindshieldRemarks.getText().clear();
                        mEditTextWindowsRemarks.getText().clear();
                        mEditTextRadioRemarks.getText().clear();
                        mEditTextHornRemarks.getText().clear();
                        mEditTextFireExtinguisherRemarks.getText().clear();
                        mEditTextFirstAidRemarks.getText().clear();
                        mEditTextCoolantRemarks.getText().clear();
                        mEditTextEngineOilRemarks.getText().clear();
                        mEditTextTransmissionOilRemarks.getText().clear();
                        mEditTextPowerSteeringRemarks.getText().clear();
                        mEditTextBrakeFluidRemarks.getText().clear();
                        mEditTextWasherFluidRemarks.getText().clear();
                        mEditTextLatitude.getText().clear();
                        mEditTextLongitude.getText().clear();
                        mEditTextImageURL.getText().clear();

                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        //  Action for 'NO' Button
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "Cancelled",
                                Toast.LENGTH_SHORT).show();
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Clear Asset Data");
            alert.show();
            return true;
        } else if (item.getItemId() == R.id.assetCreate_menu_helpButton) {
            Intent i = new Intent(this, AssetCreateHelpActivity.class);
            startActivity(i);
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void findLocation() {
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

        locationRequest.addOnSuccessListener(this, (OnSuccessListener<Location>) location -> {

            EditText latitude = findViewById(R.id.createVehicle_latitude);
            EditText longitude = findViewById(R.id.createVehicle_longitude);
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            String textLatitude = "" + lat;
            String textLongitude = "" + lon;
            latitude.setText(textLatitude);
            longitude.setText(textLongitude);

        });

    }

    private void createAsset(String pAssetCode,
                             String pSerialNumber,
                             String pCertificationDate,
                             String pRegistrationDate,
                             String pInsuranceExpiryDate,
                             String pExpiryDate,
                             String pVIN,
                             String pEngineNumber,
                             String pPlateNumber,
                             String pOwner,
                             String pMaker,
                             String pOdometer,
                             String pBeltString,
                             String pHoseString,
                             String pOilLeakString,
                             String pAirconString,
                             String pWiperString,
                             String pHeadHiString,
                             String pHeadLoString,
                             String pDrivingLightsString,
                             String pTurnSignalString,
                             String pBrakeLightsString,
                             String pHazardLightsString,
                             String pDoorLocksString,
                             String pWindshieldString,
                             String pWindowsString,
                             String pRadioString,
                             String pHornString,
                             String pFireExtinguisherString,
                             String pFirstAidString,
                             String pCoolantString,
                             String pEngineOilString,
                             String pTransmissionOilString,
                             String pPowerSteeringString,
                             String pBrakeFluidString,
                             String pWasherFluidString,
                             String pBeltRemarks,
                             String pHoseRemarks,
                             String pOilLeakRemarks,
                             String pAirconRemarks,
                             String pWiperRemarks,
                             String pHeadHiRemarks,
                             String pHeadLoRemarks,
                             String pDrivingLightsRemarks,
                             String pTurnSignalRemarks,
                             String pBrakeLightsRemarks,
                             String pHazardLightsRemarks,
                             String pDoorLocksRemarks,
                             String pWindshieldRemarks,
                             String pWindowsRemarks,
                             String pRadioRemarks,
                             String pHornRemarks,
                             String pFireExtinguisherRemarks,
                             String pFirstAidRemarks,
                             String pCoolantRemarks,
                             String pEngineOilRemarks,
                             String pTransmissionOilRemarks,
                             String pPowerSteeringRemarks,
                             String pBrakeFluidRemarks,
                             String pWasherFluidRemarks,
                             String pLatitude,
                             String pLongitude,
                             String pImageURL) {

        RequestQueue mQueue = Volley.newRequestQueue(this);
        String url = "https://www.lawtechthai.com/Assets/createVehicle.php";
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("equipmentID", pAssetCode.toUpperCase() + "-" + pSerialNumber.toUpperCase());
            parameter.put("assetCode", pAssetCode.toUpperCase());
            parameter.put("serialNumber", pSerialNumber.toUpperCase());
            parameter.put("certificationDate", pCertificationDate);
            parameter.put("expiryDate", pExpiryDate);
            parameter.put("registrationDate", pRegistrationDate);
            parameter.put("insuranceExpiryDate", pInsuranceExpiryDate);
            parameter.put("owner", pOwner);
            parameter.put("maker", pMaker);
            parameter.put("odometer", pOdometer);
            parameter.put("vin", pVIN);
            parameter.put("engineNumber", pEngineNumber);
            parameter.put("plateNumber", pPlateNumber);
            parameter.put("belt", pBeltString);
            parameter.put("hose", pHoseString);
            parameter.put("oilLeak", pOilLeakString);
            parameter.put("aircon", pAirconString);
            parameter.put("wiper", pWiperString);
            parameter.put("headHi", pHeadHiString);
            parameter.put("headLo", pHeadLoString);
            parameter.put("drivingLights", pDrivingLightsString);
            parameter.put("turnSignal", pTurnSignalString);
            parameter.put("brakeLights", pBrakeLightsString);
            parameter.put("hazardLights", pHazardLightsString);
            parameter.put("doorLocks", pDoorLocksString);
            parameter.put("windshield", pWindshieldString);
            parameter.put("windows", pWindowsString);
            parameter.put("radio", pRadioString);
            parameter.put("horn", pHornString);
            parameter.put("fireExtinguisher", pFireExtinguisherString);
            parameter.put("firstAid", pFirstAidString);
            parameter.put("coolant", pCoolantString);
            parameter.put("engineOil", pEngineOilString);
            parameter.put("transmissionOil", pTransmissionOilString);
            parameter.put("powerSteering", pPowerSteeringString);
            parameter.put("brakeFluid", pBrakeFluidString);
            parameter.put("washerFluid", pWasherFluidString);
            parameter.put("beltRemarks", pBeltRemarks);
            parameter.put("hoseRemarks", pHoseRemarks);
            parameter.put("oilLeakRemarks", pOilLeakRemarks);
            parameter.put("airconRemarks", pAirconRemarks);
            parameter.put("wiperRemarks", pWiperRemarks);
            parameter.put("headHiRemarks", pHeadHiRemarks);
            parameter.put("headLoRemarks", pHeadLoRemarks);
            parameter.put("drivingLightsRemarks", pDrivingLightsRemarks);
            parameter.put("turnSignalRemarks", pTurnSignalRemarks);
            parameter.put("brakeLightsRemarks", pBrakeLightsRemarks);
            parameter.put("hazardLightsRemarks", pHazardLightsRemarks);
            parameter.put("doorLocksRemarks", pDoorLocksRemarks);
            parameter.put("windshieldRemarks", pWindshieldRemarks);
            parameter.put("windowsRemarks", pWindowsRemarks);
            parameter.put("radioRemarks", pRadioRemarks);
            parameter.put("hornRemarks", pHornRemarks);
            parameter.put("fireExtinguisherRemarks", pFireExtinguisherRemarks);
            parameter.put("firstAidRemarks", pFirstAidRemarks);
            parameter.put("coolantRemarks", pCoolantRemarks);
            parameter.put("engineOilRemarks", pEngineOilRemarks);
            parameter.put("transmissionOilRemarks", pTransmissionOilRemarks);
            parameter.put("powerSteeringRemarks", pPowerSteeringRemarks);
            parameter.put("brakeFluidRemarks", pBrakeFluidRemarks);
            parameter.put("washerFluidRemarks", pWasherFluidRemarks);
            parameter.put("latitude", pLatitude);
            parameter.put("longitude", pLongitude);
            parameter.put("imageURL", pImageURL);
            parameter.put("inspectorName", inspectorName);
            parameter.put("certifyingCompany", certifyingCompany);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressBar circle = findViewById(R.id.createVehicle_progress);
        circle.setVisibility(View.VISIBLE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {
                    circle.setVisibility(View.GONE);
                    try {
                        boolean insertSuccess = response.getBoolean("success");
                        if (insertSuccess) {
                            Toast.makeText(this, "Asset Created", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this, "Could Not Insert Data", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_LONG).show();
                    }
                }, error -> {
            circle.setVisibility(View.GONE);
            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_LONG).show();
        });
        mQueue.add(request);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ownerText = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
