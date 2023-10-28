package org.amba.app.Crons.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class QuestionCount {

    long questionCount;

    private UUID id;

    private String projectName;

    private String type;


}
