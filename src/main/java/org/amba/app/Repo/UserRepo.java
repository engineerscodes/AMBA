package org.amba.app.Repo;

import org.amba.app.Crons.DTO.UserCronDTOProjection;
import org.amba.app.Dto.UserDTO;
import org.amba.app.Entity.User;
import org.amba.app.Util.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepo extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserIdAndEmail(UUID uuid,String email);

    List<UserDTO> findByRole(Role role);

    @Query("select new org.amba.app.Crons.DTO.UserCronDTOProjection(u.email,u.questionsCompleted,u.role) from User u")
    List<UserCronDTOProjection> findAllProjection();
}
