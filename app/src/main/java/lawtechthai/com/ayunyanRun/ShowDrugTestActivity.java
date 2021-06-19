package lawtechthai.com.ayunyanRun;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ShowDrugTestActivity extends AppCompatActivity {

    private RequestQueue mQueue;
    String assetCode;
    String serialNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drugtest_show);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            assetCode = extras.getString("assetCode");
            serialNumber = extras.getString("serialNumber");
        }

        mQueue = Volley.newRequestQueue(ShowDrugTestActivity.this);
        String url = "https://www.lawtechthai.com/Assets/huetSearch.php";
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("assetCode", assetCode.toUpperCase());
            parameter.put("serialNumber", serialNumber.toUpperCase());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        ProgressBar circle = findViewById(R.id.huetShow_progress);
        circle.setVisibility(View.VISIBLE);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {
                    circle.setVisibility(View.GONE);
                    try {

                        boolean searchSuccess = response.getBoolean("success");
                        String assetCodeResult = response.getString("assetCode");
                        String serialNumberResult = response.getString("serialNumber");
                        String firstNameResult = response.getString("firstName");
                        String lastNameResult = response.getString("lastName");
                        String companyResult = response.getString("company");
                        String trainingDateResult = response.getString("trainingDate");
                        String expiryDateResult = response.getString("expiryDate");
                        String trainingFacilityResult = response.getString("trainingFacility");
                        String trainerFirstNameResult = response.getString("trainerFirstName");
                        String trainerLastNameResult = response.getString("trainerLastName");
                        String imageURLResult = response.getString("imageURL");

                        TextView certificateCode = findViewById(R.id.huetShow_certificateCode);
                        TextView certificateNumber = findViewById(R.id.huetShow_certificateNumber);
                        TextView firstName = findViewById(R.id.huetShow_firstName);
                        TextView lastName = findViewById(R.id.huetShow_lastName);
                        TextView company = findViewById(R.id.huetShow_company);
                        TextView trainingDate = findViewById(R.id.huetShow_trainingDate);
                        TextView expiryDate = findViewById(R.id.huetShow_expiryDate);
                        TextView trainingFacility = findViewById(R.id.huetShow_trainingFacility);
                        TextView trainerFirstName = findViewById(R.id.huetShow_trainerFirstName);
                        TextView trainerLastName = findViewById(R.id.huetShow_trainerLastName);
                        TextView imageURL = findViewById(R.id.huetShow_imageURL);

                        if (searchSuccess) {

                            certificateCode.setText(assetCodeResult);
                            certificateNumber.setText(serialNumberResult);
                            firstName.setText(firstNameResult);
                            lastName.setText(lastNameResult);
                            company.setText(companyResult);
                            trainingDate.setText(trainingDateResult);
                            expiryDate.setText(expiryDateResult);
                            trainingFacility.setText(trainingFacilityResult);
                            trainerFirstName.setText(trainerFirstNameResult);
                            trainerLastName.setText(trainerLastNameResult);
                            imageURL.setText(imageURLResult);

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
}





