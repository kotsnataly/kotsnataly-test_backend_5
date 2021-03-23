package ru.geekbrains;

import com.github.javafaker.Faker;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Converter;
import ru.geekbrains.base.enums.CategoryType;
import ru.geekbrains.dto.ErrorBody;
import ru.geekbrains.dto.Product;
import ru.geekbrains.service.ProductService;
import ru.geekbrains.util.RetrofitUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductTests {
    Integer productId;
    Faker faker = new Faker();
    static ProductService productService;
    Product product;

    @SneakyThrows
    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @BeforeEach
    void setUp() {
        //чет или нечет - соот-но будет продукт либо FOOD либо BOOK
        boolean bookOrFood = System.currentTimeMillis() % 2 == 0;
        product = new Product()
                .withCategoryTitle(bookOrFood ? CategoryType.FOOD.getTitle() : CategoryType.BOOK.getTitle())
                .withPrice((int) (Math.random() * 1000 + 1))
                .withTitle(bookOrFood ? faker.food().ingredient() : faker.book().title());
    }

    @SneakyThrows
    @Test  //legacy
    void createNewProductTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(product)
                        .execute();
        productId = response.body().getId();
        assertThat(response.isSuccessful()).isTrue();
    }

    @SneakyThrows
    @Test //legacy
    void createNewProductNegativeTest() {
        retrofit2.Response<Product> response =
                productService.createProduct(product.withId(555))
                        .execute();
//        productId = Objects.requireNonNull(response.body()).getId();
        assertThat(response.code()).isEqualTo(400);
        if (response != null && !response.isSuccessful() && response.errorBody() != null) {
            ResponseBody body = response.errorBody();
            Converter<ResponseBody, ErrorBody> converter = RetrofitUtils.getRetrofit().responseBodyConverter(ErrorBody.class, new Annotation[0]);
            ErrorBody errorBody = converter.convert(body);
            assertThat(errorBody.getMessage()).isEqualTo("Id must be null for new entity");
        }
    }


    //тест создания товара проверяет содержимое хэдеров соответственно заданным сервером.
    @SneakyThrows
    @Test
    void createProductAndCheckHeaders() {
        retrofit2.Response<Product> response =
                productService.createProduct(product)
                        .execute();
        if (response.isSuccessful()) {
            assertThat(response.headers().get("Keep-Alive").equals("timeout=60"));
            assertThat(response.headers().get("Transfer-Encoding").equals("chunked"));
            assertThat(response.headers().get("Content-Type").equals("application/json"));
            assertThat(response.headers().get("Connection").equals("keep-alive"));
        }
    }

    //тест создания товара проверяет что цена созданного товара > и != 0
    @SneakyThrows
    @Test
    void checkCreatedProductPriceMoreThanZero() {
        retrofit2.Response<Product> response =
                productService.createProduct(product)
                        .execute();
        if (response.isSuccessful()) {
            assertThat(response.body().getPrice() > 0);
        }
    }

    //простая проверка что после создания товара мы сможем по методу GET получить его из "products/{id}"
    @SneakyThrows
    @Test
    void checkGetProduct() {
        createNewProductTest();
        retrofit2.Response<Product> response =
                productService.getProductInfo(productId)
                        .execute();
        assertThat(response.isSuccessful());

    }

    //проверка ответа на GET "products/{id}" запрос с несуществующим id товара.
    @SneakyThrows
    @Test
    void checkUnexistenceIdGetResponse() {
        retrofit2.Response<Product> response =
                productService.getProductInfo(-1)
                        .execute();
        assertThat(!response.isSuccessful());
        assertThat(response.errorBody().string().contains("Unable to find product with id: -1"));
    }


    //проверка ответа на PUT "products/" запрос с null id товара.
    @SneakyThrows
    @Test
    void checkUnexistenceIdUpdateResponse() {
        createNewProductTest();
        product.setId(null);
        retrofit2.Response<Product> response =
                productService.updateProductInfo(product)
                        .execute();
        assertThat(!response.isSuccessful());
        assertThat(response.errorBody().string().contains("Bad Request"));
    }


    // проверка отработки обновления цены методом PUT.
    @SneakyThrows
    @Test
    void checkPriceUpdate() {
        createNewProductTest();
        product.setPrice(100_000_000);
        productService.updateProductInfo(product)
                .execute();
        retrofit2.Response<Product> checkUpdateResponse =
                productService.getProductInfo(productId)
                        .execute();
        assertThat(checkUpdateResponse.body().getPrice() == 100_000_000);

    }


    @AfterEach
    void tearDown() {
        if (productId != null)
            try {
                retrofit2.Response<ResponseBody> response =
                        productService.deleteProduct(productId)
                                .execute();
                assertThat(response.isSuccessful()).isTrue();
            } catch (IOException e) {

            }
    }
}
