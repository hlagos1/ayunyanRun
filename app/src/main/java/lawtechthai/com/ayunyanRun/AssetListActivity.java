package lawtechthai.com.ayunyanRun;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AssetListActivity extends AppCompatActivity {
    private static final String HI = "https://www.lawtechthai.com/Assets/assetListShow.php";
    private List<List_Data> list_data;
    private RecyclerView rv;
    private MyAdapter adapter;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_list);
        rv = (RecyclerView) findViewById(R.id.recyclerview);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        list_data = new ArrayList<>();
        adapter = new MyAdapter(list_data);

        getAssetList();

    }

    private void getAssetList() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, HI, response -> {
            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray assetArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < assetArray.length(); i++) {
                    JSONObject ob = assetArray.getJSONObject(i);
                    List_Data listData = new List_Data(ob.getString("equipmentID"), ob.getString("certification"));
                    list_data.add(listData);

                }
                rv.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {

        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.assetlist_menu, menu);

        menu.getItem(0).setVisible(true);
        menu.getItem(1).setVisible(true);
        menu.getItem(2).setVisible(true);
        menu.getItem(3).setVisible(true);
        menu.getItem(4).setVisible(true);
        menu.getItem(5).setVisible(true);
        menu.getItem(6).setVisible(false);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.assetListMenu_clearButton) {

            builder = new AlertDialog.Builder(this);

            //Uncomment the below code to Set the message and title from the strings.xml file
            builder.setMessage(R.string.dialog_message).setTitle(R.string.dialog_title);

            //Setting message manually and performing action on button click
            builder.setMessage("Cannot Be Undone Please Confirm")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {

                        RequestQueue queue = Volley.newRequestQueue(this);
                        String url = "https://www.lawtechthai.com/Assets/assetListClear.php";
                        JSONObject parameter = new JSONObject();

                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, parameter,
                                response -> {
                                    try {
                                        boolean insertSuccess = response.getBoolean("success");
                                        if (insertSuccess) {
                                            Toast toast = Toast.makeText(this, "Asset List Cleared", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 10);
                                            toast.show();
                                            Intent i = new Intent(this, MainActivity.class);
                                            startActivity(i);

                                        } else {
                                            Toast.makeText(this, "Could Not Insert Data", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(this, "Unexpected Response from Server", Toast.LENGTH_SHORT).show();
                                    }

                                }, error -> {
                            Toast.makeText(this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                        });

                        queue.add(request);
                        finish();

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
            alert.setTitle("Clear Asset List");
            alert.show();

        } else if (item.getItemId() == R.id.assetListMenu_recertifyButton) {
            Intent i = new Intent(this, CertificationRequestActivity.class);
            i.putExtra("url", "https://www.lawtechthai.com/Assets/assetListShow.php");
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.assetListMenu_searchButton) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.assetListMenu_doneButton) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.assetListMenu_showExpiredButton) {
            Intent i = new Intent(this, AssetListExpiredActivity.class);
            startActivity(i);
            return true;
        } else if (item.getItemId() == R.id.assetListMenu_showValidButton) {
            Intent i = new Intent(this, AssetListValidActivity.class);
            startActivity(i);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

}
