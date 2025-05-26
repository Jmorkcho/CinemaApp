package com.finals.cinema.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private int age;
    private int roleId;
    private int statusId;
    private LocalDateTime createdAt;
    @OneToMany(mappedBy = "owner")
    @JsonManagedReference
    private List<Ticket> tickets;
    private boolean isEnabled;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private ConfirmationToken confirmationToken;

    // Method to get authorities based on roleId
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        // Map roleId to roles
        switch (roleId) {
            case 1:
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
            case 2:
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                break;
            // Add more roles as needed
            default:
                break;
        }

        return authorities;
    }

}


