package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.practicum.shareit.exceptions.DuplicateEmailException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;

    public User createUser(UserDto userDto) {
        User user = UserDtoMapper.toUser(userDto, 0); // второй параметр UserId изначально 0, будет задаваться в UserStorage при добавлении в репозиторий
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Такой email уже используется");
        }
        log.info("user = {} in service", user);
        return userRepository.save(user);
    }

    public User updateUser(UserDto userDto, int userId) {
        User user = UserDtoMapper.toUser(userDto, userId);
        user.setId(userId);
        log.info("User = {} in service update id = {}", user, userId);
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с таким id не найден");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateEmailException("Такой email уже используется");
        }
        User existUserRep = userRepository.getReferenceById(userId);
        if (user.getName() != null && !user.getName().isBlank()) {
            existUserRep.setName(user.getName());
        }
        if (user.getEmail() != null) {
            if (!user.getEmail().contains("@")) {
                throw new ValidationException("email должен содержать @");
            }
            existUserRep.setEmail(user.getEmail());
        }
        return userRepository.save(existUserRep); // Метод save() в JpaRepository (или CrudRepository) автоматически определит, нужно ли выполнить операцию вставки или обновления, основываясь на наличии идентификатора у сущности. Если идентификатор уже установлен (как в вашем случае), будет выполнена операция обновления.
    }

    public void deleteUser(int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User с таким id не найден");
        } else {
            userRepository.deleteById(id);
        }
    }

    public User getUserById(int id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User с таким id не найден");
        } else {
            return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User с таким id не найден"));
        }
    }
}