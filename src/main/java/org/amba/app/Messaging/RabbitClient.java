package org.amba.app.Messaging;


import lombok.extern.slf4j.Slf4j;
import org.amba.app.Util.QuestionMessage;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RabbitClient {

    @Autowired
    private RabbitTemplate rabbitTemplate;

   // public <T> void sendMessage(T payload) throws RuntimeException{
    public <T> void sendMessage(QuestionMessage payload) throws RuntimeException{
        try{
            rabbitTemplate.convertAndSend("Massive_Question_Upload",payload);
            log.info("[RabbitMQ]: Message to Queue");
        }catch (AmqpException e){
            log.error("Error in sending message .....");
            throw new RuntimeException(e.getCause());
        }
    }

}
