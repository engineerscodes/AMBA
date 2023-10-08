package org.amba.app.Controllers;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.amba.app.Dto.ProjectDto;
import org.amba.app.Dto.ProjectNoImageDto;
import org.amba.app.Entity.Project;
import org.amba.app.Mapper.ProjectMapper;
import org.amba.app.Repo.ProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/project")
public class ProjectController {


    @Autowired
    ProjectRepo projectRepo;

    @Autowired
    ProjectMapper projectMapper;

    @GetMapping("/list")
    private ResponseEntity<List<ProjectDto>> getAllProjects(){
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(projectMapper.ProjectsToProjectsDto(projectRepo.findAll()));
    }

    @GetMapping("/{id}")
    private ResponseEntity<HashMap<String,String>> findProject(@PathVariable("id") String projectId){
        HashMap<String,String> message = new HashMap<>();
         try {
             Optional<Project> p = projectRepo.findById(UUID.fromString(projectId));

             if (p.isEmpty()) { message.put("message","Invalid Project Id");return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(message);}
             message.put("message","valid project Id");
             message.put("projectName",p.get().getProjectName());
             return ResponseEntity.ok(message);
         }catch (Exception e){
             message.put("message","Invalid Project Id : UUID string too large");
             return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(message);
         }
    }


    @GetMapping("/list/noImage")
    private ResponseEntity<List<ProjectNoImageDto>> getAllProjectsNoImage(){
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(projectMapper.ProjectToProjectsNoImageDto(projectRepo.findAll()));
    }



    /**
     * Endpoint to add new Projects - only admin allowed
     */
    @PostMapping("/new")
    private ResponseEntity<String> newProject(@RequestPart("projectName") String name, @RequestPart("image") MultipartFile image) {
        try {
        if(name.isEmpty()) return  ResponseEntity.badRequest().body("Invalid Project Name");
        Project newPrj = new Project(name,image.getBytes());
        projectRepo.save(newPrj);
        }catch (IOException e){
            ResponseEntity.badRequest().body("Try again,Image upload failed !!");
        }
        return ResponseEntity.ok(name);
    }

    @PutMapping ("/update")
    private String updateProject(@RequestParam(name = "project") String prjID){
        return prjID;
    }

}
