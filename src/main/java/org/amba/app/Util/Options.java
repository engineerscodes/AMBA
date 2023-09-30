package org.amba.app.Util;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class Options {

    String answer;

    // MultipartFile Image

    byte[] answerImage;

}
