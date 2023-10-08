package org.amba.app.Controllers;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.amba.app.Dto.ProjectDto;
import org.amba.app.Dto.ProjectNoImageDto;
import org.amba.app.Entity.Project;
import org.amba.app.Entity.Type;
import org.amba.app.Mapper.ProjectMapper;
import org.amba.app.Repo.ProjectRepo;
import org.amba.app.Repo.TypeRepo;
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
    TypeRepo typeRepo;

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
    private ResponseEntity<String> newProject(@RequestPart("projectName") String name, @RequestPart("image") MultipartFile image,
    @RequestPart("ProjectType") String projectType) {
        try {
        if(name.isEmpty()) return  ResponseEntity.badRequest().body("Invalid Project Name");
        Optional<Type> type = typeRepo.findById(UUID.fromString(projectType));
        if(type.isEmpty()) return ResponseEntity.badRequest().body("Project Type not found");
        Project newPrj = Project.builder().projectName(name).imageData(image.getBytes()).type(type.get()).build(); //new Project(name,image.getBytes(),type.get());
        projectRepo.save(newPrj);
        }catch(IllegalArgumentException | IOException ignored){
           return ResponseEntity.badRequest().body("Try again,Image upload failed !! or check UUID Of the ProjectType");
        }catch (Exception e){
            return ResponseEntity.badRequest().body("Saving Project failed due to bad payload eg check Project Name");
        }
        return ResponseEntity.ok(name);
    }

    @PutMapping ("/update")
    private String updateProject(@RequestParam(name = "project") String prjID){
        return prjID;
    }

}
