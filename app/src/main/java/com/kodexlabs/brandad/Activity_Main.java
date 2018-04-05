package com.kodexlabs.brandad;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.Map;

public class Activity_Main extends AppCompatActivity {

    private TextView idFacebook, name, email, gender, birthday, location;
    private ProfilePictureView profilePictureView;

    private String get_idFacebook, get_name, get_email, get_gender, get_birthday, get_location;

    static String DATA_URL = "http://kiitecell.hol.es/BrandAd_User_Download.php";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                Activity_Login.facebook_logout();
                Intent intent = new Intent(getBaseContext(), Activity_Login.class);
                startActivity(intent);
                finish();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent bundle = getIntent();

        get_idFacebook = bundle.getStringExtra("idFacebook");
        getData(get_idFacebook);

        idFacebook = (TextView)findViewById(R.id.fb_idFacebook);
        name = (TextView)findViewById(R.id.fb_name);
        email = (TextView)findViewById(R.id.fb_email);
        gender = (TextView)findViewById(R.id.fb_gender);
        birthday = (TextView)findViewById(R.id.fb_birthday);
        location = (TextView)findViewById(R.id.fb_location);

        idFacebook.setText("Id : " + get_idFacebook);
        profilePictureView = (ProfilePictureView)findViewById(R.id.picture);
        profilePictureView.setProfileId(get_idFacebook);

        /*ShareDialog shareDialog = new ShareDialog(this);
        ShareLinkContent content = new ShareLinkContent.Builder().build();
        shareDialog.shower(content);*/
    }

    private void getData(String idFacebook) {
        String url = DATA_URL + "?idFacebook=" + idFacebook;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject get_data = result.getJSONObject(0);

            get_name = get_data.getString("name");
            get_email = get_data.getString("email");
            get_gender = get_data.getString("gender");
            get_birthday = get_data.getString("birthday");

            name.setText("Name : " + get_name);
            email.setText("Email : " + get_email);
            gender.setText("Gender : " + get_gender);
            birthday.setText("Birthday : " + get_birthday);

        } catch (JSONException e) {
        }
    }
}
