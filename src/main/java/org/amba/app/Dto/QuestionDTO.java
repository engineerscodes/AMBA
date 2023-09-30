package org.amba.app.Dto;

import jakarta.persistence.*;
import org.amba.app.Entity.Project;
import org.amba.app.Util.Options;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.UUID;

public class QuestionDTO {

    private String projectId;

    private byte[] QuestionImage;

    // JsonNode options
    private List<Options> options;

    private String answer_id;

}
