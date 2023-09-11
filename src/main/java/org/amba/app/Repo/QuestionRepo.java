package org.amba.app.Repo;


import org.amba.app.Entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QuestionRepo  extends  JpaRepository<Question, UUID> {
}
