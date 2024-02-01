package org.amba.app.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Email
    @Column(nullable = false)
    String email;

    @Column(nullable = false)
    String Project;

    @Column(nullable = false)
    UUID projectUuid;

    @Column(nullable = false)
    String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb",nullable = true)
    List<String>  questionNumber;

    @Column(nullable = false)
    long score;

    @Column(nullable = false)
    String role;


    @Column(name = "report_date_time",nullable = false)
    LocalDateTime reportDateTime;

    @Column
    long totalQuestions;

}
