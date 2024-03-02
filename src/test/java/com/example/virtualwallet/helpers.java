package com.example.virtualwallet;
import com.example.virtualwallet.models.User;
import com.example.virtualwallet.models.enums.Role;
import com.example.virtualwallet.models.enums.UserStatus;
import com.example.virtualwallet.utils.UserFilterOptions;

public class helpers {
    public static User createMockUser() {
        User mockUser = new User();
        mockUser.setId(1);
        mockUser.setUsername("MockUsername");
        mockUser.setPassword("MockPassword");
        mockUser.setFirstName("MockFirstName");
        mockUser.setLastName("MockLastName");
        mockUser.setEmail("mock@user.com");
        mockUser.setStatus(UserStatus.ACTIVE);
        mockUser.setPhoneNumber("3333333333");
        mockUser.setRole(Role.ADMIN);

        return mockUser;
    }
    public static UserFilterOptions createMockUserFilterOptions() {
        return new UserFilterOptions(
                createMockUser().getUsername(),
                createMockUser().getFirstName(),
                createMockUser().getLastName(),
                createMockUser().getEmail(),
                Role.USER,
                UserStatus.ACTIVE,
                "sort",
                "order");
    }
}
