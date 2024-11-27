package stark.stellasearch.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;

@Slf4j
@Service
@LogArgumentsAndResponse
public class VideoSummaryService
{
    @Autowired
    private ElasticsearchClient elasticsearchClient;
}
