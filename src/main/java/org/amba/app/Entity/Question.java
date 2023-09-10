package org.amba.app.Entity;

import jakarta.persistence.*;
import org.amba.app.Util.Answer;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;


import java.util.List;
import java.util.UUID;

@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID questionID;


    /**
     *  Many question mapped to one Project ID
     */
    @ManyToOne
    @JoinColumn(name = "fk_project_uuid",referencedColumnName = "id")
    private Project project;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<Answer> answers;
}
