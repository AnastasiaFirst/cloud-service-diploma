package com.example.cloud_service_diploma.entity;


import com.example.cloud_service_diploma.enumiration.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "login", nullable = false)
    @NotBlank
    @Size(min = 5, max = 10)
    private String login;

    @Column(name = "password", nullable = false)
    @NotBlank
    @Size(min = 8, max = 30)
    private String password;

    @ElementCollection
    @Column(name = "role", nullable = false)
    private Set<Role> role;

    public UserEntity(Long userId) {
        this.id = userId;
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public Set<Role> getRole() {
        return role;
    }

    public static UserEntity build(Long userId) {
        return new UserEntity(userId);
    }

}