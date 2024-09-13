package stark.stellasearch.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.domain.UserVideoComment;
import stark.stellasearch.dto.params.AddCommentsRequest;
import stark.stellasearch.dto.params.GetVideoCommentsRequest;
import stark.stellasearch.service.CommentService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/comment")
public class CommentController
{
    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public ServiceResponse<Boolean> addComment(@RequestBody AddCommentsRequest request)
    {
        return commentService.addComment(request);
    }

    @GetMapping("/list")
    public ServiceResponse<List<UserVideoComment>> getVideoCommentsById(@ModelAttribute GetVideoCommentsRequest request)
    {
        return commentService.getVideoCommentById(request);
    }
}
