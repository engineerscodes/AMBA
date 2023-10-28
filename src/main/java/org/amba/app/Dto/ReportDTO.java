package org.amba.app.Dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportDTO {


    @Email
    String email;

    @NotNull
    String project;

    @NotNull
    String type;

    @NotNull
    List<String>  questionNumber;

    @NotNull
    long score;

    @NotNull
    String role;

    @NotNull
    LocalDateTime reportDate;

    @NotNull
    long totalQuestions;
}
