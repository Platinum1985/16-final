package ru.practicum.shareit.comment;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;


import java.time.Instant;

@Setter
@Getter
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "text")
    private String text;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "itemId")
    private Item item;

    @Column(name = "authorName")
    private String authorName;

    @Column(name = "created")
    private Instant created;

    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", itemId=" + (item != null ? item.getId() : null) +
                ", authorName='" + authorName + '\'' +
                ", created=" + created +
                "}";
    }
}
