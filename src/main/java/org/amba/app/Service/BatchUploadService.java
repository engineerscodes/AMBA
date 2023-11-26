package org.amba.app.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.amba.app.Dto.QuestionDTO;
import org.amba.app.Entity.Project;
import org.amba.app.Entity.Question;
import org.amba.app.Entity.QuestionAudit;
import org.amba.app.Messaging.RabbitClient;
import org.amba.app.Repo.ProjectRepo;
import org.amba.app.Repo.QuestionAuditRepo;
import org.amba.app.Repo.QuestionRepo;
import org.amba.app.Util.MquStatus;
import org.amba.app.Util.Options;
import org.amba.app.Util.QuestionMessage;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class BatchUploadService {


    @Autowired
    ProjectRepo projectRepo;

    @Autowired
    QuestionRepo questionRepo;

    @Autowired
    RabbitClient rabbitClient;

    @Autowired
    QuestionAuditRepo questionAuditRepo;


    final List<String>   reqColumns = List.of("Project Name","Question Image","Question Text","Answer Index","Option","Option Image");

    public ByteArrayOutputStream validateExcelSheet(MultipartFile file,Class<?> clazz) throws IOException {
        boolean type = Objects.requireNonNull(file.getContentType())
                .equalsIgnoreCase("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        Assert.isTrue(type, "Only MS EXCEL ALLOWED ,Uploaded file type "+file.getContentType() +" is Not Allowed");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String fileName  = file.getOriginalFilename();
        Assert.isTrue(fileName!=null,"File Name can't be Null");

        XWPFDocument doc = new XWPFDocument(file.getInputStream());
        XWPFTable tables = doc.getTables().get(0);
        // add new Column
        tables.addNewCol();
        List<XWPFTableRow> row = tables.getRows();

        LinkedHashSet<String> tableColumns = getColumnName(row.get(0));
        List<String> IndexedCol = tableColumns.stream().toList();
        log.info("Found Column with Names {} in Document {}", tableColumns, fileName);
        checkColumnNames(tableColumns);
        log.info("Was Called From Class {}", clazz.getSimpleName());
        if(clazz.getSimpleName().equalsIgnoreCase("AdminController")){
            QuestionAudit qa = null;
            try {
                qa = saveRecord();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
            Assert.isTrue(qa.getQuestionID()!=null,"Question Upload ID can't be Null");
            rabbitClient.sendMessage(QuestionMessage.builder().QuestionUploadId(qa.getQuestionID()).
                    fileData(file.getInputStream().readAllBytes()).time(LocalDateTime.now().toString()).build());
            doc.write(out);
            doc.close();
            out.close();
            return out;
        }

        for (int rowIndex = 0; rowIndex < row.size(); rowIndex++) {
            if (rowIndex != 0) {
                XWPFTableRow xwpfTableRow = row.get(rowIndex);
                Question newQuestion = new Question();
                HashMap<Integer, byte[]> optionsImg = new HashMap<>();
                HashMap<Integer, String> options = new HashMap<>();
                List<XWPFTableCell> cell = xwpfTableRow.getTableCells();
                for (int colIndex = 0; colIndex < cell.size(); colIndex++) {
                    XWPFTableCell xwpfTableCell = cell.get(colIndex);
                    if (xwpfTableCell != null)
                        if (IndexedCol.get(colIndex).toLowerCase().contains("image")) {
                            for (XWPFParagraph p : xwpfTableCell.getParagraphs()) {
                                for (XWPFRun run : p.getRuns()) {
                                    if (!run.getEmbeddedPictures().isEmpty()) {
                                        XWPFPicture pic = run.getEmbeddedPictures().get(0);
                                        byte[] picture = pic.getPictureData().getData();
                                        try {
                                            setValuesImg(newQuestion, IndexedCol.get(colIndex).toLowerCase(), picture, optionsImg);
                                        }catch (Exception e){
                                            cell.get(cell.size()-1).setText(e.getMessage());
                                            cell.parallelStream().forEach(col -> col.setColor("DB1F48"));
                                            break;
                                        }
                                    }
                                }
                            }
                        } else {
                            try {
                                setReqQuestionValuesNoImg(newQuestion, IndexedCol.get(colIndex), xwpfTableCell.getText(), options);
                            }catch (Exception e){
                                cell.get(cell.size()-1).setText(e.getMessage());
                                cell.parallelStream().forEach(col -> col.setColor("DB1F48"));
                                break;
                            }

                        }
                }

                // validation for options
                try {
                    answerValidationAndSave(newQuestion, options, optionsImg);
                    cell.parallelStream().forEach(col -> col.setColor("00FF00"));
                    cell.get(cell.size() - 1).setText("Uploaded ");
                }catch (Exception e){
                    cell.get(cell.size()-1).setText(e.getMessage());
                    cell.parallelStream().forEach(col -> col.setColor("DB1F48"));
                }
            }
        }
        doc.write(out);
        //Close the doc
        doc.close();
        out.close();
        return out;
    }


    public void setValuesImg(Question question,String currentCol,byte imgData[],HashMap<Integer,byte[]> optionsImg){
        Assert.isTrue(imgData.length>0,"No Image Found for `"+currentCol+"`");
        if(currentCol.equalsIgnoreCase("Question Image")){
            question.setQuestion(imgData);
        }
        else if(currentCol.startsWith("Option Image".toLowerCase())){
            int optionIndex = Integer.parseInt(currentCol.split("Option Image ".toLowerCase())[1]);
            optionsImg.put(optionIndex,imgData);
        }
    }

    public void  answerValidationAndSave(Question question,HashMap<Integer,String> options,HashMap<Integer,byte[]> optionsImg){

        long answerIndex = question.getAnswerID();
        List<Options> questionOptions = new ArrayList<>();
        Assert.isTrue(options.size()==optionsImg.size(),
                "No of Options & No of Options Image doesn't match for question `"+question.getQuestionText()+"`");
        Assert.isTrue(answerIndex>0,"Answer index is missing");
        Assert.isTrue(answerIndex<=options.size(),
                "Answer index for Question `"+question.getQuestionText()+"` is Greater than No of Options ");
        for(int i=1;i<=options.size();i++){
            System.out.println(options.get(i)+" --- "+ Arrays.toString(optionsImg.get(i)));
            if( options.get(i)!=null && optionsImg.get(i)==null){
                throw new RuntimeException("Option " + i + " is Empty and Option Image " + i + " has data for Question " + question.getQuestionText());
            }
            if( options.get(i)==null && optionsImg.get(i)!=null)
                throw new RuntimeException("Option "+i+ " has data but Option Image "+i +" is Empty for Question "+question.getQuestionNumber());
            questionOptions.add(new Options(options.get(i),optionsImg.get(i)));
        }
        question.setAnswerID(answerIndex-1);
        question.setOptions(questionOptions);
        questionRepo.saveAndFlush(question);
        log.info("Saved Question with Number {}",question.getQuestionID());
    }


    public void setReqQuestionValuesNoImg(Question question,String currentCol,String value,HashMap<Integer,String> options){
        value = value.trim();
        if(!currentCol.startsWith("option") && !currentCol.equalsIgnoreCase("STATUS"))
         Assert.isTrue(!value.isEmpty(),currentCol+" can't be Empty ");
        if(currentCol.equalsIgnoreCase("Project Name")){
            ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreCase("projectName");
            Example<Project> projectExample = Example.of(Project.builder().projectName(value).build(),matcher);
            Optional<Project> p = projectRepo.findBy(projectExample, FluentQuery.FetchableFluentQuery::first);
            Assert.isTrue(p.isPresent(),"No Project With Name `"+value+"` Found ");
            Project project = p.get();
            question.setProject(project);
        }
        else if(currentCol.equalsIgnoreCase("Question Text")){
            question.setQuestionText(value);
        }
        else if(currentCol.equalsIgnoreCase("Answer Index")){
            long answerIndex = Long.parseLong(value);
            // AnswerIndex Will be validated before save ...........
            question.setAnswerID(answerIndex);
        }else if (currentCol.startsWith("Option".toLowerCase()) && !currentCol.startsWith("Option Image".toLowerCase())){
            int optionIndex = Integer.parseInt(currentCol.split("option ")[1]);
            if(!value.isEmpty()) options.put(optionIndex,value);
        }
    }



    public void checkColumnNames(HashSet<String> tableColumns){
        for (String col : reqColumns){
            if(!col.startsWith("Option"))
              Assert.isTrue(tableColumns.contains(col.toLowerCase()),
                      "Required  Column `"+ col+"` Missing from Document");
        }
        validateOptions(tableColumns);
    }

    public void validateOptions(HashSet<String> tableColumns){
        long optionCol = tableColumns.stream().filter(col -> col.startsWith("Option".toLowerCase()) && !col.startsWith("Option Image".toLowerCase())).count();
        long optionImgCol = tableColumns.stream().filter(col -> col.startsWith("Option Image".toLowerCase())).count();
        Assert.isTrue(optionCol!=0,"No Column  Found that Starts with Name `Option`");
        Assert.isTrue(optionImgCol!=0,"No Column Found that Starts with `Option Image`");
        Assert.isTrue(optionImgCol==optionCol,"No of Option and Option Image are not Matching found [Options = "+optionCol +" ], [Option Image = "+optionImgCol +" ]");
        for(long i=1;i<=optionCol;i++){
            Assert.isTrue(tableColumns.contains("Option ".toLowerCase()+i),"No Option column found with Name `Option "+i+"`");
            Assert.isTrue(tableColumns.contains("Option Image ".toLowerCase()+i),"No Option column found with Name `Option Image "+i+"`");
        }
    }

    public LinkedHashSet<String> getColumnName(XWPFTableRow zerothRow){ //row = zero
        List<XWPFTableCell> cell = zerothRow.getTableCells();
        LinkedHashSet<String> columnNames = new  LinkedHashSet<>();
        for (XWPFTableCell currentColCell:cell) {
            if(currentColCell!=null) {
                if(currentColCell.getText().isEmpty())
                {
                    currentColCell.setColor("00FFFF");
                    currentColCell.setText("STATUS");
                }
                Assert.isTrue(!columnNames.contains(currentColCell.getText()), "Multiple Columns with Same Name found : " + currentColCell.getText());
                columnNames.add(currentColCell.getText().toLowerCase().trim());
            }
        }
        return columnNames;
    }

    @Transactional
    public QuestionAudit saveRecord(){
        QuestionAudit qa = QuestionAudit.builder().uploadStatus(MquStatus.UPLOADED)
                .dateTime(LocalDateTime.now()).build();
        questionAuditRepo.saveAndFlush(qa);
        Assert.isTrue(qa.getQuestionID()!=null,"Question Upload ID can't be Null");
        String newFileName = "QuestionUpload_"+qa.getQuestionID()+"_"+".docx";
        qa.setFilePath(newFileName);
        questionAuditRepo.saveAndFlush(qa);
        return qa;
    }

}


/*
for (XWPFPicture pic : run.getEmbeddedPictures()) {
    byte[] pictureData = pic.getPictureData().getData();
    System.out.println("picture : " + pictureData);
 }
 */

//https://stackoverflow.com/questions/44016335/read-the-tables-data-from-the-docx-files