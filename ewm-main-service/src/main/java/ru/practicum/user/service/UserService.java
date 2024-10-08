package ru.practicum.user.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.user.dto.NewUserRequest;
import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers(List<Long> ids, PageRequest pageRequest);

    UserDto addUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
