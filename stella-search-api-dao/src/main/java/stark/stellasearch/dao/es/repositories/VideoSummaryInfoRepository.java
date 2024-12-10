package stark.stellasearch.dao.es.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import stark.stellasearch.domain.entities.es.VideoSummaryInfo;

public interface VideoSummaryInfoRepository extends ElasticsearchRepository<VideoSummaryInfo, Long>
{
}
