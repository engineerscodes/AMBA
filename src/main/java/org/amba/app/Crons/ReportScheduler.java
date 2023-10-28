package org.amba.app.Crons;


import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.amba.app.Crons.DTO.ProjectCronDTO;
import org.amba.app.Crons.DTO.QuestionCount;
import org.amba.app.Crons.DTO.UserCronDTOProjection;
import org.amba.app.Dto.ReportDTO;
import org.amba.app.Entity.Report;
import org.amba.app.Mapper.AdminReportMapper;
import org.amba.app.Repo.ProjectRepo;
import org.amba.app.Repo.QuestionRepo;
import org.amba.app.Repo.ReportAdminRepo;
import org.amba.app.Repo.UserRepo;
import org.amba.app.Service.ReportService;
import org.amba.app.Util.Doc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ReportScheduler {

    @Autowired
    ProjectRepo projectRepo;

    @Autowired
    QuestionRepo questionRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    ReportAdminRepo reportAdminRepo;

    @Autowired
    AdminReportMapper adminReportMapper;

    @Autowired
    ReportService reportService;


    //@Scheduled(cron = "0 30 22 * * *") // will run every day at 10:30
    // unlike fixedRate : fixedDelay waits for first cron to complete then starts next - to prevent excel from corruption
    @Scheduled(fixedDelay=5000)
    @Transactional
    public void generateReport() throws IOException {
        log.info("The report generation started {}",new Date());
        Doc document = reportService.generateReport();

        List<ProjectCronDTO> projects = projectRepo.findAllProjects();
        log.info("Found {} projects in DB ",projects.size());

        List<UserCronDTOProjection> users = userRepo.findAllProjection();
        log.info("Found {} users in DB",users.size());

        log.info("Removing the old reports ");
        reportAdminRepo.deleteAll();

        users.parallelStream().forEach(user ->{
            final List<UUID> questionUUID = user.getQuestionsCompleted()==null?new ArrayList<>():user.getQuestionsCompleted();
            log.info("User {} has completed {} question ",user.getEmail(),questionUUID.size());
            // get count of answered questions
            List<QuestionCount> questionCountByProjects = questionRepo.getCountByProjects(questionUUID);
            questionCountByProjects.forEach(e->{
            Optional<Long> totalQuestion = questionRepo.noOfQuestionByProject(e.getId());
            List<BigInteger>  questionNumber = questionRepo.findAllQuestionNumber(questionUUID,e.getId());
            ReportDTO reportDTO = adminReportMapper.from(e,user,questionNumber, LocalDateTime.now(),totalQuestion.orElse(0L));
            reportAdminRepo.save(adminReportMapper.fromDTO(reportDTO));
            reportService.addRow(document.getWorkbook(),document.getSheet(),document.getCreateHelper(),reportDTO);
            });

            // For Projects where User haven't answered
            Set<UUID> answeredProjects = questionCountByProjects.stream().map(QuestionCount::getId).collect(Collectors.toSet());
            projects.parallelStream().forEach(e -> {
                if(!answeredProjects.contains(e.getId())){
                    reportAdminRepo.save(Report.builder().Project(e.getProjectName())
                            .type(e.getType())
                            .email(user.getEmail()).questionNumber(null)
                                    .score(0).totalQuestions(0)
                            .role(user.getRole().toString()).reportDateTime(LocalDateTime.now())
                            .build());
                    reportService.addRow(document.getWorkbook(),document.getSheet(),document.getCreateHelper(),ReportDTO.builder().project(e.getProjectName())
                            .type(e.getType())
                            .email(user.getEmail()).questionNumber(new ArrayList<>())
                            .score(0).totalQuestions(0)
                            .role(user.getRole().toString()).reportDate(LocalDateTime.now())
                            .build());
                }
            });

        } );
        reportService.save(document.getWorkbook(),document.getSheet());
        log.info("The report generation completed {}", new Date());
    }
}

/*
 *
 *  projects.parallelStream().forEach(project -> {
 *             List<QuestionCronDTO>  questions = questionRepo.findByProjectUuid(project.getId());
 *             log.info("Found {} questions for Project {} ",questions.size(), project.getProjectName());
 *             // find user who completed -> question
 *         });
 */
