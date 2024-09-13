package stark.stellasearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.UserVideoInfoMapper;
import stark.stellasearch.dao.VideoCommentMapper;
import stark.stellasearch.domain.UserVideoComment;
import stark.stellasearch.domain.UserVideoInfo;
import stark.stellasearch.dto.params.AddCommentsRequest;
import stark.stellasearch.dto.params.DeleteCommentsRequest;
import stark.stellasearch.dto.params.GetVideoCommentsByIdParam;
import stark.stellasearch.dto.params.GetVideoCommentsRequest;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@LogArgumentsAndResponse
public class CommentService
{
    @Autowired
    private UserVideoInfoMapper userVideoInfoMapper;

    @Autowired
    private VideoCommentMapper videoCommentMapper;

    public ServiceResponse<Boolean> addComment(@Valid AddCommentsRequest request)
    {
        // 1. Validate if video id exists
        UserVideoInfo videoInfo = userVideoInfoMapper.getVideoBaseInfoById(request.getVideoId());
        if (videoInfo == null)
        {
            return ServiceResponse.buildErrorResponse(-1, "Invalid video ID: " + request.getVideoId());
        }

        // 2. Validate if parent id exists
        if (videoCommentMapper.countIdByParentId(request.getParentId()) != 1 && request.getParentId() != -1)
        {
            return ServiceResponse.buildErrorResponse(-2, "Invalid parent ID: " + request.getParentId());
        }

        // 3. Insert comment
        Date now = new Date();
        UserVideoComment comment = new UserVideoComment();
        comment.setUserId(UserContextService.getCurrentUser().getId());
        comment.setVideoId(request.getVideoId());
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setCreatorId(videoInfo.getCreatorId());
        comment.setCreationTime(now);
        comment.setModifierId(videoInfo.getCreatorId());
        comment.setModificationTime(now);
        if (videoCommentMapper.insertComment(comment) == 0)
        {
            return ServiceResponse.buildErrorResponse(-3, "Failed to insert comment.");
        }

        return ServiceResponse.buildSuccessResponse(true);
    }

    public ServiceResponse<List<UserVideoComment>> getVideoCommentById(@Valid GetVideoCommentsRequest request)
    {
        // Validate if video id exists
        UserVideoInfo videoInfo = userVideoInfoMapper.getVideoBaseInfoById(request.getVideoId());
        if (videoInfo == null)
        {
            return ServiceResponse.buildErrorResponse(-1, "Invalid video ID: " + request.getVideoId());
        }

        // Get comments
        long pageCapacity = request.getPageCapacity();

        GetVideoCommentsByIdParam queryParam = new GetVideoCommentsByIdParam();
        queryParam.setVideoId(request.getVideoId());
        queryParam.setPageCapacity(pageCapacity);
        queryParam.setOffset(pageCapacity * (request.getPageIndex() - 1));
        List<UserVideoComment> comments = videoCommentMapper.getVideoCommentsByVideoId(queryParam);

        ServiceResponse<List<UserVideoComment>> response = ServiceResponse.buildSuccessResponse(comments);
        response.putExtra("size", comments.size());

        return response;
    }

    public ServiceResponse<Boolean> deleteComment(@Valid DeleteCommentsRequest request)
    {
        // Validate if comment id exists
        UserVideoComment commentInfo = videoCommentMapper.getCommentById(request.getId());
        if (commentInfo == null)
        {
            return ServiceResponse.buildErrorResponse(-1, "Invalid video ID: " + request.getId());
        }

        // Validate if the current user id is the creator of the comment
        if (UserContextService.getCurrentUser().getId() != commentInfo.getCreatorId())
        {
            return ServiceResponse.buildErrorResponse(-2, "You can not delete this comment because you are not the creator of the comment.");
        }

        // Delete comment
        videoCommentMapper.deleteCommentById(request.getId());

        return ServiceResponse.buildSuccessResponse(true);
    }
}
