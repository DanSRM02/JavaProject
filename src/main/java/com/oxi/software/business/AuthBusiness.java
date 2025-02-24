package com.oxi.software.business;

import com.oxi.software.dto.IndividualDTO;
import com.oxi.software.dto.RolTypeDTO;
import com.oxi.software.dto.UserDTO;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AuthBusiness  {

    private final IndividualBusiness individualBusiness;
    private final UserBusiness userBusiness;

    // Valor por defecto en caso de que no se envíe rol (por ejemplo, rol de cliente)
    private static final Long DEFAULT_ROL_TYPE_ID = 1L;

    public AuthBusiness(IndividualBusiness individualBusiness, UserBusiness userBusiness) {
        this.individualBusiness = individualBusiness;
        this.userBusiness = userBusiness;
    }

    @Transactional
    public void register(Map<String, Object> request) {
        // 1. Crear Individual y obtener su DTO
        IndividualDTO individualDTO = individualBusiness.add(request);

        // 2. Construir el UserDTO a partir del IndividualDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(individualDTO.getEmail());
        userDTO.setPassword(individualDTO.getDocument()); // documento ya hasheado
        userDTO.setState(true);

        // 3. Extraer el rol enviado en el request

        Map<String, Object> data = (Map<String, Object>) request.get("data");
        Long rolTypeId = DEFAULT_ROL_TYPE_ID;
        if (data != null && data.get("rol_type") != null) {
            rolTypeId = Long.parseLong(data.get("rol_type").toString());
        }

        // 4. Asignar el rol al UserDTO
        RolTypeDTO rolTypeDTO = new RolTypeDTO();
        rolTypeDTO.setId(rolTypeId);
        userDTO.setRolType(rolTypeDTO);

        // 5. Asignar el individual creado
        userDTO.setIndividual(individualDTO);

        // 6. Crear el usuario
        userBusiness.add(userDTO);
    }

    public void validateCredentials(Map<String, Object> json) {

    }

}
