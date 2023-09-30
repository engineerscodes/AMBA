package org.amba.app.Controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.amba.app.Entity.Project;
import org.amba.app.Entity.Question;
import org.amba.app.Entity.Type;
import org.amba.app.Repo.ProjectRepo;
import org.amba.app.Repo.TypeRepo;
import org.amba.app.Service.QuestionService;
import org.amba.app.Util.Options;
import org.antlr.v4.runtime.misc.NotNull;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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


    @GetMapping("/{id}/all")
    private ResponseEntity<Question> getAllQuestion(@PathVariable("id") String projectId){

        Project p = questionService.checkValidProject(projectId);
        if(p == null) return  ResponseEntity.badRequest().body(null);
        Type projectType = p.getType();
        log.info("Project : {} ",projectType);

        return ResponseEntity.ok().body(null);
    }

    @PostMapping(value = "/new", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    private ResponseEntity<Object> addQuestion(@RequestPart("projectId")  String prjID, @RequestPart("QuestionImage") MultipartFile questionImage,
                                      @RequestPart("answer_id") String answer_index,
                                      @RequestPart(name = "options") List<Options> options) throws IOException {
        long answer_id;
        Project p = questionService.checkValidProject(prjID);
        if(p == null) return null;
        try {
            answer_id = Long.parseLong(answer_index);
        }
        catch (NumberFormatException e){return ResponseEntity.badRequest().body("Invalid Answer Index");}
        System.out.println(options.size()+" -----"+answer_id);
        if(answer_id>=options.size())
            return ResponseEntity.badRequest().body("Answer Index is greater than no of options");
        Question question =  new Question(UUID.randomUUID(),p,questionImage.getBytes(),options,answer_id);
        return ResponseEntity.ok(questionService.addNewQuestion(question));
    }

}
