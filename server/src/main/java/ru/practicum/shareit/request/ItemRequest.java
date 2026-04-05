package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(of = {"id"})
@Entity
@Table(name = "itemRequests")
@NamedEntityGraph(
        name = "ItemRequest.withItems",
        attributeNodes = @NamedAttributeNode("items")
)
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "description")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "requestorId")
    private User requestor;

    @Column(name = "created")
    private LocalDateTime created;

    @OneToMany(mappedBy = "request", fetch = FetchType.LAZY)
    private List<Item> items;

    public ItemRequest(String description, User requestor) {
        this.description = description;
        this.requestor = requestor;
        this.created = LocalDateTime.now();
    }
}