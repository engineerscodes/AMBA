package org.amba.app.Controllers;


import org.amba.app.Dto.QuestionDTO;
import org.amba.app.Entity.Project;
import org.amba.app.Entity.Question;
import org.amba.app.Entity.Type;
import org.amba.app.Entity.User;
import org.amba.app.Repo.ProjectRepo;
import org.amba.app.Repo.QuestionRepo;
import org.amba.app.Repo.TypeRepo;
import org.amba.app.Repo.UserRepo;
import org.amba.app.Service.QuestionService;
import org.amba.app.Service.UserService;
import org.amba.app.Util.Options;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

import org.slf4j.Logger;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/Question")
public class QuestionController {


    Logger log = LoggerFactory.getLogger(QuestionController.class);


    @Autowired
    ProjectRepo projectRepo;

    @Autowired
    TypeRepo typeRepo;

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;


    @GetMapping("/{id}/all")
    private ResponseEntity<List<QuestionDTO>> getAllQuestion(@PathVariable("id") String projectId,
                                                             @RequestParam(defaultValue = "0") int page,
                                                             @RequestParam(defaultValue = "5") int size){

        Project p = questionService.checkValidProject(projectId);
        if(p == null) return  ResponseEntity.badRequest().body(null);
        Type projectType = p.getType();
        log.info("Project : {} ",projectType);
        Pageable pageable = PageRequest.of(page, size);
        List<QuestionDTO> questionList = questionService.getAllQuestion(p,pageable);

        return ResponseEntity.ok().body(questionList);
    }
    @PutMapping("/{id}/submit")
    private  ResponseEntity<Object> submitAnswer(@PathVariable("id") String QuestionID ,
                                                 @RequestParam(name = "answerId") int answerIndex){
       try {
           Optional<Question> questionOp = questionService.findQuestionByUuid(UUID.fromString(QuestionID));
           if(questionOp.isEmpty()) return  ResponseEntity.badRequest().body("No Question Uuid Found");
           Question question = questionOp.get();
           if(answerIndex>=question.getOptions().size()) return ResponseEntity.badRequest().body("AnswerIndex greater than no of options");
           if(question.getAnswerID()!=answerIndex) return ResponseEntity.badRequest().body("Not correct answer");
           Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
           if(authentication.isAuthenticated()){
               User user = (User) authentication.getPrincipal();
               List<UUID> list = user.getQuestionsCompleted()==null? new ArrayList<UUID>():user.getQuestionsCompleted();
               if(list.parallelStream().anyMatch(e-> e.equals(question.getQuestionID()))){
                   return ResponseEntity.badRequest().body("Question is already submitted");
               }
               list.add(question.getQuestionID());
               user.setQuestionsCompleted(list);
               user = userService.saveUser(user);
               log.info("User {} submitted answer {} for question {}",user.getUserId(),answerIndex,QuestionID);
               return ResponseEntity.ok(user.getQuestionsCompleted());
           }else
               return ResponseEntity.badRequest().body("No User Found Authenticated");
       }catch (Exception e){
           System.out.println(e);
           return ResponseEntity.badRequest().body("Error in saving Project check Project UUID or Saving Question Problem");
       }
    }

    @PostMapping(value = "/new", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    private ResponseEntity<Object> addQuestion(@RequestPart("projectId")  String prjID, @RequestPart("QuestionImage") MultipartFile questionImage,
                                      @RequestPart("answer_id") String answer_index,
                                      @RequestPart(name = "options") List<Options> options , @RequestPart(value = "question_text",required = false) String question_text) throws IOException {
        long answer_id;
        if(prjID==null || prjID.isEmpty()) return ResponseEntity.badRequest().body(null);
        Project p = questionService.checkValidProject(prjID);
        if(p == null) return ResponseEntity.badRequest().body(null);
        try {
            answer_id = Long.parseLong(answer_index);
        }
        catch (NumberFormatException e){return ResponseEntity.badRequest().body("Invalid Answer Index");}
        System.out.println(options.size()+" -----"+answer_id);
        if(answer_id>=options.size())
            return ResponseEntity.badRequest().body("Answer Index is greater than no of options");
       // Question question =  new Question(UUID.randomUUID(),p,questionImage.getBytes(),options,answer_id);
        Question question = Question.builder().project(p).questionText(question_text).question(questionImage.getBytes()).options(options).answerID(answer_id).build();
        return ResponseEntity.ok(questionService.addNewQuestion(question));
    }

}
