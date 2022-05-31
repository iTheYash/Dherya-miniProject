package yash.com.miniproject.dherya.app.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;

import yash.com.miniproject.dherya.app.utils.Constants;
import yash.com.miniproject.dherya.app.utils.SharedPreferencesUtils;
import yash.com.miniproject.dherya.database.ExpenseManagerDAO;
import yash.com.miniproject.dherya.exceptions.CreditCardNotFoundException;
import yash.com.miniproject.dherya.exceptions.SharedPreferenceNotFoundException;

/**
 * Created by Alex on 26/8/2016.
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExpenseManagerDAO mDao = new ExpenseManagerDAO(getApplicationContext());

        //Check if ACTIVE_CC_ID exists and it is valid
        try {
            int activeCreditCardId = SharedPreferencesUtils.getInt(getApplicationContext(), Constants.ACTIVE_CC_ID);
            mDao.getCreditCard(activeCreditCardId);
        }catch(SharedPreferenceNotFoundException | CreditCardNotFoundException e) {

            //if there's at least one CreditCard in database, set it's id to ACTIVE_CC_ID
            if(mDao.getCreditCardList().size() > 0) {
                SharedPreferencesUtils.setInt(getApplicationContext(), Constants.ACTIVE_CC_ID,  mDao.getCreditCardList().get(0).getId());
            } else {
                //There are no credit cards in the system, send to welcomeActivity
                startActivity(new Intent(this, WelcomeActivity.class));
                finish();
                return;
            }
        }





        //All is good, go home!
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

}
