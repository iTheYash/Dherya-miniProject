package yash.com.miniproject.dherya.app.grocery;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.activities.HomeActivity;
import yash.com.miniproject.dherya.app.grocery.adapter.CheckoutCartAdapter;
import yash.com.miniproject.dherya.app.grocery.api.clients.RestClient;
import yash.com.miniproject.dherya.app.grocery.model.Cart;
import yash.com.miniproject.dherya.app.grocery.model.Order;
import yash.com.miniproject.dherya.app.grocery.model.OrderItem;
import yash.com.miniproject.dherya.app.grocery.model.OrdersResult;
import yash.com.miniproject.dherya.app.grocery.model.PlaceOrder;
import yash.com.miniproject.dherya.app.grocery.model.User;
import yash.com.miniproject.dherya.app.grocery.util.localstorage.LocalStorage;
import yash.com.miniproject.dherya.app.recipe.RecommendedRecipeActivity;

public class ConfirmFragment extends Fragment {
    LocalStorage localStorage;
    List<Cart> cartList = new ArrayList<>();
    Gson gson;
    RecyclerView recyclerView;
    CheckoutCartAdapter adapter;
    RecyclerView.LayoutManager recyclerViewlayoutManager;
    TextView back, order;
    TextView total, shipping, totalAmount;
    Double _total, _shipping, _totalAmount;
    ProgressDialog progressDialog;
    List<Order> orderList = new ArrayList<>();
    List<OrderItem> orderItemList = new ArrayList<>();
    PlaceOrder confirmOrder;
    String orderNo;
    String id;
    OrderItem orderItem = new OrderItem();

    NotificationManagerCompat notificationManagerCompat;
    Notification notification;

    TaskStackBuilder stackBuilder;

    public ConfirmFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm, container, false);
        localStorage = new LocalStorage(getContext());
        recyclerView = view.findViewById(R.id.cart_rv);
        totalAmount = view.findViewById(R.id.total_amount);
        total = view.findViewById(R.id.total);
        shipping = view.findViewById(R.id.shipping_amount);
        back = view.findViewById(R.id.back);
        order = view.findViewById(R.id.place_order);
        progressDialog = new ProgressDialog(getContext());
        gson = new Gson();
        orderList = ((BaseActivity) getActivity()).getOrderList();
        orderList = ((BaseActivity) getActivity()).getOrderList();
        cartList = ((BaseActivity) getContext()).getCartList();
        User user = gson.fromJson(localStorage.getUserLogin(), User.class);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("myCh", "MyChannel", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }



        stackBuilder = TaskStackBuilder.create(getContext());
        Intent intent = new Intent(getContext(), RecommendedRecipeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "myCh")
                .setSmallIcon(R.drawable.icon_about)
                .setContentTitle("New Notification")
                .setContentText("This is sample notification")
                .setContentIntent(contentIntent)
                .setAutoCancel(true);
        notification = builder.build();
        builder.setContentIntent(resultPendingIntent);
        notificationManagerCompat = NotificationManagerCompat.from(getContext());


        for (int i = 0; i < cartList.size(); i++) {

            orderItem = new OrderItem(cartList.get(i).getTitle(), cartList.get(i).getQuantity(), cartList.get(i).getAttribute(), cartList.get(i).getCurrency(), cartList.get(i).getImage(), cartList.get(i).getPrice(), cartList.get(i).getSubTotal());
            orderItemList.add(orderItem);
        }

        confirmOrder = new PlaceOrder(user.getToken(), user.getFname(), " ", user.getMobile(), user.getCity(), user.getAddress(), user.getId(), orderItemList);


        _total = ((BaseActivity) getActivity()).getTotalPrice();
        _shipping = 0.0;
        _totalAmount = _total + _shipping;
        total.setText(_total + "");
        shipping.setText(_shipping + "");
        totalAmount.setText(_totalAmount + "");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CartActivity.class));
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                placeUserOrder();
                push(v);


            }
        });

        setUpCartRecyclerview();
        return view;
    }

    public void push(View view){
        notificationManagerCompat.notify(1, notification);
    }

    private void placeUserOrder() {
        progressDialog.setMessage("Confirming Order...");
        progressDialog.show();
        Log.d("Confirm Order==>", gson.toJson(confirmOrder));
        Call<OrdersResult> call = RestClient.getRestService(getContext()).confirmPlaceOrder(confirmOrder);
        call.enqueue(new Callback<OrdersResult>() {
            @Override
            public void onResponse(Call<OrdersResult> call, Response<OrdersResult> response) {
                Log.d("respose==>", response.body().getCode() + "");

                OrdersResult ordersResult = response.body();

                if (ordersResult.getCode() == 200) {
                    localStorage.deleteCart();
                    showCustomDialog();
                }

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<OrdersResult> call, Throwable t) {
                Log.d("Error respose==>", t.getMessage() + "");
                progressDialog.dismiss();
            }
        });


    }


    private void showCustomDialog() {

        // Create custom dialog object
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        // Include dialog.xml file
        dialog.setContentView(R.layout.success_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                startActivity(new Intent(getContext(), HomeActivity.class));
                getActivity().finish();
            }
        });
        // Set dialog title

        dialog.show();
    }

    private void setUpCartRecyclerview() {
        recyclerView.setHasFixedSize(true);
        recyclerViewlayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(recyclerViewlayoutManager);
        adapter = new CheckoutCartAdapter(cartList, getContext());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Confirm");
    }


}
