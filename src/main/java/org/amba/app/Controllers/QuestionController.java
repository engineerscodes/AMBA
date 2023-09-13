package org.amba.app.Controllers;


import org.amba.app.Entity.Project;
import org.amba.app.Entity.Question;
import org.amba.app.Entity.Type;
import org.amba.app.Repo.ProjectRepo;
import org.amba.app.Repo.TypeRepo;
import org.amba.app.Service.QuestionService;
import org.amba.app.Util.Options;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
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


    @GetMapping("/{id}/all")
    private ResponseEntity<Question> getAllQuestion(@PathVariable("id") String projectId){

        Project p = questionService.checkValidProject(projectId);
        if(p == null) return  ResponseEntity.badRequest().body(null);
        Type projectType = p.getType();
        log.info("Project : {} ",projectType);

        return ResponseEntity.ok().body(null);
    }

    @PostMapping("/new")
    private Question addQuestion(@RequestPart("projectId") String prjID, @RequestPart("QuestionImage") MultipartFile questionImage,
    @RequestPart("options") List<Options> options , @RequestPart("answer_id") Long answer_id) throws IOException {

        Project p = questionService.checkValidProject(prjID);
        if(p == null) return null;
        Question question =  new Question(UUID.randomUUID(),p,questionImage.getBytes(),options,answer_id);
        return questionService.addNewQuestion(question);
    }

}
