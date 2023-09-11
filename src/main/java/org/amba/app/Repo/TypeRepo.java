package org.amba.app.Repo;


import org.amba.app.Entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TypeRepo extends JpaRepository<Type, UUID> {
}
