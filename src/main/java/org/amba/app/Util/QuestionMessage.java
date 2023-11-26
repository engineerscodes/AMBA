package org.amba.app.Util;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.time.LocalDateTime;


@Data
@Builder
public class QuestionMessage implements Serializable {

    long QuestionUploadId;

    byte[] fileData;

    String time;
}
