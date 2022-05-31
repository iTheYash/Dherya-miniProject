package yash.com.miniproject.dherya.app.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Locale;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.recipe.RecommendedRecipeActivity;

/**
 * Created by abice on 2/2/2017.
 */

public class AboutFragment extends Fragment {

    Button button;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_about));
        Spinner mSpinner = getActivity().findViewById(R.id.spinner_tagss);
        mSpinner.setVisibility(View.GONE);

        button = getActivity().findViewById(R.id.buttonR);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), RecommendedRecipeActivity.class));
            }
        });
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);


        TextView websiteLink = (TextView) rootView.findViewById(R.id.fragment_about_website_link);






        websiteLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWebBrowser(getActivity(), getResources().getString(R.string.url_website));
            }
        });

        return rootView;
    }


    public static Pair<String, Integer> getAppVersionAndBuild(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return new Pair<>(pInfo.versionName, pInfo.versionCode);
        } catch (Exception e) {
            return new Pair<>("", 0);
        }
    }

    @SuppressLint("DefaultLocale")
    public static boolean launchWebBrowser(Context context, String url) {
        try {
            url = url.toLowerCase();
            if (!url.startsWith("http://") || !url.startsWith("https://")) {
                url = "http://" + url;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            if (null == resolveInfo) {
                Toast.makeText(context, "Could not find a Browser to open link", Toast.LENGTH_SHORT).show();
                return false;
            }
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            Toast.makeText(context, "Could not start web browser", Toast.LENGTH_SHORT).show();

            return false;
        }
    }
}
