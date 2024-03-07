package com.example.virtualwallet.helpers;


import com.example.virtualwallet.models.Photo;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.Wallet;
import com.example.virtualwallet.models.dtos.*;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.services.contracts.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserMapper {

    private final UserService userService;

    @Autowired
    public UserMapper(UserService userService) {
        this.userService = userService;
    }

    public User fromDtoRegister(RegisterDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setProfilePicture(dto.getProfilePicture());
        return user;
    }

    public Wallet fromDtoCreateWallet(RegisterDto registerDto) {
        Wallet wallet = new Wallet();
        wallet.setDefault(true);
        wallet.setBalance(BigDecimal.ONE);
        wallet.setCurrency(registerDto.getCurrency());
        return wallet;
    }
    public Photo fromDtoCreatePhoto(RegisterDto registerDto) {
        Photo photo = new Photo();
        photo.setSelfie(registerDto.getSelfie());
        photo.setCardId(registerDto.getCardId());
        return photo;
    }

    public User fromDtoUpdate(int id, UpdateUserDto dto) {
        User user = userService.getById(id);
        user.setId(id);
        user.setUsername(userService.getById(id).getUsername());
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        return user;
    }

    public UserResponseDto toDto(User user) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(user.getId());
        userResponseDto.setUsername(user.getUsername());
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

    public User fromDtoUpdatePhoneNumber(int id, PhoneNumberDto dto, UpdateUserDto userDto) {
        User updatedUser = fromDtoUpdate(id, userDto);
        updatedUser.setPhoneNumber(dto.getPhoneNumber());
        return updatedUser;
    }

    public User fromDto(RegisterDto dto) {
        User user = new User();
//        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.getUsersRole().setRole(Role.USER);
        return user;
    }

}