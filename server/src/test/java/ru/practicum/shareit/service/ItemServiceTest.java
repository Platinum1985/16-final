package ru.practicum.shareit.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRequest;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemForOwnerGetDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {

    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @BeforeEach
    public void setUp() {
        // Очистка данных перед каждым тестом (опционально)
    }

    // Тест 1: получение предмета владельца
   /* @Test
    public void getItemByOwner_ShouldReturnItemForOwner() {
        // Создаём пользователя
        User user = userService.createUser(new UserDto("user1", "user1@mail.ru"));

        // Создаём DTO для предмета
        ItemDto itemDto = new ItemDto(
                0,                    // id (0 — новый предмет)
                "Laptop",             // название предмета
                "Gaming laptop",      // описание
                true,                 // доступен
                user.getId(),         // ID владельца — берём из созданного пользователя
                0                     // requests — 0, если нет запросов
        );

        // Преобразуем DTO в модель Item и сохраняем в БД
        Item itemSaved = ItemDtoMapper.toItem(itemDto, itemRequestService, userService);
        Item item = itemRepository.save(itemSaved);

        // Получаем предмет для владельца — исправлены параметры метода
        // Первый параметр — ID владельца (user.getId()), второй — ID предмета (item.getId())
        ItemForOwnerGetDto result = itemService.getItemByOwner(user.getId(), item.getId());

        assertThat(result.getId()).isEqualTo(item.getId());      // ID предмета
        assertThat(result.getName()).isEqualTo("Laptop");       // Название — как в DTO
        assertThat(result.getOwner().getId()).isEqualTo(user.getId());  // ID владельца совпадает
    } */

    @Test
    public void addItem_ShouldCreateNewItemSuccessfully() {
        // Создаём пользователя
        User user = userService.createUser(new UserDto("user1", "user1@mail.ru"));

        // Готовим DTO для нового предмета
        ItemDto itemDto = new ItemDto(
                0,                    // id (0 — новый предмет)
                "Book",               // название предмета
                "Interesting book",   // описание
                true,                 // доступен
                user.getId(),         // ID владельца
                0                     // requests — 0, если нет запросов
        );

        // Вызываем метод создания предмета
        Item createdItem = itemService.addItem(itemDto);

        // Проверяем, что предмет создан и имеет корректные данные
        assertThat(createdItem.getId()).isGreaterThan(0);  // ID должен быть сгенерирован
        assertThat(createdItem.getName()).isEqualTo("Book");
        assertThat(createdItem.getDescription()).isEqualTo("Interesting book");
        assertThat(createdItem.getAvailable()).isTrue();
        assertThat(createdItem.getOwner().getId()).isEqualTo(user.getId());
    }

    @Test
    public void addItem_ShouldThrowValidationException_WhenItemDtoIsInvalid() {
        // Создаём пользователя
        User user = userService.createUser(new UserDto("user1", "user1@mail.ru"));

        // Готовим некорректный DTO (нет названия)
        ItemDto itemDto = new ItemDto(
                0,
                null,                 // некорректное название
                "Interesting book",   // описание
                true,                 // доступен
                user.getId(),         // ID владельца
                0                     // requests — 0, если нет запросов
        );

        // Проверяем, что метод выбрасывает исключение
        assertThatThrownBy(() -> itemService.addItem(itemDto))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Некорректно заполнены поля itemDto");
    }

    @Test
    public void patchItem_ShouldUpdateItemSuccessfully() {
        // Создаём пользователя
        User user = userService.createUser(new UserDto("user1", "user1@mail.ru"));
        ItemDto itemDto = new ItemDto(0, "Book", "Interesting book", true, user.getId(), 0);
        // Создаём предмет
        Item itemSaved = ItemDtoMapper.toItem(itemDto, itemRequestService, userService);
        Item item = itemRepository.save(itemSaved);

        // Готовим DTO с обновлёнными данными
        ItemDto updatedDto = new ItemDto(
                item.getId(),
                "Updated Book",      // новое название
                "New description",    // новое описание
                false,               // недоступен
                user.getId(),
                0
        );

        // Вызываем метод обновления
        Item updatedItem = itemService.patchItem(updatedDto, user.getId());

        // Проверяем обновлённые данные
        assertThat(updatedItem.getName()).isEqualTo("Updated Book");
        assertThat(updatedItem.getDescription()).isEqualTo("New description");
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    public void addComment_ShouldCreateCommentSuccessfully() {
        // 1. Создаём пользователя, который будет автором комментария
        User author = userService.createUser(new UserDto("author1", "author1@mail.ru"));

        // 2. Создаём второго пользователя — владельца предмета
        User owner = userService.createUser(new UserDto("owner1", "owner1@mail.ru"));

        // 3. Создаём предмет, к которому будет добавлен комментарий
        ItemDto itemDto = new ItemDto(
                0,                    // id (0 — новый предмет)
                "Book",               // название предмета
                "Interesting book",   // описание
                true,                 // доступен
                owner.getId(),        // ID владельца
                0                     // requests — 0, если нет запросов
        );

        Item itemSaved = ItemDtoMapper.toItem(itemDto, itemRequestService, userService);
        Item item = itemRepository.save(itemSaved);

        // 4. Создаём бронирование (аренду) с корректными датами
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(author);
        booking.setStatus(Status.APPROVED); // или другой статус, который учитывается в userHasRentalHistory
        booking.setStart(LocalDateTime.now().minusDays(1)); // начало аренды
        booking.setEnd(LocalDateTime.now()); // конец аренды
        bookingRepository.save(booking);

        // 5. Создаём запрос на комментарий
        CommentRequest commentRequest = new CommentRequest("Отличный предмет!");

        // 6. Вызываем тестируемый метод
        Comment createdComment = itemService.addComment(
                item.getId(),
                commentRequest,
                author.getId()
        );

        // 7. Проверяем результаты
        assertThat(createdComment.getId()).isGreaterThan(0); // Комментарий сохранён в БД
        assertThat(createdComment.getText()).isEqualTo("Отличный предмет!"); // Текст комментария верный
        assertThat(createdComment.getAuthorName()).isEqualTo(author.getName()); // Имя автора верное
        assertThat(createdComment.getItem().getId()).isEqualTo(item.getId()); // Комментарий привязан к предмету
    }
}