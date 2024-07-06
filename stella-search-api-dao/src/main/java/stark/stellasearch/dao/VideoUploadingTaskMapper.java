package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.VideoUploadingTask;

@Mapper
public interface VideoUploadingTaskMapper
{
    int insert(VideoUploadingTask videoUploadingTask);
    int setStateById(@Param("taskId") String taskId, @Param("state") int state);
}
