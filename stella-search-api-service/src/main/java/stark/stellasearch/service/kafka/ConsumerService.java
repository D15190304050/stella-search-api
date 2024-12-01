package stark.stellasearch.service.kafka;

import io.minio.errors.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.boot.autoconfig.minio.EasyMinio;
import stark.stellasearch.dao.UserVideoInfoMapper;
import stark.stellasearch.dao.es.repositories.VideoSummaryInfoRepository;
import stark.stellasearch.domain.UserVideoInfo;
import stark.stellasearch.domain.entities.es.VideoSummaryInfo;
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
    public static final long SUMMARY_THRESHOLD = 1000;

    @Value("${dataworks.easy-minio.bucket-name-video-subtitles}")
    private String bucketNameVideoSubtitles;

    @Value("${dataworks.easy-minio.bucket-name-summaries}")
    private String bucketNameSummaries;

    @Autowired
    private EasyMinio easyMinio;

    @Autowired
    private DoubaoSummarizer doubaoSummarizer;

    @Autowired
    private UserVideoInfoMapper userVideoInfoMapper;

    @Autowired
    private VideoSummaryInfoRepository videoSummaryInfoRepository;

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
            TranscriptSummary summary;

            // We don't generate summary for transcript with length less than SUMMARY_THRESHOLD.
            if (StringUtils.hasText(transcript) && transcript.length() > SUMMARY_THRESHOLD)
            {
                summary = doubaoSummarizer.summarize(transcript);
                log.info("Summary = {}", JsonSerializer.serialize(summary));
            }
            else
            {
                summary = new TranscriptSummary();
                summary.setCanSummary(false);
            }

            saveSummary(videoId, summary);
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

    private void saveSummary(long videoId, TranscriptSummary transcriptSummary) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        saveSummaryToElasticsearch(videoId, transcriptSummary);
        String summaryFileName = saveSummaryToMinio(videoId, transcriptSummary);
        saveSummaryFileNameToDb(videoId, summaryFileName);
    }

    private void saveSummaryToElasticsearch(long videoId, TranscriptSummary transcriptSummary)
    {
        VideoSummaryInfo videoSummaryInfo = toVideoSummaryInfo(videoId, transcriptSummary);
        videoSummaryInfoRepository.save(videoSummaryInfo);
    }

    private VideoSummaryInfo toVideoSummaryInfo(long videoId, TranscriptSummary transcriptSummary)
    {
        UserVideoInfo userVideoInfo = userVideoInfoMapper.getVideoBaseInfoById(videoId);
        VideoSummaryInfo videoSummaryInfo = new VideoSummaryInfo();
        videoSummaryInfo.setVideoId(videoId);
        videoSummaryInfo.setTitle(userVideoInfo.getTitle());
        videoSummaryInfo.setIntroduction(userVideoInfo.getIntroduction());
        videoSummaryInfo.setSummary(transcriptSummary.getSummary());
        videoSummaryInfo.setLabels(transcriptSummary.getLabels());
        return videoSummaryInfo;
    }

    private String saveSummaryToMinio(long videoId, TranscriptSummary transcriptSummary) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        String summaryFileName = DoubaoSummarizer.getSummaryFileName(videoId);
        easyMinio.putObject(bucketNameSummaries, summaryFileName, transcriptSummary);
        return summaryFileName;
    }

    private void saveSummaryFileNameToDb(long videoId, String summaryFileName)
    {
        userVideoInfoMapper.setVideoSummaryFileNameById(videoId, summaryFileName);
    }
}

