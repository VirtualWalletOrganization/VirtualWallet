package com.example.virtualwallet.models;

import com.example.virtualwallet.models.enums.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users_roles")
public class UsersRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role_name")
    private Role role;

    public UsersRole() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}