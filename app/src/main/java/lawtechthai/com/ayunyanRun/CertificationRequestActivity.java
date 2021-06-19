package lawtechthai.com.ayunyanRun;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.GridLayout;

public class CertificationRequestActivity extends AppCompatActivity {

    private String url;

    GridLayout mainGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification_request);
        mainGrid = (GridLayout) findViewById(R.id.mainGrid);
        setSingleEvent(mainGrid);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            url = extras.getString("url");
        }


    }

    private void setSingleEvent(GridLayout mainGrid) {

//Loop all child item of Main Grid
        for (int i = 0; i < mainGrid.getChildCount(); i++) {
            //You can see , all child item is CardView , so we just cast object to CardView
            CardView cardView = (CardView) mainGrid.getChildAt(i);
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String contactPerson;
                    String contactEmail;

                    switch (finalI) {

                        case 0:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 1:
                            contactPerson = "Jane Doe";
                            contactEmail = "hlagos1@gmail.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 2:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 3:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 4:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 5:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 6:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 7:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 8:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 9:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 10:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                        case 11:
                            contactPerson = "Jane Doe";
                            contactEmail = "jDoe@alert.com";
                            sendEmail(contactEmail, contactPerson);
                            break;
                    }
                }
            });
        }
    }


    private void sendEmail(String pContactEmail, String pContactPerson) {

        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Request for Certification");
        intent.putExtra(Intent.EXTRA_TEXT,"Dear K " + pContactPerson + ", \n\nPlease provide a quotation for the certification of the following equipment: \n\n" + url);
        intent.setData(Uri.parse("mailto: " + pContactEmail)); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        startActivity(intent);


    }


}
