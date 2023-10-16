package org.amba.app.Controllers;

import org.amba.app.Dto.AuthenticationResponseDTO;
import org.amba.app.Dto.LoginUserDTO;
import org.amba.app.Dto.NewUserDto;
import org.amba.app.Service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {


    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/signUp")
    private ResponseEntity<AuthenticationResponseDTO> signUp(@RequestBody NewUserDto newUserDto){
        try {
            return ResponseEntity.ok(authenticationService.signUp(newUserDto));
        }catch (Exception e)
        {
            return ResponseEntity.badRequest().body(AuthenticationResponseDTO.builder().message(e.getMessage()).build());
        }
    }

    @PostMapping("/login")
    private ResponseEntity<AuthenticationResponseDTO> login(@RequestBody LoginUserDTO loginUserDTO){
        return ResponseEntity.ok(authenticationService.login(loginUserDTO));
    }


}
