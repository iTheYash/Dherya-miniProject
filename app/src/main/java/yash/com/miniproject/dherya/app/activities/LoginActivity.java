package yash.com.miniproject.dherya.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.grocery.api.clients.RestClient;
import yash.com.miniproject.dherya.app.grocery.model.User;
import yash.com.miniproject.dherya.app.grocery.model.UserResult;
import yash.com.miniproject.dherya.app.grocery.util.CustomToast;
import yash.com.miniproject.dherya.app.grocery.util.localstorage.LocalStorage;
import yash.com.miniproject.dherya.database.Login_Register_DBHelper;

public class LoginActivity extends AppCompatActivity {

    private static EditText mobile, password;
    private static Button loginButton;
    private static TextView forgotPassword, signUp;
    private static CheckBox show_hide_password;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static FragmentManager fragmentManager;
    Gson gson = new Gson();
    View progress;
    LocalStorage localStorage;
    String userString;
    User user;

    public static final String CAME_FROM_WELCOME_ACTIVITY_INTENT = "CAME_FROM_WELCOME_ACTIVITY_INTENT";
    boolean mCameFromWelcomeScreen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mobile = findViewById(R.id.login_mobile);
        password = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.loginBtn);
        forgotPassword = findViewById(R.id.forgot_password);
        signUp = findViewById(R.id.createAccount);
        show_hide_password = findViewById(R.id.show_hide_password);
        loginLayout = findViewById(R.id.login_layout);

        localStorage = new LocalStorage(LoginActivity.this);
        String userString = localStorage.getUserLogin();
        Gson gson = new Gson();
        userString = localStorage.getUserLogin();
        user = gson.fromJson(userString, User.class);
        Log.d("User", userString);

        // Load ShakeAnimation
        shakeAnimation = AnimationUtils.loadAnimation(LoginActivity.this,
                R.anim.shake);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });

        show_hide_password
                .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton button,
                                                 boolean isChecked) {

                        // If it is checkec then show password else hide
                        // password
                        if (isChecked) {

                            show_hide_password.setText(R.string.hide_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT);
                            password.setTransformationMethod(HideReturnsTransformationMethod
                                    .getInstance());// show password
                        } else {
                            show_hide_password.setText(R.string.show_pwd);// change
                            // checkbox
                            // text

                            password.setInputType(InputType.TYPE_CLASS_TEXT
                                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            password.setTransformationMethod(PasswordTransformationMethod
                                    .getInstance());// hide password

                        }

                    }
                });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

    }

    // Check Validation before login
    private void checkValidation() {
        // Get email id and password
        final String getMobile = mobile.getText().toString();
        final String getPassword = password.getText().toString();


        // Check for both field is empty or not
        if (getMobile.equals("") || getMobile.length() == 0
                || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            vibrate(200);
        } else {
            user = new User(getMobile, getPassword);
            login(user);
        }
    }

    private void login(User user) {
        Call<UserResult> call = RestClient.getRestService(LoginActivity.this).login(user);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {

                Log.d("Response :=>", response.body() + "");
                if (response != null) {

                    UserResult userResult = response.body();
                    if (userResult.getCode() == 200) {
                        String userString = gson.toJson(userResult.getUser());
                        localStorage.createUserLoginSession(userString);
                        Toast.makeText(LoginActivity.this, userResult.getStatus(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(LoginActivity.this, AddCreditCardActivity.class));
                        LoginActivity.this.finish();
                    }

                }

            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                Log.d("Error==> ", t.getMessage());
            }
        });
    }

    public void vibrate(int duration) {
        Vibrator vibs = (Vibrator) LoginActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        vibs.vibrate(duration);
    }



    @Override
    public void onBackPressed() {
        if(mCameFromWelcomeScreen) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(getResources().getString(R.string.activity_add_new_cc_exit_dialog_title))
                    .setMessage(getResources().getString(R.string.activity_add_new_cc_exit_dialog_message))
                    .setPositiveButton(getResources().getString(R.string.activity_add_new_cc_exit_dialog_button_exit),  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.activity_add_new_cc_exit_dialog_button_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();
            dialog.show();
        }
        else
            super.onBackPressed();
    }
}