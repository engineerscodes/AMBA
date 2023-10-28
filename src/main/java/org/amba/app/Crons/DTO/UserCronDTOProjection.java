package org.amba.app.Crons.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.amba.app.Util.Role;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class UserCronDTOProjection {

    private String email;

    List<UUID> questionsCompleted;

    private Role role;

}
