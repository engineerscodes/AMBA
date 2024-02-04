package org.amba.app.Controllers;



import org.amba.app.Dto.QuestionDTO;
import org.amba.app.Dto.UserDTO;
import org.amba.app.Entity.*;
import org.amba.app.Repo.QuestionAuditRepo;
import org.amba.app.Repo.QuestionRepo;
import org.amba.app.Repo.ReportAdminRepo;
import org.amba.app.Repo.UserRepo;
import org.amba.app.Security.JwtService;
import org.amba.app.Service.BatchUploadService;
import org.amba.app.Util.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

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

    @Autowired
    BatchUploadService batchUploadService;

    @Autowired
    QuestionAuditRepo questionAuditRepo;

    @Autowired
    QuestionRepo questionRepo;


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


    @GetMapping("/getUserByType") //pagination needed
    private ResponseEntity<Object> getUsers(@RequestParam String role,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "50") int size){
        try {
            Role userRole = Role.valueOf(role);
            Pageable pageable = PageRequest.of(page, size);
            List<UserDTO> users =userRepo.findByRole(userRole,pageable);
            Assert.isTrue(!users.isEmpty(),"No more User found with Role "+role);
            return ResponseEntity.ok(users);
        }catch (Exception e){
            log.error("No role found of type : {}",role);
            return ResponseEntity.badRequest().body(Map.of("error",e.getMessage()));
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

    @GetMapping("report")  // pagination needed
    private ResponseEntity<Page<Report>> getReport(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "50") int size){
      Pageable pageable = PageRequest.of(page, size);
      return ResponseEntity.ok(reportAdminRepo.findAll(pageable));
    }


    @PutMapping("/user/{uuid}/question/answer/remove")
    private ResponseEntity<Object> removeAnswerOfUser(@RequestParam("ProjectUUID") String uuid){
        return null;
    }


    @PostMapping("/ChangeRoles")
    private ResponseEntity<List<Map<String, String>>> ChangeRoles(@RequestBody List<Map<String, String>> userMapList){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            if (authentication.isAuthenticated()) {
                User ReqUser = (User) authentication.getPrincipal();
                userMapList.parallelStream().forEach(userMap -> {
                    if (userMap.get("email").equalsIgnoreCase(ReqUser.getEmail()))
                        throw new RuntimeException("Can't update your role ");
                });
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(List.of(Map.of("Error","Can't Updated yourself your Role")));
        }
        userMapList.parallelStream().forEach(userMap-> {
            String userUuid = userMap.get("userUuid");
            String email = userMap.get("email");
            if(userUuid != null && email!=null){
                Optional<User> userOptional= userRepo.findByUserIdAndEmail(UUID.fromString(userUuid),email);
                if(userOptional.isPresent()){
                    User user = userOptional.get();
                    Role newRole = user.getRole().toString().equalsIgnoreCase("ADMIN")?Role.USER:Role.ADMIN;
                    user.setRole(newRole);
                    userRepo.save(user);
                    userMap.put("newRole",newRole.toString());
                    log.info("Changed User {} Role to {}",email,newRole);
                }
            }
        });
        return ResponseEntity.ok(userMapList);
    }

    @PostMapping("/deleteUsers")
    private ResponseEntity<List<Map<String, String>>> deleteUsers(@RequestBody List<Map<String, String>> userMapList){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            if (authentication.isAuthenticated()) {
                User ReqUser = (User) authentication.getPrincipal();
                userMapList.parallelStream().forEach(userMap -> {
                    if (userMap.get("email").equalsIgnoreCase(ReqUser.getEmail()))
                        throw new RuntimeException("Can't Delete yourself ");
                });
            }
        }catch (Exception e){
            return ResponseEntity.badRequest().body(List.of(Map.of("Error","Can't Updated Delete yourself ")));
        }

        userMapList.parallelStream().forEach(userMap-> {
            String userUuid = userMap.get("userUuid");
            String email = userMap.get("email");
            if(userUuid != null && email!=null){
                Optional<User> userOptional= userRepo.findByUserIdAndEmail(UUID.fromString(userUuid),email);
                if(userOptional.isPresent()){
                    User user = userOptional.get();
                    userRepo.delete(user);
                    userMap.put("deleted","true");
                    log.info("Deleted User with Email {}",email);
                }
            }
        });
        return ResponseEntity.ok(userMapList);
    }

    @PostMapping(value = "/uploadQuestions",consumes ={MediaType.MULTIPART_FORM_DATA_VALUE},produces = "application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    private ResponseEntity<Object> batchUpload(MultipartFile file){
        try {
             return ResponseEntity.ok().body(batchUploadService.validateExcelSheet(file,AdminController.class).toByteArray());
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/QuestionUploaded")
    private List<QuestionAudit> getUploadedQuestions(){
        return questionAuditRepo.findAll().stream().filter(e-> e.getDateTime()!=null)
                .sorted(Comparator.comparing(QuestionAudit::getDateTime)).toList();
    }

    @GetMapping(value = "downloadFile",produces ="application/vnd.openxmlformats-officedocument.wordprocessingml.document")
    private ResponseEntity<Object> downloadFile(@RequestParam long id) throws IOException {
        String fileName = "QuestionUpload_"+id+"_"+".docx";
        File file = new File("src//main//resources//Files//QuestionUpload//"+fileName);
        try (FileInputStream inputStream = new FileInputStream(file)){ // using try with resource for automatically manages the closing of resources
            return ResponseEntity.ok(inputStream.readAllBytes());
        } catch (FileNotFoundException e) {
            return ResponseEntity.internalServerError().body("Can't Find the File with the above name");
        }
    }

    @GetMapping("/massiveUpload/status")
    private List<QuestionAudit> getUploadStatus(){
        return questionAuditRepo.findAll().stream().filter(e->e.getDateTime()!=null).sorted(Comparator.comparing(QuestionAudit::getDateTime)).toList();
    }

    @GetMapping("/answer") //todo Pagination needed
    // instead of doing Pagination just return list of completed Question ID and based on uer selection show question
    private Object getUserAnswer(@RequestParam String email,@RequestParam UUID prjID){
        if(email.isBlank()) return ResponseEntity.badRequest().body("Email Can't be Empty");
        try {
            Optional<User> user = userRepo.findByEmail(email);
            Assert.isTrue(user.isPresent(), "No user Found");
            List<UUID> questionsCompleted = user.get().getQuestionsCompleted();
            if(questionsCompleted == null) return ResponseEntity.ok("No Answer found for the User");
            List<UUID> questionsInProject = questionRepo.userAnsweredQuestions(prjID); //here mostly
            return questionsInProject.stream()
                    .filter(questionsCompleted::contains)
                    .toList();
            //must just return List of question UUID
        }catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/question") //todo get question if user has answered it
    private ResponseEntity<QuestionDTO> getQuestion(){
        return null;
    }

}
