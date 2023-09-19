package org.amba.app.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.UUID;


@Data
public class ProjectNoImageDto {

    private UUID id;

    private String projectName;

    @JsonIgnore
    private byte[] image;

}
