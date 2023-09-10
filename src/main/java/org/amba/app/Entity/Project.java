package org.amba.app.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Type;
import org.springframework.web.bind.annotation.RequestPart;

import java.util.UUID;


@Data
@AllArgsConstructor
@Entity
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;


    @Column(name = "project_name",unique = true,nullable = false)
    private String projectName;

    @Column(name = "project_img")
    @Lob
    private byte[] imageData;

    public Project(String projectName,byte[] imageData){
        this.projectName = projectName;
        this.imageData = imageData;
    }

}
