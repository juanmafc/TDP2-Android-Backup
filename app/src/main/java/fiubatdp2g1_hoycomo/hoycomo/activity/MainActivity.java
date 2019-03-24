package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabaseParser;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HttpRequester;
import fiubatdp2g1_hoycomo.hoycomo.service.firebase.HoyComoFirebaseIDService;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;

    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private TextView userBannedTextView;
    private AccessTokenTracker facebookAccessTokenTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       Log.e("FIREBASE", HoyComoFirebaseIDService.GetFirebaseToken());

        HttpRequester.Initialize(this);
        this.facebookLoginButton = this.findViewById(R.id.login_button);

        this.userBannedTextView = this.findViewById(R.id.user_banned_textview);
        this.userBannedTextView.setVisibility(View.INVISIBLE);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        //If the current Access Token is null then the user is not loged in into facebook
        final AccessToken currentToken = AccessToken.getCurrentAccessToken();
        if (currentToken != null){
            GraphRequest request = GraphRequest.newMeRequest(
                    currentToken,
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(JSONObject object, GraphResponse response) {
                            Log.v("LoginActivity", response.toString());
                            // Application code
                            try {
                                String name = object.getString("name");
                                MainActivity.this.loginUser(currentToken, name);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "name");
            request.setParameters(parameters);
            request.executeAsync();
        }
        else {
            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(final LoginResult loginResult) {


                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            Log.v("LoginActivity", response.toString());
                                            // Application code
                                            try {
                                                String name = object.getString("name");
                                                MainActivity.this.loginUser(loginResult.getAccessToken(), name);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "name");
                            request.setParameters(parameters);
                            request.executeAsync();
                        }

                        @Override
                        public void onCancel() {
                            Toast.makeText(MainActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Toast.makeText(MainActivity.this, "Login Error", Toast.LENGTH_LONG).show();
                        }
                    });


        }



        this.facebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if (currentAccessToken == null ){
                    //User has logged out
                    MainActivity.this.userBannedTextView.setVisibility(View.INVISIBLE);
                }
            }
        };
    }

    private void loginUser(final AccessToken accessToken, final String name) {
        //Hide Facebook login button
        String firebaseToken = HoyComoFirebaseIDService.GetFirebaseToken();
        HoyComoUserProfile.setFirebaseToken( firebaseToken );
        HoyComoDatabase.loginUser(accessToken.getUserId(), accessToken.getToken(), firebaseToken, name, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                MainActivity.this.saveLoginInformation(accessToken.getUserId(), name, response);

                HoyComoDatabase.getUserInformation(HoyComoUserProfile.getUserId(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if ( response.getBoolean("active") ) {
                                MainActivity.this.facebookLoginButton.setVisibility(View.INVISIBLE);
                                if ( response.isNull("address") ) {
                                    HoyComoUserProfile.setMyAddress( null );
                                    HoyComoUserProfile.useMyCurrentAddress();
                                }
                                else {
                                    JSONObject addressJsonObject = response.getJSONObject("address");
                                    HoyComoUserProfile.setMyAddress( HoyComoDatabaseParser.ParseAddress( addressJsonObject ) );
                                }
                                MainActivity.this.openMenu(null);
                            }
                            else {
                                MainActivity.this.userBannedTextView.setVisibility(View.VISIBLE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });



    }

    private void saveLoginInformation(String facebookId, String name, JSONObject response) {
        try {
            JSONObject tokenJsonObject = response.getJSONObject("token");
            HoyComoUserProfile.setHoyComoServerToken(tokenJsonObject.getString("token"));
            HoyComoUserProfile.setUserFacebookId( facebookId );
            HoyComoUserProfile.setUserName(name);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void openMenu(View view) {
        Intent myIntent = new Intent(this, NavigationMenuActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        this.startActivity(myIntent);
    }

}
