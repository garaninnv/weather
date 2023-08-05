package com.garanin.weather.dto;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
@Table(name = "sessions")
public class SessionDTO {
    @Id
    @Column(name = "id")
    private UUID id;

    @OneToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "userId")
    private UserDTO userId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}