package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentRequest;
import ru.practicum.shareit.exceptions.DataBaseException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.ItemForOwnerGetDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final UserService userService;
    private final ItemRequestService itemRequestService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public ItemForOwnerGetDto getItemByOwner(int itemId, int ownerId) {
        User owner = userRepository.findById(ownerId).orElseThrow(() -> new DataBaseException("Нет такого owner"));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item не найден"));
        log.info("-----------itemId={} ________-----ownerId={}", itemId, ownerId);
        log.info("item in service*************** ==============={}", item);
        ItemForOwnerGetDto itemForOwnerGetDto = ItemDtoMapper.toItemForOwnerGetDto(item);
        if (item.getOwner().equals(owner)) {


            List<Booking> bookings = bookingRepository.findByItem(item);
            log.info("BOOKINGs in service*************** BOOOOKINGS ==============={}", bookings);
            LocalDateTime now = LocalDateTime.now();
            Optional<Booking> closestPastBooking = Optional.empty();
            Optional<Booking> closestFutureBooking = Optional.empty();

            for (Booking booking : bookings) {
                if (booking.getStart().isBefore(now)) {
                    if (!closestPastBooking.isPresent() || booking.getStart().isAfter(closestPastBooking.get().getStart())) {
                        closestPastBooking = Optional.of(booking);
                    }
                } else {
                    if (!closestFutureBooking.isPresent() || booking.getStart().isBefore(closestFutureBooking.get().getStart())) {
                        closestFutureBooking = Optional.of(booking);
                    }
                }
            }

            itemForOwnerGetDto.setLastBooking(closestPastBooking.orElse(null));
            itemForOwnerGetDto.setNextBooking(closestFutureBooking.orElse(null));
        }
        List<Comment> comments = commentRepository.findByItem(item);
        log.info("----++++comments ==== {}", comments);
        itemForOwnerGetDto.setComments(comments);
        return itemForOwnerGetDto;
    }

    public Comment addComment(int itemId, CommentRequest commentRequest, int authorId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new DataBaseException("Такой Item не найден"));
        log.info("in service item = {}", item);

        Comment comment = new Comment();
        comment.setText(commentRequest.getText());
        comment.setItem(item);
        log.info("in service comment = {}", comment);
        User author = userRepository.findById(authorId).orElseThrow(() -> new DataBaseException("User not found"));
        log.info("====****authorId=====****{}", author.getId());
        comment.setAuthorName(author.getName());
        comment.setCreated(Instant.now());

        // Проверка, что пользователь действительно брал вещь в аренду
        if (!userHasRentalHistory(item, author)) {
            throw new DataBaseException("User has not rented the item");
        }

        return commentRepository.save(comment);
    }

    private boolean userHasRentalHistory(Item item, User author) {
        Booking booking = bookingRepository.findByItemAndBooker(item, author);
        log.info("boooooking booking = {}", booking);
        boolean isBookerItem = booking.getStatus().equals(Status.APPROVED) && booking.getEnd().isBefore(LocalDateTime.now());
        log.info("******!!!!!---isBookerItem =================={}", isBookerItem);
        return isBookerItem;

    }

    public Item addItem(ItemDto itemDto) {
        if (!validationItemDto(itemDto)) {
            throw new ValidationException("Некорректно заполнены поля itemDto");
        }
        Item item = ItemDtoMapper.toItem(itemDto, itemRequestService);
        log.info("item = {} сервис перед сохранением", item);
        return itemRepository.save(item);
    }

    public Item patchItem(ItemDto itemDto, int ownerId) {
        Item item = itemRepository.findById(itemDto.getId()).orElseThrow(() -> new DataBaseException("Item с таким id не найден"));
        if (item.getOwner().getId() == ownerId) {
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            return itemRepository.save(item);
        } else {
            throw new NotFoundException("You are not the owner of this item");
        }
    }

    public List<Item> getAllItemsForOwner(int ownerId) {
        User owner = userService.getUserById(ownerId);
        System.out.println("Полученный пользователь: " + owner);
        List<Item> items = itemRepository.findByOwner(owner);
        System.out.println("Количество элементов: " + items.size());
        return items;
    }

    public List<Item> searchAvailableItems(String searchText) {
        log.info("Поиск по тексту: {}", searchText);
        if (searchText.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailable() != null && item.getAvailable().booleanValue()
                        && (item.getName() != null && item.getName().toLowerCase().contains(searchText.toLowerCase())
                        || item.getDescription() != null && item.getDescription().toLowerCase().contains(searchText.toLowerCase())))
                .collect(Collectors.toList());
    }

    private boolean validationItemDto(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            return false;
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            return false;
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isEmpty()) {
            return false;
        }
        return true;
    }
}