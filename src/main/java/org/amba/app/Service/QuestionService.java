package org.amba.app.Service;


import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.amba.app.Dto.QuestionDTO;
import org.amba.app.Entity.Project;
import org.amba.app.Entity.Question;
import org.amba.app.Entity.User;
import org.amba.app.Repo.ProjectRepo;
import org.amba.app.Repo.QuestionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class QuestionService {


    @Autowired
    private ProjectRepo projectRepo;

    @Autowired
    private QuestionRepo questionRepo;

    @PersistenceContext
    private EntityManager entityManager;



    public Project checkValidProject(String projectID){
        Optional<Project> p;
        try {
            p = projectRepo.findById(UUID.fromString(projectID));
            return p.orElse(null);
        }catch (Exception e){
            return null;
        }

    }


    @Transactional
    public Question addNewQuestion(Question question){
        questionRepo.saveAndFlush(question);
        entityManager.refresh(question); //need because question number will be null if not refreshed
        return question;
    }

    @Transactional
    public List<QuestionDTO> getAllQuestion(Project p, Pageable pageable){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        if(user.getQuestionsCompleted()==null){
            return questionRepo.findRandomByProjectFirstTime(p,pageable);
        }
        List<UUID> list = user.getQuestionsCompleted();
        return questionRepo.findRandomByProject(p,pageable,list);
    }

    public Optional<Question> findQuestionByUuid(UUID questionID){
        return questionRepo.findById(questionID);
    }




}
