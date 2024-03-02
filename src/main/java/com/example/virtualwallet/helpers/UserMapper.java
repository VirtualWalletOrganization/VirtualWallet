package com.example.virtualwallet.helpers;


import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.dtos.PhoneNumberDto;
import com.example.virtualwallet.models.dtos.RegisterDto;
import com.example.virtualwallet.models.dtos.UserDto;
import com.example.virtualwallet.models.dtos.UserResponseDto;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final UserService userService;

    @Autowired
    public UserMapper(UserService userService) {
        this.userService = userService;
    }

    public User fromDtoRegister(UserDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        return user;
    }

    public User fromDtoUpdate(int id, UserDto dto) {
        User updatedUser = userService.getById(id);
        updatedUser.setId(id);
        updatedUser.setUsername(userService.getById(id).getUsername());
        updatedUser.setFirstName(dto.getFirstName());
        updatedUser.setLastName(dto.getLastName());
        updatedUser.setEmail(dto.getEmail());
        updatedUser.setPassword(dto.getPassword());
        return updatedUser;
    }

    public UserResponseDto toDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setFirstName(user.getFirstName());
        userResponseDto.setLastName(user.getLastName());
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setRole(user.getUsersRole().getRole());
        userResponseDto.setPhoneNumber(user.getPhoneNumber());
        return userResponseDto;
    }

    public UserDto userToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public UserResponseDto toDtoRegisterAndUpdateUser(User user) {
        User userToUpdate = userService.getByUsername(user.getUsername());
        UserResponseDto userResponseDto = toDto(userToUpdate);
        return userResponseDto;
    }

    public User fromDtoUpdatePhoneNumber(int id, PhoneNumberDto dto, UserDto userDto) {
        User updatedUser = fromDtoUpdate(id, userDto);
        updatedUser.setPhoneNumber(dto.getPhoneNumber());
        return updatedUser;
    }

    public User fromDto(RegisterDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.getUsersRole().setRole(Role.USER);
        return user;
    }
}