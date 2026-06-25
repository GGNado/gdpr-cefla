package com.cefla.iot.gdpr.dto.response.user;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
public class UtenteFindDTO {
    private Long id;

    @NotBlank
    @Size(max = 50)
    private String username;

    @NotBlank
    @Size(max = 100)
    @Email
    private String email;

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    private Boolean enabled;
    private Boolean accountNonExpired;
    private Boolean accountNonLocked;
    private Boolean credentialsNonExpired;

    private Set<RuoloFindDTO> roles;
}