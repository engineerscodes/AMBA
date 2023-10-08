package org.amba.app.Dto;


import org.amba.app.Util.Options;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

public record QuestionDTO(UUID questionID, byte[] question, List<Options> options,
                          long answerID, BigInteger questionNumber,String questionText) {
        }
/*
   q1_0.questionid,
        q1_0.answer_index,
        q1_0.options,
        q1_0.fk_project_uuid,
        q1_0.question_img
    from
        question q1_0
    where
        q1_0.fk_project_uuid=?
* */