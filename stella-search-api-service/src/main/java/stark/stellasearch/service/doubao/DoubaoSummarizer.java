package stark.stellasearch.service.doubao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.stellasearch.dto.results.ContentStructure;
import stark.stellasearch.dto.results.TranscriptSummary;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class DoubaoSummarizer
{
    public static final String SEND_TRANSCRIPT_PREFIX = "我这里有一份字幕，你接下来的回答都要严格根据这份字幕里的内容生成，下面是我的字幕\n";
    public static final String TRANSCRIPT_SUMMARY = "你擅长写内容总结，内容全面具体精准，对于先前的对话中给出的字幕内容，我需要你帮我生成总结，生成的总结信息每一行由时间开头，只需要给出开始时间即可，后面跟上一句话的简述，总共大约15条(重要)，要能够完整概括字幕的内容，需要完全按照原文顺序和内容进行总结(重要)，一定要完整，不要只对前面几句作出总结，按照时间顺序排列好，时间要从字幕信息里取,时间要从字幕信息里取,时间要从字幕信息里取，不要自己随意生成，生成的摘要格式如下:\"[时间] - [内容]\"，例如：\"00:00:32,608 - 字幕内容字幕内容字幕内容\"。";
    public static final String TRANSCRIPT_STRUCTURE =
        "对于我一开始给出的字幕内容，我需要你帮我总结其文字部分的内容，并且输出嵌套层级的JSON格式，只需要title, content, children字段，如果没有子内容，children就是空数组，例如，" +
            "[\n" +
            "    {\n" +
            "        \"title\": \"标题1\",\n" +
            "        \"content\": \"展开的内容1\",\n" +
            "        \"children\": [\n" +
            "            {\n" +
            "                \"title\": \"子标题1\",\n" +
            "                \"content\": \"展开的子内容1\"\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    {\n" +
            "        \"title\": \"标题2\",\n" +
            "        \"content\": \"展开的内容2\",\n" +
            "        \"children\": [] // 没有的话可以不写\n" +
            "    }\n" +
            "]";
    public static final String TRANSCRIPT_LABELS = "对于我一开始给出的字幕内容，我需要你帮我生成几个关键词，10个以内就行，用顿号分隔开，例如\"关键词1、关键词2\"";

    @Autowired
    private DoubaoMultiRoundChatSessionFactory doubaoMultiRoundChatSessionFactory;

    public TranscriptSummary summarize(String transcript)
    {
        DoubaoMultiRoundChatSession connection = doubaoMultiRoundChatSessionFactory.build();

        connection.runChat(SEND_TRANSCRIPT_PREFIX + transcript);
        String summary = connection.runChat(TRANSCRIPT_SUMMARY);
        String structureJsonText = connection.runChat(TRANSCRIPT_STRUCTURE);
        String labelsText = connection.runChat(TRANSCRIPT_LABELS);

        List<ContentStructure> contentStructures = JsonSerializer.deserializeList(structureJsonText, ContentStructure.class);
        List<String> labels = Arrays.asList(labelsText.substring(1, labelsText.length() - 1).split("、"));

        TranscriptSummary transcriptSummary = new TranscriptSummary();
        transcriptSummary.setCanSummary(true);
        transcriptSummary.setSummary(summary);
        transcriptSummary.setLabels(labels);
        transcriptSummary.setContentStructures(contentStructures);
        return transcriptSummary;
    }
}
