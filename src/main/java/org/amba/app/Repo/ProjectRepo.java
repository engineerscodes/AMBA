package org.amba.app.Repo;


import org.amba.app.Crons.DTO.ProjectCronDTO;
import org.amba.app.Entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepo extends JpaRepository<Project, UUID> {

    @Query("select new org.amba.app.Crons.DTO.ProjectCronDTO(p.id , p.projectName , p.type.type) from Project p")
    List<ProjectCronDTO> findAllProjects();

}
