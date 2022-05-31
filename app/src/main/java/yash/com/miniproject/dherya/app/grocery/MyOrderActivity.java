package yash.com.miniproject.dherya.app.grocery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.grocery.adapter.OrderAdapter;
import yash.com.miniproject.dherya.app.grocery.api.clients.RestClient;
import yash.com.miniproject.dherya.app.grocery.model.Order;
import yash.com.miniproject.dherya.app.grocery.model.OrdersResult;
import yash.com.miniproject.dherya.app.grocery.model.Product;
import yash.com.miniproject.dherya.app.grocery.model.User;
import yash.com.miniproject.dherya.app.grocery.util.localstorage.LocalStorage;

public class MyOrderActivity extends AppCompatActivity {

    LocalStorage localStorage;
    LinearLayout linearLayout;
    private List<Order> orderList = new ArrayList<>();
    private List<Product> productList = new ArrayList<>();
    Gson gson = new Gson();
    private RecyclerView recyclerView;
    private OrderAdapter mAdapter;
    Order order;
    private List<Order> newOrderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);

        recyclerView = findViewById(R.id.order_rv);
        linearLayout = findViewById(R.id.no_order_ll);
        localStorage = new LocalStorage(this);

        User user = gson.fromJson(localStorage.getUserLogin(), User.class);
        order = new Order(user.getId(), user.getToken());
        fetchOrderDetails(order);
    }

    private void fetchOrderDetails(Order order) {

        Call<OrdersResult> call = RestClient.getRestService(this).orderDetails(order);
        call.enqueue(new Callback<OrdersResult>() {
            @Override
            public void onResponse(Call<OrdersResult> call, Response<OrdersResult> response) {
                Log.d("Response :=>", response.body() + "");
                if (response != null) {

                    OrdersResult ordersResult = response.body();
                    if (ordersResult.getCode() == 200) {

                        orderList = ordersResult.getOrderList();
                        setupOrderRecycleView();

                    }

                }
            }

            @Override
            public void onFailure(Call<OrdersResult> call, Throwable t) {

            }
        });

    }

    private void setupOrderRecycleView() {
        if (orderList.isEmpty()) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
        mAdapter = new OrderAdapter(orderList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }


}