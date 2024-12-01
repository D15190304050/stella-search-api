package stark.stellasearch.dao.es.queryers;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.stellasearch.domain.entities.es.VideoSummaryInfo;

import java.io.IOException;
import java.util.List;

@Service
public class VideoSummaryInfoQueryer
{
    @Getter
    @Setter
    private List<String> queryFields;

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    public List<VideoSummaryInfo> searchByKeyword(String keyword) throws IOException
    {
        Query query = Query.of(q -> q
            .multiMatch(m -> m
                .query(keyword)
                .fields(queryFields)
                .type(TextQueryType.BestFields)
            )
        );

        SearchRequest searchRequest = SearchRequest.of(s -> s
            .index(VideoSummaryInfo.INDEX_NAME)
            .query(query)
        );

        SearchResponse<VideoSummaryInfo> searchResponse = elasticsearchClient.search(searchRequest, VideoSummaryInfo.class);
        return searchResponse
            .hits()
            .hits()
            .stream()
            .map(Hit::source)
            .toList();
    }
}
