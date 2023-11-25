package org.amba.app.Repo;

import org.amba.app.Entity.QuestionAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionAuditRepo extends JpaRepository<QuestionAudit,Long> {
}
