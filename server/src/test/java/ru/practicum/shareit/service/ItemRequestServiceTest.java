package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForGetList;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {

    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    @BeforeEach
    public void setUp() {
    }

    // Тест 1: добавление запроса
    @Test
    public void addItemRequest_shouldCreateRequestSuccessfully() {
        User userSaved1 = new User(0, "User1", "user1@example.com");
        User user1 = userRepository.save(userSaved1);
        ItemRequestDto itemRequestDto = new ItemRequestDto("Ищу книгу по Java");

        // Когда
        ItemRequest createdRequest = itemRequestService.addItemRequest(itemRequestDto, user1.getId());

        assertThat(createdRequest.getDescription()).isEqualTo("Ищу книгу по Java");
        assertThat(createdRequest.getRequestor().getId()).isEqualTo(user1.getId());
    }

    // получение всех запросов пользователя
    @Test
    public void getAllUserRequests_shouldReturnUsersOwnRequests() {
        User userSaved1 = new User(0, "User1", "user1@example.com");
        User userSaved2 = new User(0, "User2", "user2@example.com");
        User user1 = userRepository.save(userSaved1);
        User user2 = userRepository.save(userSaved2);

        ItemRequest request1 = itemRequestRepository.save(
                new ItemRequest("Запрос 1", user1, LocalDateTime.now())
        );
        ItemRequest request2 = itemRequestRepository.save(
                new ItemRequest("Запрос 2", user2, LocalDateTime.now().minusHours(1))
        );
        System.out.println("request1===================" + request1);
        List<ItemRequestDtoForGetList> userRequests = itemRequestService.getAllUserRequests(user1.getId());

        assertThat(userRequests).hasSize(1);
        assertThat(userRequests.get(0).getId()).isEqualTo(request1.getId());
    }

    @Test
    public void itemRequestDtoForGetListById_shouldReturnRequestById() {
        User user = userRepository.save(new User(0, "User1", "user1@example.com"));
        ItemRequest request = itemRequestRepository.save(new ItemRequest("Запрос 1", user, LocalDateTime.now()));

        ItemRequestDtoForGetList itemRequestDto = itemRequestService.itemRequestDtoForGetListById(request.getId());

        assertThat(itemRequestDto.getId()).isEqualTo(request.getId());
        assertThat(itemRequestDto.getDescription()).isEqualTo("Запрос 1");
        assertThat(itemRequestDto.getRequestor().getId()).isEqualTo(user.getId());
    }

    @Test
    public void getAllOtherRequests_shouldReturnAllRequestsExceptCurrentUser() {
        User user1 = userRepository.save(new User(0, "User1", "user1@example.com"));
        User user2 = userRepository.save(new User(0, "User2", "user2@example.com"));

        ItemRequest request1 = itemRequestRepository.save(new ItemRequest("Запрос 1", user1, LocalDateTime.now()));
        ItemRequest request2 = itemRequestRepository.save(new ItemRequest("Запрос 2", user2, LocalDateTime.now().minusHours(1)));

        List<ItemRequestDtoForGetList> otherRequests = itemRequestService.getAllOtherRequests(user1.getId());

        assertThat(otherRequests).hasSize(1);
        assertThat(otherRequests.get(0).getId()).isEqualTo(request2.getId());
    }
}
