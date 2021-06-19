package lawtechthai.com.ayunyanRun;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class CreateMedicalActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView mTextViewCertificationDate;
    private TextView mTextViewExpiryDate;
    private DatePickerDialog certDatePickerDialog;
    private DatePickerDialog expiryDatePickerDialog;
    private int year;
    private int month;
    private int dayOfMonth;
    private Calendar calendar;
    private String inspectorName;
    private String certifyingCompany;
    private String employerText;
    private RadioButton fitButton;
    private RadioButton unfitButton;
    private RadioButton tempFitButton;
    private RadioButton tempUnfitButton;
    private RadioButton noHepaButton;
    private RadioButton hepaButton;
    private RadioButton followUpButton;
    private RadioButton asapButton;
    private RadioButton naButton;
    private String medicalStatus;
    private String hepaB;
    private String followUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_medical);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            inspectorName = extras.getString("inspector");
            certifyingCompany = extras.getString("certifyingCompany");
        }

        fitButton = findViewById(R.id.createMedical_fitButton);
        unfitButton = findViewById(R.id.createMedical_unfitButton);
        tempFitButton = findViewById(R.id.createMedical_tempFitButton);
        tempUnfitButton = findViewById(R.id.createMedical_tempUnfitButton);
        noHepaButton = findViewById(R.id.createMedical_noHepaButton);
        hepaButton = findViewById(R.id.createMedical_hepaButton);
        followUpButton = findViewById(R.id.createMedical_followUpButton);
        asapButton = findViewById(R.id.createMedical_asapButton);
        naButton = findViewById(R.id.createMedical_naButton);
        TextView certifiedByTextView = findViewById(R.id.createMedical_certifiedByText);
        certifiedByTextView.setText(inspectorName);
        ImageView companyLogo = findViewById(R.id.createMedical_certifyingCompanyLogo);
        switch (certifyingCompany) {

            case "Bangkok Hospital":
                companyLogo.setBackgroundResource(R.drawable.bhb_logo);
                break;
            case "Bangkok Hospital Hatyai":
                companyLogo.setBackgroundResource(R.drawable.bhh_logo);
                break;

        }

        Button selectCertificationDate = findViewById(R.id.createMedical_certificationDateButton);
        mTextViewCertificationDate = findViewById(R.id.createMedical_certificationDate);

        selectCertificationDate.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            certDatePickerDialog = new DatePickerDialog(this,
                    (datePicker, year, month, day) -> mTextViewCertificationDate.setText(day + "/" + (month + 1) + "/" + year), year, month, dayOfMonth);
            certDatePickerDialog.show();
        });

        Button selectExpiryDate = findViewById(R.id.createMedical_expiryDateButton);
        mTextViewExpiryDate = findViewById(R.id.createMedical_expiryDate);

        selectExpiryDate.setOnClickListener(view -> {
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            expiryDatePickerDialog = new DatePickerDialog(this,
                    (datePicker, year, month, day) -> mTextViewExpiryDate.setText(day + "/" + (month + 1) + "/" + year), year, month, dayOfMonth);
            expiryDatePickerDialog.show();
        });

        Spinner spinner = findViewById(R.id.createMedical_companySpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ownerCompanies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.assetcreate_menu, menu);

        menu.getItem(0).setEnabled(true);
        menu.getItem(1).setEnabled(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setEnabled(true);
        menu.getItem(3).setEnabled(true);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        EditText mLastnameEditText = findViewById(R.id.createMedical_lastName);
        EditText mFirstnameEditText = findViewById(R.id.createMedical_firstName);
        EditText mImageURLEditText = findViewById(R.id.createMedical_imageURL);
        EditText mRemarksEditText = findViewById(R.id.createMedical_remarks);

        Vibrator vibrator;
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        if (item.getItemId() == R.id.assetCreate_menu_createButton) {

            String lastnameText = mLastnameEditText.getText().toString();
            String firstnameText = mFirstnameEditText.getText().toString();
            String certificationDateText = mTextViewCertificationDate.getText().toString();
            String expiryDateText = mTextViewExpiryDate.getText().toString();
            String imageURLText = mImageURLEditText.getText().toString();
            String remarksText = mRemarksEditText.getText().toString();

            if (lastnameText.isEmpty()) {
                Toast.makeText(this, "Please Enter Patient Lastname", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (firstnameText.isEmpty()) {
                Toast.makeText(this, "Please Enter Patient Firstname", Toast.LENGTH_LONG).show();
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
            if (this.employerText.isEmpty() || this.employerText.equals("-select-")) {
                Toast.makeText(this, "Please Select Patient Employer", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= 26) {
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    vibrator.vibrate(200);
                }
                return false;
            }
            if (!fitButton.isChecked() && !unfitButton.isChecked() && !tempFitButton.isChecked() && !tempUnfitButton.isChecked()) {
                Toast.makeText(this, "Please Select Medical Status of Patient", Toast.LENGTH_SHORT).show();
                return false;
            }
                if (fitButton.isChecked()) {
                    medicalStatus = "Fit to work off-shore";
                }
                if (unfitButton.isChecked()) {
                    medicalStatus = "Unfit to work off-shore";
                }
                if (tempFitButton.isChecked()) {
                    medicalStatus = "Temporary Fit to work off-shore";
                }
                if (tempUnfitButton.isChecked()) {
                    medicalStatus = "Temporary Unfit to work off-shore";
                }


            if (!hepaButton.isChecked() && !noHepaButton.isChecked()) {
                Toast.makeText(this, "Please Select Hepa B Antibody Status of Patient", Toast.LENGTH_SHORT).show();
                return false;
            }

                if (noHepaButton.isChecked()) {
                    hepaB = "No antibodies for Hepatitis B, vaccination strongly recommended";
                }
                if (hepaButton.isChecked()) {
                    hepaB = "No antibodies for Hepatitis B, vaccination strongly recommended";
                }

            if (!followUpButton.isChecked() && !asapButton.isChecked() && !naButton.isChecked()) {
                Toast.makeText(this, "Please Select Follow Up Requirement of Patient", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (followUpButton.isChecked()) {
                followUp = "Required (See Next Checkup Date)";
            }
            if (asapButton.isChecked()) {
                followUp = "ASAP";
            }
            if (naButton.isChecked()) {
                followUp = "N/A";
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

            createAsset(lastnameText,
                        firstnameText,
                        certificationDateText,
                        expiryDateText,
                        this.employerText,
                        medicalStatus,
                        hepaB,
                        followUp,
                        imageURLText,
                        remarksText);
            return true;

        } else if (item.getItemId() == R.id.assetCreate_menu_cancelButton) {
            Intent i = new Intent(this, MainActivity.class);//have to fix later
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.assetCreate_menu_helpButton) {
            Intent i = new Intent(this, AssetCreateHelpActivity.class);
            startActivity(i);
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void createAsset(String pLastname,
                             String pFirstname,
                             String pCertificationDate,
                             String pExpiryDate,
                             String pEmployer,
                             String pMedicalStatus,
                             String pHepaB,
                             String pFollowUp,
                             String pImageURL,
                             String pRemarks) {

        RequestQueue mQueue = Volley.newRequestQueue(this);
        String url = "https://www.lawtechthai.com/Assets/createMedical.php";
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("fullname", pFirstname.toUpperCase() + " " + pLastname.toUpperCase());
            parameter.put("lastname", pLastname.toUpperCase());
            parameter.put("firstname", pFirstname.toUpperCase());
            parameter.put("checkupDate", pCertificationDate);
            parameter.put("nextCheckupDate", pExpiryDate);
            parameter.put("employer", pEmployer);
            parameter.put("medicalStatus", pMedicalStatus);
            parameter.put("hepaB", pHepaB);
            parameter.put("followUp", pFollowUp);
            parameter.put("imageURL", pImageURL);
            parameter.put("remarks", pRemarks);
            parameter.put("doctor", inspectorName);
            parameter.put("hospital", certifyingCompany);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressBar circle = findViewById(R.id.createMedical_progress);
        circle.setVisibility(View.VISIBLE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {
                    circle.setVisibility(View.GONE);
                    try {
                        boolean insertSuccess = response.getBoolean("success");
                        if (insertSuccess) {
                            Toast.makeText(this, "Medical Record Created", Toast.LENGTH_LONG).show();
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
        employerText = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
