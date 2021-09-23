package cn.nome.saas.cart.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author chentaikuang
 */
@Component
public class TestReceiver {

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

//    @KafkaListener(topics = {"pollTopic"}, containerFactory = "kafkaListenerContainerFactory")
//    public void listen(ConsumerRecord<?, ?> record) {
//        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//        if (kafkaMessage.isPresent()) {
//            Object message = kafkaMessage.get();
//            LOGGER.info("1-[POLL_TOPIC]:{}", message);
//        }
//    }

//    @KafkaListener(topics = {"pollTopic"})
//    public void listen2(ConsumerRecord<?, ?> record) {
//        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
//        if (kafkaMessage.isPresent()) {
//            Object message = kafkaMessage.get();
//            LOGGER.info("2-[POLL_TOPIC]:{}", message);
//        }
//    }

//    @KafkaListener(topics = "pollTopic", containerFactory = "kafkaListenerContainerFactory")
//    public void ackListener(ConsumerRecord record, Acknowledgment ack) {
//        LOGGER.info("[POLL_TOPIC]ack receive:{}", record.value());
//        ack.acknowledge();
//        LOGGER.info("[POLL_TOPIC]ack done");
//    }
}