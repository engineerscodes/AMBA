package org.amba.app.Mapper;


import org.amba.app.Crons.DTO.QuestionCount;
import org.amba.app.Crons.DTO.UserCronDTOProjection;
import org.amba.app.Dto.ReportDTO;
import org.amba.app.Entity.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AdminReportMapper {

    AdminReportMapper INSTANCE = Mappers.getMapper(AdminReportMapper.class);

    @Mapping(source = "question.questionCount",target ="score")
    @Mapping(source = "question.projectName",target ="project")
    @Mapping(source = "question.type",target ="type")
    @Mapping(source = "user.role",target ="role")
    @Mapping(source = "user.email",target = "email")
    @Mapping(source = "dateTime",target = "reportDate")
    ReportDTO from(QuestionCount question, UserCronDTOProjection user, List<BigInteger> questionNumber, LocalDateTime dateTime,long totalQuestions);


    @Mapping(source = "reportDTO.reportDate",target = "reportDateTime")
    @Mapping(source = "reportDTO.project",target = "Project")
    Report fromDTO(ReportDTO reportDTO);

}
