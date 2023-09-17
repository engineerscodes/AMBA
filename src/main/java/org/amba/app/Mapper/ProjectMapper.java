package org.amba.app.Mapper;

import org.amba.app.Dto.ProjectDto;
import org.amba.app.Entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class );


    @Mapping(source = "imageData", target = "image")
    ProjectDto ProjectToProjectSto(Project p);


    List<ProjectDto> ProjectsToProjectsDto(List<Project> p);

    List<ProjectDto> ProjectToProjectsNoImageDto(List<Project> p);

}
