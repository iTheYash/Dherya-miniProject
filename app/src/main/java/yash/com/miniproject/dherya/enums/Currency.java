package yash.com.miniproject.dherya.enums;

import java.io.Serializable;

/**
 * Created by Alex on 6/8/2016.
 */
public enum Currency implements Serializable {

    INR("INR"),
    USD("USD"),
    CAD("CAD"),
    EUR("EUR"),
    AED("AED"),
    ;

    private String mFriendlyName;

    Currency(String friendlyName) {
        mFriendlyName = friendlyName;
    }

    public String getCode(){
        return this.name();
    }

    public String getFriendlyName() {
        return mFriendlyName;
    }


    @Override
    public String toString() {
        return mFriendlyName;
    }

}
