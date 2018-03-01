package com.cathedralsw.schoolparent;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cathedralsw.schoolparent.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;


public class LoginActivity extends AppCompatActivity {
    private EditText _emailText;
    private EditText _passwordText;
    private Button _loginButton;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    public void login() {

        if (!validate()) {
            onLoginFailed(getResources().getString(R.string.login_error));
            return;
        }

        _loginButton.setEnabled(false);

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", email);
            postData.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new loginTask(postData).execute();

    }


    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onLoginFailed(String msg) {
        _loginButton.setEnabled(true);
        Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();


    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError(getResources().getString(R.string.email_error));
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordText.setError(getResources().getString(R.string.password_error));
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    public void saveSharedPreferencesLogin(JSONObject response) {
        try {
            String email = response.getString("email");
            String token = response.getString("token");
            String first = response.getString("first_name");
            String last = response.getString("last_name");
            Integer id = response.getInt("id");
            SharedPreferences sharedPref = getSharedPreferences("creds", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("email", email);
            editor.putString("token", token);
            editor.putString("first_name", first);
            editor.putString("last_name", last);
            editor.putInt("userId", id);
            editor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class loginTask extends AsyncTask<Void, Void, JSONObject> {

        JSONObject postData;

        public loginTask(JSONObject postData) {
            if (postData != null) {
                this.postData = postData;
            }
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            JSONObject response = null;
            JSONObject user = null;
            try {
                response = NetworkUtils.schoolAuth(postData);
                if (response != null && !response.has("error")) {
                    user = NetworkUtils.schoolUser(response);
                    response.put("first_name", user.getString("first_name"));
                    response.put("last_name", user.getString("last_name"));
                    response.put("id", user.getInt("id"));
                    JSONArray groups = user.getJSONArray("groups");
                    Boolean isParent = false;
                    for (int i = 0; i < groups.length(); i++) {
                        JSONObject group = (JSONObject) groups.get(i);
                        if (group.getString("name").equals("Parent")) { //Teacher
                            isParent = true;
                            break;
                        }
                    }
                    if (!isParent)
                        response.put("not_parent", "is not parent");
                }
            } catch (ConnectException e) {
                try {
                    response = new JSONObject();
                    response.put("server_error", "server_error");
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            progressBar.setVisibility(View.INVISIBLE);

            if (result != null) {
                if (result.has("not_parent"))
                    onLoginFailed(getResources().getString(R.string.profile_error));
                else if (!result.has("error") && !result.has("server_error")) {
                    saveSharedPreferencesLogin(result);
                    onLoginSuccess();
                } else {
                    if (result.has("error"))
                        onLoginFailed(getResources().getString(R.string.login_error));
                    if (result.has("server_error"))
                        onLoginFailed(getResources().getString(R.string.server_error));
                }
            }

        }
    }
}
