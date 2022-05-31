package yash.com.miniproject.dherya.app.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import yash.com.miniproject.dherya.R;

/**
 * Created by Alex on 26/8/2016.
 */
public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    Button addCCButton;
    Button login;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Change the status bar color!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.welcome_activity_status_bar));
            window.setNavigationBarColor(ContextCompat.getColor(getApplicationContext(), R.color.welcome_activity_status_bar));
        }

        addCCButton = (Button) findViewById(R.id.activity_welcome_register_account);
        addCCButton.setOnClickListener(this);



        login = (Button) findViewById(R.id.activity_welcome_login_button);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.activity_welcome_register_account:
                Intent registerIntent = new Intent(WelcomeActivity.this, SignupActivity.class);
                registerIntent.putExtra(SignupActivity.CAME_FROM_WELCOME_ACTIVITY_INTENT, true);
                startActivity(registerIntent);
                finish();
                break;


            case R.id.activity_welcome_login_button:
                Intent loginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;
        }
    }
}
