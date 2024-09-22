package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.List;

@Data
public class TranscriptSummary
{
    private String summary;
    private List<ContentStructure> contentStructures;
    private List<String> labels;
}
