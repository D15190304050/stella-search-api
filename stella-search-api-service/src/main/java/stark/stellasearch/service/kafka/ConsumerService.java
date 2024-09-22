package stark.stellasearch.service.kafka;

import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.boot.autoconfig.minio.EasyMinio;
import stark.stellasearch.dto.params.VideoSummaryEndMessage;
import stark.stellasearch.dto.results.TranscriptSummary;
import stark.stellasearch.service.doubao.DoubaoSummarizer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class ConsumerService
{
    @Value("${dataworks.easy-minio.bucket-name-video-subtitles}")
    private String bucketNameVideoSubtitles;

    @Autowired
    private EasyMinio easyMinio;

    @Autowired
    private DoubaoSummarizer doubaoSummarizer;

    @KafkaListener(topics = {"${spring.kafka.consumer.topic-summary-video-end}"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory",
            properties = {"${spring.kafka.consumer.auto-offset-reset}"})
    public void handleMessage(ConsumerRecord<String, String> record, Acknowledgment ack)
    {
        String message = record.value();
        log.info("Received data, topic = {}，value = {}", record.topic(), message);

        try
        {
            VideoSummaryEndMessage summaryEndMessage = JsonSerializer.deserialize(message, VideoSummaryEndMessage.class);
            long videoId = summaryEndMessage.getVideoId();
            String subtitleObjectName = summaryEndMessage.getSubtitleObjectName();

            String transcript = getTranscript(subtitleObjectName);

            TranscriptSummary summary = doubaoSummarizer.summarize(transcript);

            log.info("Summary = {}", JsonSerializer.serialize(summary));
        }
        catch (Exception e)
        {
            log.error("Error consuming message, value = {}", message, e);
        }
        finally
        {
            // Submit offset manually.
            ack.acknowledge();
            log.info("Done consuming message, value = {}", message);
        }
    }

    private String getTranscript(String subtitleObjectName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        InputStream objectInputStream = easyMinio.getObjectInputStream(bucketNameVideoSubtitles, subtitleObjectName);
        byte[] byteContent = objectInputStream.readAllBytes();
        return new String(byteContent, StandardCharsets.UTF_8);
    }

}

