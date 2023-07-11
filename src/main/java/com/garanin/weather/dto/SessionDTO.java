package com.garanin.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

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

    @OneToOne ()
    @Cascade({org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.REMOVE})
    @JoinColumn(name = "userId")
    private UserDTO userId;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
}