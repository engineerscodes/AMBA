package org.amba.app.Repo;


import org.amba.app.Entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportAdminRepo extends JpaRepository<Report,Long> {
}
