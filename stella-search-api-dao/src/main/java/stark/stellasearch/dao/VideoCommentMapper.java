package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.UserVideoComment;
import stark.stellasearch.dto.params.GetCommentsByVideoIdQueryParam;
import stark.stellasearch.dto.results.VideoCommentInfo;

import java.util.List;

@Mapper
public interface VideoCommentMapper
{
    int countByParentId(long parentId);
    int insertComment(UserVideoComment userVideoComment);
    List<VideoCommentInfo> getVideoCommentsByVideoId(GetCommentsByVideoIdQueryParam queryParam);
    int deleteCommentById(long id);
    UserVideoComment getCommentById(long id);
    long countCommentsByVideoId(long videoId);
}
