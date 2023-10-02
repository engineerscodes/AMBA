package org.amba.app.Service;


import org.amba.app.Dto.QuestionDTO;
import org.amba.app.Entity.Project;
import org.amba.app.Entity.Question;
import org.amba.app.Repo.ProjectRepo;
import org.amba.app.Repo.QuestionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuestionService {


    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private QuestionRepo questionRepo;


    public Project checkValidProject(String projectID){
        Optional<Project> p;
        try {
            p = projectRepo.findById(UUID.fromString(projectID));
            return p.orElse(null);
        }catch (Exception e){
            return null;
        }

    }


    public Question addNewQuestion(Question question){
        return questionRepo.save(question);
    }

    @Transactional
    public List<QuestionDTO> getAllQuestion(Project p, Pageable pageable){
       return questionRepo.findRandomByProject(p,pageable);
    }




}
