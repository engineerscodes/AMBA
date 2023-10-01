package org.amba.app.Repo;


import org.amba.app.Dto.QuestionDTO;
import org.amba.app.Entity.Project;
import org.amba.app.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepo  extends  JpaRepository<Question, UUID> {
    @Query(value = "select  new org.amba.app.Dto.QuestionDTO(q.questionID , q.question , q.options , q.answerID) from Question q where project = :project")
    List<QuestionDTO> findProjectedByProject(@Param("project") Project p);

    List<QuestionDTO> findByProject(Project p);
}
