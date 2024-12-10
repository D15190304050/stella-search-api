package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.List;

@Data
public class ElasticsearchResult<T>
{
    private List<T> data;
    private long total;
}
