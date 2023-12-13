package org.amba.app.Consumer;


import lombok.extern.slf4j.Slf4j;
import org.amba.app.Entity.QuestionAudit;
import org.amba.app.Repo.QuestionAuditRepo;
import org.amba.app.Service.BatchUploadService;
import org.amba.app.Util.MquStatus;
import org.amba.app.Util.QuestionMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


@Service
@Slf4j
public class QuestionUpload {

   @Autowired
   BatchUploadService batchUploadService;

   @Autowired
   QuestionAuditRepo questionAuditRepo;

   @RabbitListener(queues = {"Massive_Question_Upload"})
    public void onUserRegistration(QuestionMessage message) throws IOException {
       log.info("New File with id {} Received at {} ",message.getQuestionUploadId(),message.getTime());
       try {
           QuestionAudit qa = questionAuditRepo.findById(message.getQuestionUploadId())
                   .orElseThrow(() -> new RuntimeException("No record found"));
           qa.setUploadStatus(MquStatus.PROCESSING);
           questionAuditRepo.saveAndFlush(qa);
           Assert.isTrue(qa.getQuestionID() != null, "Question Upload ID can't be Null");
           String fileName = "QuestionUpload_" + qa.getQuestionID() + "_" + ".docx";
           MultipartFile file = new MockMultipartFile(
                   fileName,         // Original file name
                   fileName,         // Desired file name
                   "application/vnd.openxmlformats-officedocument.wordprocessingml.document",  // Content type
                   message.getFileData()            // Byte[] array
           );

           FileOutputStream fout = new FileOutputStream("src//main//resources//Files//QuestionUpload//" + fileName);
           ByteArrayOutputStream out = batchUploadService.validateExcelSheet(file, QuestionUpload.class);
           out.writeTo(fout);
           out.close();
           fout.close();
           qa.setUploadStatus(MquStatus.DONE);
           questionAuditRepo.saveAndFlush(qa);
           log.info("Uploaded ENDED ......");
       }catch (Exception e){
           QuestionAudit qa = questionAuditRepo.findById(message.getQuestionUploadId())
                   .orElseThrow(() -> new RuntimeException("No record found"));
           qa.setUploadStatus(MquStatus.ERROR);
           questionAuditRepo.saveAndFlush(qa);
       }
   }

}
