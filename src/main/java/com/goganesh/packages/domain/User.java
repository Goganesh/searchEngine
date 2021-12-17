package com.goganesh.packages.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String username;

    private String password;

    @Column(name = "is_enabled")
    private boolean isEnabled;
}
