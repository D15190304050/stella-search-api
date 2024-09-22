package stark.stellasearch.service.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.ExecutionException;

@Component
@Slf4j
@EnableAsync
public class ProducerService
{
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Send message.
     *
     * @param topic
     * @param data
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void sendMessage(String topic, String data) throws ExecutionException, InterruptedException
    {
        SendResult<String, String> sendResult = kafkaTemplate.send(topic, data).get();
        RecordMetadata recordMetadata = sendResult.getRecordMetadata();
        log.info("Send message successfully, topic = {}, message = {}", recordMetadata.topic(), data);
    }

    /**
     * Send message asynchronously.
     *
     * @param topic
     * @param data
     */
    public void sendMessageAsync(String topic, String data, ListenableFutureCallback<SendResult<String, String>> callback)
    {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, data);
        future.addCallback(callback);
    }

    /**
     * 发送带附加信息的消息
     *
     * @param record
     */
    public void sendMessage(ProducerRecord<String, String> record)
    {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>()
        {
            @Override
            public void onFailure(Throwable throwable)
            {
                log.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult)
            {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                log.debug("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }

    /**
     * 发送Message消息
     *
     * @param message
     */
    public void sendMessage(Message<String> message)
    {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>()
        {
            @Override
            public void onFailure(Throwable throwable)
            {
                log.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult)
            {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                log.debug("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }

    /**
     * 发送带key的消息
     *
     * @param topic
     * @param key
     * @param data
     */
    public void sendMessage(String topic, String key, String data)
    {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, data);
        log.info("发送到：{} ，消息体为：{}", topic, data);
        future.addCallback(
                success -> log.debug("发送消息成功!"),
                failure -> log.error("发送消息失败!失败原因是:{}", failure.getMessage())
        );
    }

    public void sendMessage(String topic, Integer partition, String key, String data)
    {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, partition, key, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>()
        {
            @Override
            public void onFailure(Throwable throwable)
            {
                log.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult)
            {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                log.debug("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }

    public void sendMessage(String topic, Integer partition, Long timestamp, String key, String data)
    {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, partition, timestamp, key, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>()
        {
            @Override
            public void onFailure(Throwable throwable)
            {
                log.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult)
            {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                log.debug("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }
}

