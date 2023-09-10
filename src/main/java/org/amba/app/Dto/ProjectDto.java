package org.amba.app.Dto;



import lombok.Data;


import java.util.UUID;


@Data
public class ProjectDto {


    private UUID id;

    private String projectName;


    private byte[] image;


}
