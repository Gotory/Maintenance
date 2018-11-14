package com.dmt_winches.maintenance.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dmt_winches.maintenance.Common.HttpRequest;
import com.dmt_winches.maintenance.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {


    // UI references.
    private EditText mUserView;
    private EditText mPasswordView;
    private SharedPreferences loginPreferences;
    private CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserView = findViewById(R.id.user);
        mPasswordView = findViewById(R.id.password);
        rememberMe = findViewById(R.id.rememberMeCB);
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if (loginPreferences.getBoolean("saveLogin", false)) {
            mUserView.setText(loginPreferences.getString("username", ""));
            mPasswordView.setText(loginPreferences.getString("password", ""));
            rememberMe.setChecked(true);
        }

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        findViewById(R.id.loginLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }
        });
    }

    private void attemptLogin() {

        // Reset errors.
        mUserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String userName = mUserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(userName)) {
            mUserView.setError(getString(R.string.error_field_required));
            focusView = mUserView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Perform the user login attempt.
            UserLoginTask mAuthTask = new UserLoginTask(userName, password);
            mAuthTask.execute((Void) null);
        }
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUser;
        private final String mPassword;
        private String userType;
        private String message;

        UserLoginTask(String userName, String password) {
            mUser = userName;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            HashMap<String, String> postParams = new HashMap<>();

            postParams.put("type", "login");
            postParams.put("username", mUser);
            postParams.put("password", mPassword);

            String response = new HttpRequest(postParams).connect();

            if (response.equals("Connection error")) {
                message = "Connection error";
                return false;
            }

            try {
                JSONObject json = new JSONObject(response);

                String success = json.getString("success");

                if (success.equals("1")) {
                    userType = json.getString("user_type");
                    String userId = json.getString("user_id");
                    String userName = json.getString("user_name");
                    if (rememberMe.isChecked()) {
                        loginPreferences.edit().putString("username", mUser)
                        .putBoolean("saveLogin", true)
                        .putString("password", mPassword)
                        .putString("userId", userId)
                        .putString("userName", userName)
                        .apply();
                    } else {
                        loginPreferences.edit().clear()
                        .apply();
                    }

                } else {
                    message = json.getString("message");
                    return false;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Intent login = new Intent(LoginActivity.this, TaskView.class);
                login.putExtra("userType", userType);
                LoginActivity.this.startActivity(login);
            } else {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}

