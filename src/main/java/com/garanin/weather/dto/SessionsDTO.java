package com.garanin.weather.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter@NoArgsConstructor
@Table(name = "sessions")
public class SessionsDTO {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "user_id")
    private int userId;
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}
