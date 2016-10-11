package sngcet.com.safeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;


public class MainActivity extends Activity {
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);

//        loginButton.setPublishPermissions("publish_actions");
//        loginButton.setReadPermissions("public_profile");


        if (isLoggedIn()) {
//            new GraphRequest(
//                    AccessToken.getCurrentAccessToken(),
//                    "/100000346110865/photos", bundle,
//                    HttpMethod.POST,
//                    new GraphRequest.Callback() {
//                        public void onCompleted(GraphResponse response) {
//            /* handle the result */
//                            Log.i("<<FB=====>>", response.toString());
//
//                        }
//                    }
//            ).executeAsync();
        }
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                AccessToken.getCurrentAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code

                                        Log.i("TOKEN FB----", object.toString());
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link");
                        request.setParameters(parameters);
                        request.executeAsync();


                    }

                    @Override
                    public void onCancel() {
                        Log.i("dskjksdksdjk", "dsdlskdlskdl cancellllllllllll");

                    }

                    @Override
                    public void onError(FacebookException e) {
                        Log.i("dskjksdksdjk", "dsdlskdlskdl  errorrrrrrrrrrrrrrrrrrrrrr");

                    }
                }

        );


        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.toggleButton);

        //        Context context = getBaseContext();
        final SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String active = sharedPref.getString("active", "OFF");

        if (active.equals("ON"))
            toggleButton.setChecked(true);

        toggleButton.setOnClickListener(new View.OnClickListener()

                                        {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(view.getContext(), BackgroundService.class);
                                                sharedPref.edit().putString("active", ((ToggleButton) view).getText().toString()).apply();
                                                if (((ToggleButton) view).getText().equals("ON")) {
                                                    startService(intent);
                                                } else {
                                                    stopService(intent);
                                                }
                                            }
                                        }

        );
    }

    public boolean isLoggedIn() {
        boolean loggedIn = false;
        if (AccessToken.getCurrentAccessToken() != null)
            loggedIn = true;
        return loggedIn;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void profile(View v) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void settings(View v) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
