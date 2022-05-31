package yash.com.miniproject.dherya.app.recipe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.grocery.adapter.OrderAdapter;
import yash.com.miniproject.dherya.app.grocery.model.Order;
import yash.com.miniproject.dherya.app.grocery.model.Product;
import yash.com.miniproject.dherya.app.grocery.model.User;
import yash.com.miniproject.dherya.app.grocery.util.localstorage.LocalStorage;
import yash.com.miniproject.dherya.app.notes.model.ToDoModel;
import yash.com.miniproject.dherya.app.notes.utils.DatabaseHandler;

public class RecommendedRecipeActivity extends AppCompatActivity {

    ProgressDialog dialog;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecyclerView recyclerView;

    List<String> tags = new ArrayList<>();

    LocalStorage localStorage;
    LinearLayout linearLayout;
    private List<Order> orderList = new ArrayList<>();
    Gson gson = new Gson();
    private OrderAdapter mAdapter;
    Order order;
    private List<Order> newOrderList = new ArrayList<>();

    private List<Product> productList = new ArrayList<>();

    private List<ToDoModel> taskList;

    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_recipe);

        dialog = new ProgressDialog(this);
        dialog.setTitle("Loading...");
        manager = new RequestManager(this);

        localStorage = new LocalStorage(this);

        User user = gson.fromJson(localStorage.getUserLogin(), User.class);
        order = new Order(user.getId(), user.getToken());


        db = new DatabaseHandler(this);
        db.openDatabase();

        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        tags.clear();
        tags.add("potato");
        manager.getRandomRecipe(randomRecipeResponseListener, tags);
        dialog.show();
    }


    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            dialog.dismiss();
            recyclerView = findViewById(R.id.recycler_recomm);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(RecommendedRecipeActivity.this, 1));
            randomRecipeAdapter = new RandomRecipeAdapter(RecommendedRecipeActivity.this, response.recipes, recipeClickListener);
            recyclerView.setAdapter(randomRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecommendedRecipeActivity.this, message, Toast.LENGTH_SHORT).show();
        }
    };


    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(RecommendedRecipeActivity.this, RecipeDetailsActivity.class).putExtra("id", id));
        }
    };

}