package yash.com.miniproject.dherya.app.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;



import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.fragments.AppPreferenceFragment;


public class AppPreferenceActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_preference);

        setUpToolbar();

        if (savedInstanceState == null) {
            AppPreferenceFragment fragment = new AppPreferenceFragment();

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.activity_preference_fragment, fragment);
            ft.commit();
        }
    }

    private void setUpToolbar() {

        mToolbar = (Toolbar) findViewById(R.id.activity_preference_toolbar);
        mToolbar.setTitle(getResources().getString(R.string.fragment_name_preferences));

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
