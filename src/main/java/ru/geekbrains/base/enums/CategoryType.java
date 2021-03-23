package ru.geekbrains.base.enums;

import lombok.Getter;

public enum CategoryType {
    FOOD(1, "Food"),
    BOOK(2, "Electronic"); // пришлось тут пойти против логики

    @Getter
    private final Integer id;
    @Getter
    private final String title;

    CategoryType(int id, String title) {
        this.id = id;
        this.title = title;
    }
}
