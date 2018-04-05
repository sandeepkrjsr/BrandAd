package com.kodexlabs.brandad;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 1505560 on 17-Jun-17.
 */

public class Activity_Login extends Activity {

    private LoginButton loginButton;

    private String idFacebook, name, email, gender, birthday, location;

    private CallbackManager callbackManager;

    private static SharedPreferences preferences;
    private String prefName = "MyPref";
    private static final String UID = "UID";

    static String DataParseUrl = "http://kiitecell.hol.es/BrandAd_User_Upload.php";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.loginButton);

        preferences = getSharedPreferences(prefName, MODE_PRIVATE);
        String loggedin = preferences.getString(UID, "UID");

        if (loggedin.equals("UID"))
            facebook_login();
        else
            nextActivity(loggedin);
    }

    private void facebook_login() {
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        getFacebookData(object);
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
            }
            @Override
            public void onError(FacebookException exception) {
            }
        });
    }

    private void getFacebookData(JSONObject object) {
        try {
            if(object.has("id"))
                idFacebook = object.getString("id");
            if(object.has("name"))
                name = object.getString("name");
            if(object.has("email"))
                email = object.getString("email");
            if(object.has("gender"))
                gender = object.getString("gender");
            if(object.has("birthday"))
                birthday = object.getString("birthday");
            //bundle.putString("location", object.getJSONObject("location").getString("name"));

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(UID, idFacebook);
            editor.commit();

            SendDataToServer(idFacebook, name, email, gender, birthday);
            nextActivity(idFacebook);

        } catch (JSONException e) {}
    }

    private void SendDataToServer(final String idFacebook, final String name, final String email, final String gender, final String birthday){
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                List<NameValuePair> data = new ArrayList<NameValuePair>();
                data.add(new BasicNameValuePair("idFacebook", idFacebook));
                data.add(new BasicNameValuePair("name", name));
                data.add(new BasicNameValuePair("email", email));
                data.add(new BasicNameValuePair("gender", gender));
                data.add(new BasicNameValuePair("birthday", birthday));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(DataParseUrl);
                    httpPost.setEntity(new UrlEncodedFormEntity(data));
                    HttpResponse response = httpClient.execute(httpPost);
                    HttpEntity entity = response.getEntity();

                } catch (ClientProtocolException e) {
                } catch (IOException e) {
                }
                return "Data Submit Successfully";
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(idFacebook, name, email, gender, birthday);
    }

    private void nextActivity(String bundle){
        if(bundle != null){
            Intent intent = new Intent(getBaseContext(), Activity_Main.class);
            intent.putExtra("idFacebook", bundle);
            startActivity(intent);
            finish();
        }
    }

    static void facebook_logout(){
        LoginManager.getInstance().logOut();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UID, "UID");
        editor.commit();
    }
}
