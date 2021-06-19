package lawtechthai.com.ayunyanRun;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        setContentView(R.layout.activity_user_registration);

        EditText firstnameText = findViewById(R.id.userRegistration_firstname);
        EditText lastnameText = findViewById(R.id.userRegistration_lastname);
        EditText jobTitleText = findViewById(R.id.userRegistration_jobTitle);
        EditText companyText = findViewById(R.id.userRegistration_company);
        EditText emailText = findViewById(R.id.userRegistration_email);
        EditText passwordText = findViewById(R.id.userRegistration_password);
        EditText confirmPasswordText = findViewById(R.id.userRegistration_confirmPassword);
        RadioButton inspectorButton = findViewById(R.id.userRegistration_inspectorButton);
        RadioButton userButton = findViewById(R.id.userRegistration_userButton);
        Button registerButton = findViewById(R.id.userRegistration_registerButton);
        registerButton.setOnClickListener(v -> {

                    String firstname = firstnameText.getText().toString();
                    String lastname = lastnameText.getText().toString();
                    String jobTitle = jobTitleText.getText().toString();
                    String company = companyText.getText().toString();
                    String email = emailText.getText().toString();
                    String password = passwordText.getText().toString();
                    String confirmPassword = confirmPasswordText.getText().toString();
                    if (email.isEmpty()) {
                        Toast.makeText(UserRegistrationActivity.this, "Please Type Email Address", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (password.isEmpty()) {
                        Toast.makeText(UserRegistrationActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (confirmPassword.isEmpty() || !password.equals(confirmPassword)) {
                        Toast.makeText(UserRegistrationActivity.this, "Passwords " +
                                "Do Not Match", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!inspectorButton.isChecked() && !userButton.isChecked()) {
                        Toast.makeText(UserRegistrationActivity.this, "Please select user Type", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String userType = "Inspector";
                    if (userButton.isChecked()) {
                        userType = "User";
                    }

                    doRegistration(firstname, lastname, jobTitle, company, email, password, userType);
                }
        );

//                    SharedPreferences.Editor editor = prefs.edit();
//
//                    editor.putString("username", email);
//                    editor.putString("password", password);
//                    editor.putBoolean("certifier", certifierButton.isChecked());
//                    editor.apply();//this writes it to the phone, phone remembers data even on App exit//


//        String email = prefs.getString("email", "");
//        String password = prefs.getString("password", "");
//        boolean certifier = prefs.getBoolean("certifier", false);
//
//        emailText.setText(email);
//        passwordText.setText(password);
//        if (certifier) {
//            certifierButton.setChecked(true);
//        } else {
//            userButton.setChecked(true);
//        }


    }

    private void doRegistration(String firstname, String lastname, String jobTitle, String company, String username, String password, String userType) {

// ...


// Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://www.lawtechthai.com/userRegistration.php";
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("username", username);
            parameter.put("firstname", firstname);
            parameter.put("lastname", lastname);
            parameter.put("jobTitle", jobTitle);
            parameter.put("email", username);
            parameter.put("password", password);
            parameter.put("company", company);
            parameter.put("userType", userType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                response -> {

                    try {
                        boolean logInSuccess = response.getBoolean("success");
                        if (logInSuccess) {
                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(this, MainActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_SHORT).show();
                    }

                    // Display the first 500 characters of the response string.

                }, error -> Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show());

        queue.add(request);

// Request a string response from the provided URL.
//        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                response -> {
//                    // Display the first 500 characters of the response string.
//                    mTextView.setText("Response is: " + response);
//                }, error -> mTextView.setText("That didn't work!" + error));

// Add the request to the RequestQueue.
//        queue.add(stringRequest);
    }


}
