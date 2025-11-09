package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class NameIdPair {
    int id;
    String name;

    public static int compare(NameIdPair o1, NameIdPair o2) {
        return Integer.compare(o1.getId(), o2.getId());
    }
}
