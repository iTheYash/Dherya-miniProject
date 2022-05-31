package yash.com.miniproject.dherya.app.adapters;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.holders.NavigationDrawerViewHolder;
import yash.com.miniproject.dherya.model.NavigationDrawerItem;


/**
 * Created by Alex on 5/8/2016.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerViewHolder> {

    private List<NavigationDrawerItem> mDataList = Collections.emptyList();
    private LayoutInflater inflater;
    private Fragment fragment;

    public NavigationDrawerAdapter(Fragment fragment, List<NavigationDrawerItem> data) {
        this.fragment = fragment;
        this.inflater = LayoutInflater.from(fragment.getActivity());
        this.mDataList = data;
    }

    @Override
    public NavigationDrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_nav_drawer, parent, false);
        NavigationDrawerViewHolder holder = new NavigationDrawerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerViewHolder holder, int position) {
        NavigationDrawerItem current = mDataList.get(position);

        holder.setData(fragment, current, position);
        holder.setListeners();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }
}
