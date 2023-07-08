package com.garanin.weather.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sessions")
public class SessionDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @OneToOne ()
    @JoinColumn(name = "userId")
    private UserDTO userId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
