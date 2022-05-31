package yash.com.miniproject.dherya.app.holders;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.activities.AppPreferenceActivity;
import yash.com.miniproject.dherya.app.fragments.AboutFragment;
import yash.com.miniproject.dherya.app.fragments.CreditCardListFragment;
import yash.com.miniproject.dherya.app.fragments.ChartsFragment;
import yash.com.miniproject.dherya.app.fragments.NavigationDrawerFragment;
import yash.com.miniproject.dherya.app.fragments.ExpenseListFragment;
import yash.com.miniproject.dherya.app.fragments.NotessFragment;
import yash.com.miniproject.dherya.app.fragments.OverviewFragment;
import yash.com.miniproject.dherya.app.grocery.GroceryFragment;
import yash.com.miniproject.dherya.app.notes.ToDoFragment;
import yash.com.miniproject.dherya.app.recipe.RecipeeFragment;
import yash.com.miniproject.dherya.app.tasks.TaskFragment;
import yash.com.miniproject.dherya.model.NavigationDrawerItem;

public class NavigationDrawerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView title;
    private ImageView icon;
    private LinearLayout container;

    private NavigationDrawerItem current;
    private Fragment fragment;
    private int position;

    public NavigationDrawerViewHolder(View itemView) {
        super(itemView);

        title = (TextView) itemView.findViewById(R.id.txt_title_nav_drawer);
        icon = (ImageView) itemView.findViewById(R.id.img_icon_nav_drawer);
        container = (LinearLayout) itemView.findViewById(R.id.container_item_nav_drawer);
    }

    public void setData(Fragment fragment, NavigationDrawerItem current, int position) {
        this.fragment = fragment;
        this.current = current;
        this.position = position;

        this.title.setText(current.getTitle());
        this.icon.setImageResource(current.getIconID());
    }

    public void setListeners() {
        container.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch(id) {
            case R.id.container_item_nav_drawer:
                FragmentManager fm = fragment.getFragmentManager();
                Fragment f = null;
                switch (position) {
                    case 0:
                        f = new OverviewFragment();
                        break;
                    case 1:
                        f = new ToDoFragment();
                        break;
                    case 2:
                        f = new RecipeeFragment();
                        break;
                    case 3:
                        f = new GroceryFragment();
                        break;
                    case 4:
                        f = new ExpenseListFragment();
                        break;
                    case 5:
                        f = new ChartsFragment();
                        break;
                    case 6:
                        f = new CreditCardListFragment();
                        break;
                    case 7:
                        Intent intent = new Intent(fragment.getActivity(), AppPreferenceActivity.class);
                        fragment.getActivity().startActivity(intent);
                        break;
                    case 8:
                        f = new AboutFragment();
                        break;
                    default:
                        Toast.makeText(fragment.getActivity(), title.getText().toString() + " is under construction :)", Toast.LENGTH_SHORT).show();
                        break;
                }

                if(f != null) {
                    fm.beginTransaction().replace(R.id.home_content_frame, f).commit();

                    ((NavigationDrawerFragment)fragment).closeDrawer();
                }
                break;
        }
    }

}
