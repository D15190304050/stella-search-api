package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.VideoPlayRecord;

@Mapper
public interface VideoPlayRecordMapper {
    int insert(VideoPlayRecord videoPlayRecord);

    long countPlayCountByVideoId(long videoId);
}
