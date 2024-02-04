package org.amba.app.Repo;


import org.amba.app.Crons.DTO.QuestionCount;
import org.amba.app.Crons.DTO.QuestionCronDTO;
import org.amba.app.Dto.QuestionDTO;
import org.amba.app.Entity.Project;
import org.amba.app.Entity.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

@Repository
public interface QuestionRepo  extends  JpaRepository<Question, UUID> {
    @Query(value = "select  new org.amba.app.Dto.QuestionDTO(q.questionID , q.question , q.options , q.answerID) from Question q where project = :project")
    List<QuestionDTO> findProjectedByProject(@Param("project") Project p);

    List<QuestionDTO> findByProject(Project p);

    @Query(value = "select count(*) from Question q where q.fk_project_uuid = :projectID",nativeQuery = true)
    Optional<Long> noOfQuestionByProject(@Param("projectID") UUID projectID);

    @Query(value = "select q.questionid from Question q where q.fk_project_uuid = :projectID",nativeQuery = true)
    @Transactional
    List<UUID> userAnsweredQuestions(@Param("projectID") UUID projectID);

    @Query(value = "select q.question_number from Question q where q.questionID in :questionId and q.fk_project_uuid = :projectID",nativeQuery = true)
    List<BigInteger> findAllQuestionNumber(@Param("questionId") List<UUID> questionID,@Param("projectID") UUID projectID);

    @Query(value = "select q.questionID as questionID ,q.question_number as questionNumber from Question q where q.fk_project_uuid = :id" ,nativeQuery = true)
    List<QuestionCronDTO> findByProjectUuid(@Param("id") UUID uuid);

    @Query(value = "select new org.amba.app.Crons.DTO.QuestionCount" +
            "(count(*),q.project.id,q.project.projectName,q.project.type.type)" +
            " from Question q where q.questionID in :uuid group by q.project.id,q.project.projectName,q.project.type.type")
    List<QuestionCount>  getCountByProjects(@Param("uuid") List<UUID> UUID);

    @Query(value = "select  new org.amba.app.Dto.QuestionDTO(q.questionID , q.question , q.options , q.answerID, q.questionNumber , q.questionText) " +
            "from Question q where project = :project and q.questionID not in :questionIDs ORDER BY RANDOM() ")
    List<QuestionDTO> findRandomByProject(@Param("project") Project p,Pageable pageable,@Param("questionIDs") List<UUID> questionUuid);

    @Query(value = "select  new org.amba.app.Dto.QuestionDTO(q.questionID , q.question , q.options , q.answerID, q.questionNumber , q.questionText) " +
            "from Question q where project = :project")
    List<QuestionDTO> findRandomByProjectFirstTime(@Param("project") Project p,Pageable pageable);
}
