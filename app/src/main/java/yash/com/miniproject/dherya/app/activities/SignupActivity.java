package yash.com.miniproject.dherya.app.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.grocery.api.clients.RestClient;
import yash.com.miniproject.dherya.app.grocery.model.User;
import yash.com.miniproject.dherya.app.grocery.model.UserResult;
import yash.com.miniproject.dherya.app.grocery.util.CustomToast;
import yash.com.miniproject.dherya.app.grocery.util.Utils;
import yash.com.miniproject.dherya.app.grocery.util.localstorage.LocalStorage;
import yash.com.miniproject.dherya.database.Login_Register_DBHelper;

public class SignupActivity extends AppCompatActivity {

    public static final String CAME_FROM_WELCOME_ACTIVITY_INTENT = "CAME_FROM_WELCOME_ACTIVITY_INTENT";
    boolean mCameFromWelcomeScreen = false;

    private static EditText fullName, mobileNumber,
            password;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    User user;
    LocalStorage localStorage;
    Gson gson = new Gson();
    View progress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Check if intent comes with mCameFromWelcomeScreen = true;
        mCameFromWelcomeScreen = getIntent().getBooleanExtra(CAME_FROM_WELCOME_ACTIVITY_INTENT, true);

        fullName = findViewById(R.id.fullName);
        progress = findViewById(R.id.progress_bar);
//        emailId = view.findViewById(R.id.userEmailId);
        mobileNumber = findViewById(R.id.mobileNumber);

        password = findViewById(R.id.password);

        signUpButton = findViewById(R.id.signUpBtn);
        login = findViewById(R.id.already_user);
        terms_conditions = findViewById(R.id.terms_conditions);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

    }

    // Check Validation Method
    private void checkValidation() {
        // Get all edittext texts
        String getFullName = fullName.getText().toString();
//        String getEmailId = emailId.getText().toString();
        String getMobileNumber = mobileNumber.getText().toString();
        String getPassword = password.getText().toString();
        // Pattern match for email id
        Pattern p = Pattern.compile(Utils.regEx);


        if (getFullName.length() == 0) {
            fullName.setError("Eneter Your Name");
            fullName.requestFocus();
        }/* else if (getEmailId.length() == 0) {
            emailId.setError("Eneter Your Email");
            emailId.requestFocus();
        } else if (!m.find()) {
            emailId.setError("Eneter Correct Email");
            emailId.requestFocus();
        }*/ else if (getMobileNumber.length() == 0) {
            mobileNumber.setError("Eneter Your Mobile Number");
            mobileNumber.requestFocus();
        } else if (getPassword.length() == 0) {
            password.setError("Eneter Password");
            password.requestFocus();
        } else if (getPassword.length() < 6) {
            password.setError("Eneter 6 digit Password");
            password.requestFocus();
        } else {
            user = new User(getFullName, "", getMobileNumber, getPassword);
            registerUser(user);
            /*  gson = new Gson();
            String userString = gson.toJson(user);


            localStorage.createUserLoginSession(userString);
            progressDialog.setMessage("Registering Data....");
            progressDialog.show();
            Handler mHand = new Handler();
            mHand.postDelayed(new Runnable() {

                @Override
                public void run() {
                    progressDialog.dismiss();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }, 5000);*/
        }

    }

    private void registerUser(User userString) {
        showProgressDialog();
        Call<UserResult> call = RestClient.getRestService(SignupActivity.this).register(userString);
        call.enqueue(new Callback<UserResult>() {
            @Override
            public void onResponse(Call<UserResult> call, Response<UserResult> response) {
                Log.d("Response :=>", response.body() + "");
                if (response != null) {

                    UserResult userResult = response.body();
                    if (userResult.getCode() == 201) {
                        String userString = gson.toJson(userResult.getUser());
                        localStorage.createUserLoginSession(userString);
                        Toast.makeText(SignupActivity.this, userResult.getStatus(), Toast.LENGTH_LONG).show();
                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                        SignupActivity.this.finish();
                    }

                }

                hideProgressDialog();

            }

            @Override
            public void onFailure(Call<UserResult> call, Throwable t) {
                Log.d("Error==> ", t.getMessage());
                hideProgressDialog();
            }
        });
    }

    private void hideProgressDialog() {
        progress.setVisibility(View.GONE);
    }

    private void showProgressDialog() {
        progress.setVisibility(View.VISIBLE);
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