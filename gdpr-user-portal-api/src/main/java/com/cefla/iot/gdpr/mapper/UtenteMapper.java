package com.cefla.iot.gdpr.mapper;

import com.cefla.iot.gdpr.dto.request.auth.RegisterRequest;
import com.cefla.iot.gdpr.dto.request.utente.UtenteCreateRequestDTO;
import com.cefla.iot.gdpr.dto.request.utente.UtenteUpdateRequestDTO;
import com.cefla.iot.gdpr.dto.response.user.UtenteFindDTO;
import com.cefla.iot.gdpr.entity.auth.Role;
import com.cefla.iot.gdpr.entity.auth.User;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UtenteMapper {

    User convert(UtenteCreateRequestDTO dto);

    User convert(UtenteUpdateRequestDTO dto);

    User convert(RegisterRequest dto);

    User convert(UtenteFindDTO dto);

    UtenteFindDTO conver(User entity);

    List<UtenteFindDTO> convert(List<User> entities);

    // Metodo di mapping personalizzato
    default Set<Role> map(Set<String> value) {
        if (value == null) return null;
        return value.stream()
                .map(roleName -> {
                    Role role = new Role();
                    role.setName(roleName);
                    return role;
                })
                .collect(Collectors.toSet());

    }
}