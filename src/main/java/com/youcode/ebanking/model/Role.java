package com.youcode.ebanking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Exemple: "ROLE_USER", "ROLE_ADMIN"

    @OneToMany(mappedBy = "role")
    private List<EbUser> users = new ArrayList<>();


    public Role(String roleUser) {
        this.name = roleUser;
    }
}