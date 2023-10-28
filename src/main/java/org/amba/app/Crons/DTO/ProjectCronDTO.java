package org.amba.app.Crons.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.amba.app.Entity.Type;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ProjectCronDTO {

    private UUID id;

    private String projectName;

    private String type;

}
