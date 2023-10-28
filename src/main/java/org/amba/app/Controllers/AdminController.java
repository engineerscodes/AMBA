package org.amba.app.Controllers;


import org.amba.app.Dto.QuestionDTO;
import org.amba.app.Dto.ReportDTO;
import org.amba.app.Dto.UserDTO;
import org.amba.app.Entity.Report;
import org.amba.app.Entity.User;
import org.amba.app.Repo.ReportAdminRepo;
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
import org.springframework.util.Assert;
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

    @Autowired
    ReportAdminRepo reportAdminRepo;

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

    @PutMapping(value = "/edit/question")
    private ResponseEntity<QuestionDTO> changeQuestion(){
        // check if question exists
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try{
            if(authentication.isAuthenticated()){
                User user = (User) authentication.getPrincipal();
                Assert.isTrue(user.getRole() != null && user.getRole().equals(Role.ADMIN),"Only Admin allowed ");
                System.out.println(user.getRole());
            }
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(null);
        }

        return ResponseEntity.ok().body(null);
    }

    @GetMapping("/user/{uuid}/question/answer")
    private ResponseEntity<Object> getAnswerByUser(){
        return null;
    }

    @GetMapping("report")
    private ResponseEntity<List<Report>> getReport(){
      return ResponseEntity.ok(reportAdminRepo.findAll());
    }


   // @GetMapping("/report")
   // private ResponseEntity<List<ReportDTO>>


    @PutMapping("/user/{uuid}/question/answer/remove")
    private ResponseEntity<Object> removeAnswerOfUser(@RequestParam("ProjectUUID") String uuid){
        return null;
    }

}
