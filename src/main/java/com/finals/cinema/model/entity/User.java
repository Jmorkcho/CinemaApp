package com.finals.cinema.model.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;
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
}