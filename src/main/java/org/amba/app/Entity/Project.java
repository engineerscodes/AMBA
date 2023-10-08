package org.amba.app.Entity;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.UUID;


@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "project_name",unique = true,nullable = false)
    private String projectName;

    @Column(name = "project_img")
    @Lob
    private byte[] imageData;

    @ManyToOne
    @JoinColumn(name = "fk_type",referencedColumnName = "id",nullable = false)
    private Type type;


}
