package ru.geekbrains;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.geekbrains.base.enums.CategoryType;
import ru.geekbrains.dto.Category;
import ru.geekbrains.service.CategoryService;
import ru.geekbrains.util.RetrofitUtils;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.geekbrains.base.enums.CategoryType.FOOD;

public class CategoryTests {
   static CategoryService categoryService;

    @BeforeAll
    static void beforeAll() throws MalformedURLException {
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
    }

    @Test
    void getFoodCategoryPositiveTest() throws IOException {
        Response<Category> response = categoryService
                .getCategory(FOOD.getId())
                .execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body().getId()).as("Id is not equal to 1!").isEqualTo(1);
        assertThat(response.body().getTitle()).isEqualTo(FOOD.getTitle());
    }

    @Test // сравнение на соответствие названия и номера категории Electronics
    void getElectronicsCategoryCorrectId() throws IOException {
        Response<Category> response = categoryService
                .getCategory(2)
                .execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body().getTitle().equals("Electronics"));
    }

    @Test //сравнение на соответствие названия и номера категории Furniture
    void getFurnitureCategoryCorrectId() throws IOException {
        Response<Category> response = categoryService
                .getCategory(3)
                .execute();
        assertThat(response.isSuccessful()).isTrue();
        assertThat(response.body().getTitle().equals("Furniture"));
    }

    @Test // цикл с перебором по категориям и контролем хэдера на "content-type" = "application/json"
    void cycleTestOfCorrectContentTypeInResponseCategories() throws IOException {
        for (int i = 1; i < 4; i++) {
            Response<Category> response = categoryService
                    .getCategory(i)
                    .execute();
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.headers().get("content-type").equals("application/json"));
        }
    }

    @Test // проверка возврата статуса false на запрос несуществующей категории
    void getSomeNegativeTestAboutAbsentCategory() throws IOException {
        Response<Category> response = categoryService
                .getCategory(4)
                .execute();
        assertThat(response.isSuccessful()).isFalse();
    }

    @Test //цикл с перебором по категориям и контролем хэдера на "Keep-Alive" = "timeout=60"

    void cycleThrowCategoriesTestConnectionKeepAliveInHeader() throws IOException {
        for (int i = 1; i < 4; i++) {
            Response<Category> response = categoryService
                    .getCategory(i)
                    .execute();
            assertThat(response.isSuccessful()).isTrue();
            assertThat(response.headers().get("Keep-Alive").equals("timeout=60"));
        }
    }

}
