package org.amba.app.Service;


import org.amba.app.Controllers.QuestionController;
import org.amba.app.Dto.AuthenticationResponseDTO;
import org.amba.app.Dto.LoginUserDTO;
import org.amba.app.Dto.NewUserDto;
import org.amba.app.Entity.User;
import org.amba.app.Repo.UserRepo;
import org.amba.app.Security.JwtService;
import org.amba.app.Util.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    Logger log = LoggerFactory.getLogger(AuthenticationService.class);


    public AuthenticationResponseDTO signUp(NewUserDto newUserDto) {
        Optional<User> new_user = userRepo.findByEmail(newUserDto.getEmail());
        if(new_user.isPresent()) throw new RuntimeException("User Exist with Email Already");

        User user = User.builder()
                .email(newUserDto.getEmail())
                .name(newUserDto.getName())
                .password(passwordEncoder.encode(newUserDto.getPassword()))
                .role(Role.USER)
                .build();
        String jwtToken = jwtService.generateToken(user);
        userRepo.save(user);
        log.info("New User created with Email ID : {}",newUserDto.getEmail());
        return AuthenticationResponseDTO.builder()
                .token(jwtToken).build();

    }

    public AuthenticationResponseDTO login(LoginUserDTO loginUserDTO) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginUserDTO.getEmail(),
                loginUserDTO.getPassword()
        ));

        User user = userRepo.findByEmail(loginUserDTO.getEmail())
                .orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        return AuthenticationResponseDTO.builder()
                .token(jwtToken).build();
    }
}
