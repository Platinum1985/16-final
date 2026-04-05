package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.exceptions.DataBaseException;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.request.dto.ItemDtoForItemRequestList;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoForGetList;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;

    public ItemRequest addItemRequest(ItemRequestDto itemRequestDto, int requestor) {
        log.info("ItemRequestDto in SERVICE = {}  int Requestor = {}", itemRequestDto.toString(), requestor);
        User reqUser = userRepository.findById(requestor).orElseThrow(() -> new DataBaseException("User-Requestor не найден"));
        log.info("User = {}", reqUser.toString());
        return itemRequestRepository.save(ItemRequestMapper.toItemRequest(itemRequestDto, reqUser));
    }

    public List<ItemRequestDtoForGetList> getAll(int requestorId) { // находит все запрошенные вещи пользователя по его id
        Iterable<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedDesc(requestorId);

        // Преобразуем запросы в DTO с нужными полями
        List<ItemRequestDtoForGetList> itemRequestDtos = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) { // исправлено: теперь переменная имеет корректный тип
            // Создаём список DTO для связанных предметов
            List<ItemDtoForItemRequestList> itemsDto = itemRequest.getItems().stream()
                    .map(item -> new ItemDtoForItemRequestList(item.getId(), item.getName(), item.getOwner().getId()))
                    .collect(Collectors.toList());

            // Формируем итоговый DTO
            ItemRequestDtoForGetList itemRequestDtoForGetList = new ItemRequestDtoForGetList(
                    itemRequest.getId(),
                    itemRequest.getDescription(),
                    itemRequest.getRequestor(),
                    itemRequest.getCreated(),
                    itemsDto
            );
            itemRequestDtos.add(itemRequestDtoForGetList);
        }
        return itemRequestDtos;
    }

    public ItemRequestDtoForGetList itemRequestDtoForGetListById(int requestId) { // возвращаем запрос по id
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Запрос с таким id не найден"));
        List<ItemDtoForItemRequestList> itemsDto = itemRequest.getItems().stream()
                .map(item -> new ItemDtoForItemRequestList(item.getId(), item.getName(), item.getOwner().getId()))
                .toList();
        return new ItemRequestDtoForGetList(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getRequestor(),
                itemRequest.getCreated(),
                itemsDto
        );
    }

    public ItemRequest getItemRequestById(int requestId) { // этот метод для получения ItemRequest в методе по добавлению Item
        return itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFoundException("Не найден"));
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNoFoundIdException(NotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(ValidationException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneralException(Exception e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleGeneralException(DuplicateEmailException e) {
        return Map.of("Duplicate email", e.getMessage());
    }
}


