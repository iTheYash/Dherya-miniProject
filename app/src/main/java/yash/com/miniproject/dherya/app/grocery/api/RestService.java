package yash.com.miniproject.dherya.app.grocery.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import yash.com.miniproject.dherya.app.grocery.model.Category;
import yash.com.miniproject.dherya.app.grocery.model.CategoryResult;
import yash.com.miniproject.dherya.app.grocery.model.Order;
import yash.com.miniproject.dherya.app.grocery.model.OrdersResult;
import yash.com.miniproject.dherya.app.grocery.model.PlaceOrder;
import yash.com.miniproject.dherya.app.grocery.model.ProductResult;
import yash.com.miniproject.dherya.app.grocery.model.Token;
import yash.com.miniproject.dherya.app.grocery.model.User;
import yash.com.miniproject.dherya.app.grocery.model.UserResult;

public interface RestService {

    @POST("api/v1/register")
    Call<UserResult> register(@Body User user);

    @POST("api/v1/login")
    Call<UserResult> login(@Body User user);

    @POST("api/v1/allcategory")
    Call<CategoryResult> allCategory(@Body Token token);

    @POST("api/v1/newProduct")
    Call<ProductResult> newProducts(@Body Token token);

    @POST("api/v1/homepage")
    Call<ProductResult> popularProducts(@Body Token token);

    @POST("api/v1/getlist")
    Call<ProductResult> getCategoryProduct(@Body Category category);

    @POST("api/v1/placeorder")
    Call<OrdersResult> confirmPlaceOrder(@Body PlaceOrder placeOrder);

    @POST("api/v1/orderDetails")
    Call<OrdersResult> orderDetails(@Body Order order);

    @POST("api/v1/updateUser")
    Call<UserResult> updateUser(@Body User user);

    @GET("api/v1/product/search")
    Call<ProductResult> searchProduct(@Query("s") String search);
}
