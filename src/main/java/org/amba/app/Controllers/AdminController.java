package org.amba.app.Controllers;


import org.amba.app.Dto.QuestionDTO;
import org.amba.app.Dto.UserDTO;
import org.amba.app.Entity.User;
import org.amba.app.Repo.UserRepo;
import org.amba.app.Security.JwtService;
import org.amba.app.Service.AuthenticationService;
import org.amba.app.Util.Options;
import org.amba.app.Util.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {

    Logger log = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    UserRepo userRepo;

    @Autowired
    JwtService jwtService;

    @PostMapping("/add")
    private ResponseEntity<String> makeUserAdmin(@RequestBody Map<String, String> userMap){
        String userUuid = userMap.get("userUuid");
        String email = userMap.get("email");
        try{
            Optional<User> newAdminUser = userRepo.findByUserIdAndEmail(UUID.fromString(userUuid),email);
            if(newAdminUser.isEmpty()) return ResponseEntity.badRequest().body("No user Found");
            newAdminUser.get().setRole(Role.ADMIN);
            userRepo.save(newAdminUser.get());
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Invalid UUID");
        }
        log.info("Now {} is a admin",email);
        return ResponseEntity.ok(userUuid);
    }

    @PostMapping("/remove")
    private ResponseEntity<String> removeUserAdmin(@RequestBody Map<String, String> userMap){
        String userUuid = userMap.get("userUuid");
        String email = userMap.get("email");
        try{
            Optional<User> newAdminUser = userRepo.findByUserIdAndEmail(UUID.fromString(userUuid),email);
            if(newAdminUser.isEmpty()) return ResponseEntity.badRequest().body("No user Found");
            newAdminUser.get().setRole(Role.USER);
            userRepo.save(newAdminUser.get());
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body("Invalid UUID");
        }
        log.info("Now {} is a admin",email);
        return ResponseEntity.ok(userUuid);
    }


    @GetMapping("/getUserByType")
    private ResponseEntity<List<UserDTO>> getUsers(@RequestParam String role){
        try {
            Role userRole = Role.valueOf(role);
            return ResponseEntity.ok(userRepo.findByRole(userRole));
        }catch (Exception e){
            log.error("No role found of type : {}",role);
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping(value = "/edit/question", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    private ResponseEntity<QuestionDTO> changeQuestion(@RequestPart("projectId")  String prjID, @RequestPart("QuestionImage") MultipartFile questionImage,
                                                       @RequestPart("answer_id") String answer_index,
                                                       @RequestPart(name = "options") List<Options> options , @RequestPart(value = "question_text",required = false) String question_text){
        // check if question exists
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            if(authentication.isAuthenticated()){
                User user = (User) authentication.getPrincipal();
                assert user.getRole() != null && user.getRole().equals(Role.ADMIN);
            }
        }catch (AssertionError e){
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping("/user/{uuid}/answer")
    private ResponseEntity<Object> getAnswerByUser(){
        return null;
    }

}
