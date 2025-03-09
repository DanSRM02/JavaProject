package com.oxi.software.business;

import com.oxi.software.dto.*;
import com.oxi.software.entity.User;
import com.oxi.software.service.AuthService;
import com.oxi.software.service.UserService;
import com.oxi.software.utilities.types.Util;
import com.oxi.software.utilities.exception.CustomException;
import com.oxi.software.utilities.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class AuthBusiness  {

    private final IndividualBusiness individualBusiness;
    private final UserBusiness userBusiness;
    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    // Valor por defecto en caso de que no se envíe rol (por ejemplo, rol de cliente)
    private static final Long DEFAULT_ROL_TYPE_ID = 2L;
    private final UserService userService;

    public AuthBusiness(IndividualBusiness individualBusiness, UserBusiness userBusiness, AuthService authService, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder, UserService userService) {
        this.individualBusiness = individualBusiness;
        this.userBusiness = userBusiness;
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    private AuthDTO validateData(Map<String, Object> request){
        AuthDTO authDTO = new AuthDTO();
        JSONObject data = Util.getData(request);
        authDTO.setUsername(data.getString("username"));
        authDTO.setPassword(data.getString("password"));
        return authDTO;
    }

    public void changePassword(Map<String, Object> request, Long id){
        AuthDTO authDTO = validateData(request);

        User user = userService.findBy(id);

        if (user == null) {
            throw new BadCredentialsException("User not found");
        }

        user.setUsername(authDTO.getUsername());
        user.setPassword(passwordEncoder.encode(authDTO.getPassword()));

        userService.save(user);
    }

    @Transactional
    public void register(Map<String, Object> request) {
        // 1. Crear Individual y obtener su DTO
        IndividualDTO individualDTO = individualBusiness.add(request);

        // 2. Construir el UserDTO a partir del IndividualDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(individualDTO.getEmail());
        userDTO.setPassword(passwordEncoder.encode(individualDTO.getDocument())); // documento ya hasheado
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

    public AuthResponseDTO loginUser (Map<String, Object> json){
        try{
            AuthDTO authDTO = validateData(json);

            String username = authDTO.getUsername();
            String password = authDTO.getPassword();

            Authentication authentication = this.authentication(username, password);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = authService.findByUsername(username);
            Long userId = user.getId();
            String accessToken = jwtTokenProvider.createToken(authentication, userId);

            return new AuthResponseDTO(
                    accessToken,
                    "",
                    username
            );
        } catch (CustomException ce){
            throw new CustomException("Invalid credentials provided.", HttpStatus.BAD_REQUEST);
        }
    }

    public Authentication authentication(String username, String password){

        UserDetails userFound = userFinder(username);

        if(userFound == null){
            throw new BadCredentialsException("Invalid username or password");
        }

        if (!passwordEncoder.matches(password, userFound.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(username, password, userFound.getAuthorities());
    }

    protected UserDetails userFinder(String username){

        if (username == null) {
            throw new CustomException("The document provided must not be null", HttpStatus.NO_CONTENT);
        }

        boolean existsUser = authService.existsUser(username);

        if (existsUser){
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            User user = authService.findByUsername(username);

            authorities.add(new SimpleGrantedAuthority("ROLE_".concat(user.getRolType().getName())));

            user.getRolType().getPermissions()
                    .forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName())));

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.isEnabled(),
                    user.isAccountNoExpired(),
                    user.isCredentialsNoExpired(),
                    user.isAccountNoLocked(),
                    authorities
            );
        }
        return null ;
    }

}