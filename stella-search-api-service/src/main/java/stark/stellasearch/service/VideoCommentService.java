package stark.stellasearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.UserVideoInfoMapper;
import stark.stellasearch.dao.VideoCommentMapper;
import stark.stellasearch.domain.UserVideoComment;
import stark.stellasearch.dto.params.AddCommentsRequest;
import stark.stellasearch.dto.params.DeleteCommentsRequest;
import stark.stellasearch.dto.params.GetCommentsByVideoIdQueryParam;
import stark.stellasearch.dto.params.GetCommentsByVideoIdRequest;
import stark.stellasearch.dto.results.VideoCommentInfo;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@LogArgumentsAndResponse
public class VideoCommentService
{
    @Autowired
    private UserVideoInfoMapper userVideoInfoMapper;

    @Autowired
    private VideoCommentMapper videoCommentMapper;

    public ServiceResponse<Long> addComment(@Valid AddCommentsRequest request)
    {
        // 1. Validate if video id exists.
        long videoId = request.getVideoId();
        long videoCount = userVideoInfoMapper.countVideoById(videoId);
        if (videoCount == 0)
            return ServiceResponse.buildErrorResponse(-1, "Invalid video ID: " + videoId);

        // 2. Validate if parent id exists.
        long commentParentId = request.getParentId();
        if ((commentParentId != -1) &&
            (videoCommentMapper.countByParentId(commentParentId) != 1))
            return ServiceResponse.buildErrorResponse(-2, "Invalid comment parent ID: " + commentParentId);

        // 3. Insert comment.
        UserVideoComment comment = generateUserVideoComment(videoId, commentParentId, request.getContent());
        if (videoCommentMapper.insertComment(comment) == 0)
            return ServiceResponse.buildErrorResponse(-3, "Failed to insert comment, please try again.");

        return ServiceResponse.buildSuccessResponse(comment.getId());
    }

    private static UserVideoComment generateUserVideoComment(long videoId, long commentParentId, String content)
    {
        Date now = new Date();
        long userId = UserContextService.getCurrentUser().getId();

        UserVideoComment comment = new UserVideoComment();
        comment.setUserId(UserContextService.getCurrentUser().getId());
        comment.setVideoId(videoId);
        comment.setContent(content);
        comment.setParentId(commentParentId);
        comment.setCreatorId(userId);
        comment.setCreationTime(now);
        comment.setModifierId(userId);
        comment.setModificationTime(now);
        return comment;
    }

    public ServiceResponse<PaginatedData<VideoCommentInfo>> getCommentsByVideoId(@Valid GetCommentsByVideoIdRequest request)
    {
        long videoId = request.getVideoId();

        // Validate if video id exists.
        long videoCount = userVideoInfoMapper.countVideoById(videoId);
        if (videoCount == 0)
            return ServiceResponse.buildErrorResponse(-1, "Invalid video ID: " + videoId);

        // Get comments.
        GetCommentsByVideoIdQueryParam queryParam = new GetCommentsByVideoIdQueryParam();
        queryParam.setVideoId(videoId);
        queryParam.setPaginationParam(request);
        List<VideoCommentInfo> comments = videoCommentMapper.getVideoCommentsByVideoId(queryParam);

        long commentCount = videoCommentMapper.countCommentsByVideoId(videoId);

        PaginatedData<VideoCommentInfo> paginatedData = new PaginatedData<>();
        paginatedData.setData(comments);
        paginatedData.setTotal(commentCount);

        ServiceResponse<PaginatedData<VideoCommentInfo>> response = ServiceResponse.buildSuccessResponse(paginatedData);
        response.putExtra("size", comments.size());

        return response;
    }

    public ServiceResponse<Boolean> deleteComment(@Valid DeleteCommentsRequest request)
    {
        long commentId = request.getId();

        // Validate if comment id exists
        UserVideoComment commentInfo = videoCommentMapper.getCommentById(commentId);
        if (commentInfo == null)
            return ServiceResponse.buildErrorResponse(-1, "Invalid video ID: " + commentId);

        // Validate if the current user id is the creator of the comment
        if (UserContextService.getCurrentUser().getId() != commentInfo.getCreatorId())
            return ServiceResponse.buildErrorResponse(-2, "You can not delete this comment because you are not the creator of the comment.");

        // Delete comment
        videoCommentMapper.deleteCommentById(commentId);

        return ServiceResponse.buildSuccessResponse(true);
    }
}
