package org.amba.app.Controllers;


import org.amba.app.Entity.Type;
import org.amba.app.Repo.TypeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/projects/types")
public class ProjectTypeController {

    @Autowired
    TypeRepo typeRepo;

    @GetMapping("/list")
    private List<Type> getAllTypes(){
      return  typeRepo.findAll();
    }

    @PostMapping("/new")
    private ResponseEntity<Object> addNewType(@RequestBody() String newType){

        if(newType== null)return ResponseEntity.badRequest().body("TYPE CAN'T BE NULL");

        if(typeRepo.findByType(newType).isPresent()) {
            return ResponseEntity.badRequest().body(String.format("TYPE WITH NAME  %s ALREADY EXIST", newType));
        }
        typeRepo.save(Type.builder().type(newType.trim()).build());
        return ResponseEntity.ok(newType);
    }

}
