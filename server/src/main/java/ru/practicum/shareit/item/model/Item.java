package ru.practicum.shareit.item.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // уникальный идентификатор вещи;

    @Column(name = "name")
    private String name; //— краткое название;

    @Column(name = "description")
    private String description; //— развёрнутое описание;

    @Column(name = "available")
    private Boolean available; // — статус о том, доступна или нет вещь для аренды;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ownerId")
    private User owner; //— владелец вещи;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST) //
    @JoinColumn(name = "itemRequestId")
    @Nullable
    private ItemRequest request; //— если вещь была создана по запросу другого пол

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY) // item-название поля модели Booking
    @JsonIgnore
    private List<Booking> bookings; // список всех бронирований для вещи

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY) // item-название поля модели Comment
    @JsonIgnore
    private List<Comment> comments;  // комментарии к вещи

    public Item(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public Item() {
    }

    public Item(String name, String description, Boolean available, User owner, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", available=" + available +
                ", owner=" + owner +
                ", request=" + request +
                ", bookings=" + bookings +
                '}';
    }
}