package com.example.virtualwallet.helpers;


import com.example.virtualwallet.models.*;
import com.example.virtualwallet.models.dtos.*;
import com.example.virtualwallet.models.enums.Identity;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.models.enums.WalletRole;
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

    public ReturnDto fromDtoShow(User user) {
        ReturnDto dto = new ReturnDto();
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setUsername(user.getUsername());
        return dto;
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
        user.setEmailVerified(false);

        IdentityStatus identityStatus = new IdentityStatus();
        identityStatus.setId(Identity.PENDING.ordinal() + 1);
        identityStatus.setIdentity(Identity.PENDING);
        user.setIdentityStatus(identityStatus);

        UsersRole role = new UsersRole();
        role.setId(Role.USER.ordinal() + 1);
        role.setRole(Role.USER);
        user.setUsersRole(role);

        user.setDeleted(false);
        user.setStatus(UserStatus.PENDING);

        WalletsRole walletsRole = new WalletsRole();
        walletsRole.setId(WalletRole.USER.ordinal() + 1);
        walletsRole.setWalletRole(WalletRole.USER);
        user.setWalletsRole(walletsRole);

        return user;
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
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setProfilePicture(dto.getProfilePicture());

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
}