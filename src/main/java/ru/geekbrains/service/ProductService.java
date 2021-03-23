package ru.geekbrains.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;
import ru.geekbrains.dto.Category;
import ru.geekbrains.dto.Product;

public interface ProductService {
    @GET("products/{id}")
    Call<Product> getProductInfo(@Path("id") int id);

    @POST("products")
    Call<Product> createProduct(@Body Product createProductRequest);

    @PUT("products")
    Call<Product> updateProductInfo(@Body Product updateProductRequest);

    @DELETE("products/{id}")
    Call<ResponseBody> deleteProduct(@Path("id") int id);


}
