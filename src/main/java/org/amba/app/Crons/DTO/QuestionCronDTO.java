package org.amba.app.Crons.DTO;


import org.springframework.data.rest.core.config.Projection;

import java.math.BigInteger;
import java.util.UUID;



public interface QuestionCronDTO {

    UUID getquestionID();

    BigInteger getquestionNumber();

}
