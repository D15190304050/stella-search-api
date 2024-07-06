package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.VideoUploadingTask;

@Mapper
public interface VideoUploadingTaskMapper
{
    int insert(VideoUploadingTask videoUploadingTask);
}
