package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.UserVideoComment;
import stark.stellasearch.dto.params.GetVideoCommentsByIdParam;

import java.util.List;

@Mapper
public interface VideoCommentMapper
{
    int countIdByParentId(long parentId);

    int insertComment(UserVideoComment userVideoComment);

    List<UserVideoComment> getVideoCommentsByVideoId(GetVideoCommentsByIdParam queryParam);
}
