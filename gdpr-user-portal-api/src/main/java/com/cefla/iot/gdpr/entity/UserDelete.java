package com.cefla.iot.gdpr.entity;

import lombok.*;
import jakarta.persistence.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "portale_utente")
public class UserDelete {
    @Id
    @Column(name = "IdUtente")
    private String id;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "Nome")
    private String nome;

    @Column(name = "Cognome")
    private String cognome;

    @Column(name = "Email")
    private String email;
}