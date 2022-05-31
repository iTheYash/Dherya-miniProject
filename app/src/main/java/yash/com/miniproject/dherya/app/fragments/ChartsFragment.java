package yash.com.miniproject.dherya.app.fragments;


import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.adapters.ChartsViewPagerAdapter;

public class ChartsFragment extends Fragment {


    private ViewPager mGraphsViewpager;
    private TabLayout mTabLayout;


    private List<String> titleList = new ArrayList<>();
    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.fragment_name_graphs));
        Spinner mSpinner = getActivity().findViewById(R.id.spinner_tagss);
        mSpinner.setVisibility(View.GONE);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        initLists();

        View rootView = inflater.inflate(R.layout.fragment_charts, container, false);

        ChartsViewPagerAdapter adapter = new ChartsViewPagerAdapter(getActivity().getSupportFragmentManager(), titleList, fragmentList);
        mGraphsViewpager = (ViewPager) rootView.findViewById(R.id.charts_viewpager);
        mTabLayout = (TabLayout) rootView.findViewById(R.id.charts_tab_layout);

        mGraphsViewpager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mGraphsViewpager);

        return rootView;
    }

    private void initLists() {
        titleList.add("DAILY TOTALS");
        titleList.add("BY CATEGORY");
        fragmentList.add(new ChartExpenseFragment());
        fragmentList.add(new ChartCategoryFragment());
    }
}
