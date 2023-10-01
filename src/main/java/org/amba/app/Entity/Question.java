package org.amba.app.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.amba.app.Util.Options;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;


import java.util.List;
import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "question_img",nullable = false)
    @Lob
    private byte[] question;


    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb",nullable = false)
    private List<Options> options;


    @Column(name = "answer_index",nullable = false)
    private long answerID;


}
