package yash.com.miniproject.dherya.app.grocery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.List;

import yash.com.miniproject.dherya.R;
import yash.com.miniproject.dherya.app.grocery.MyOrderActivity;
import yash.com.miniproject.dherya.app.grocery.model.Order;
import yash.com.miniproject.dherya.app.grocery.model.Product;
import yash.com.miniproject.dherya.app.grocery.util.localstorage.LocalStorage;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    List<Order> orderList;
    Context context;
    int pQuantity = 1;
    String _subtotal, _price, _quantity;
    LocalStorage localStorage;
    Gson gson;

    public OrderAdapter(List<Order> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;

        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        localStorage = new LocalStorage(context);
        gson = new Gson();

        final Order order = orderList.get(position);

        holder.orderId.setText("#" + order.getId());
        holder.date.setText(order.getDate());
        holder.total.setText(order.getTotal());
        holder.status.setText(order.getStatus());


    }

    @Override
    public int getItemCount() {

        return orderList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView orderName, orderId, date, total, status;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            orderName = itemView.findViewById(R.id.order_name);
            orderId = itemView.findViewById(R.id.order_id);
            date = itemView.findViewById(R.id.date);
            total = itemView.findViewById(R.id.total_amount);
            status = itemView.findViewById(R.id.status);

        }
    }
}
