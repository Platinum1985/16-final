package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findByRequestor(User requestor);

    @EntityGraph(value = "ItemRequest.withItems", type = EntityGraph.EntityGraphType.LOAD)
    Optional<ItemRequest> findById(int id);

    @EntityGraph(value = "ItemRequest.withItems", type = EntityGraph.EntityGraphType.LOAD)
    List<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(int requestorId);

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.requestor.id <> :requestorId ORDER BY ir.created DESC")
    Iterable<ItemRequest> findAllByRequestorIdNotOrderByCreatedDesc(@Param("requestorId") int requestorId);
}
