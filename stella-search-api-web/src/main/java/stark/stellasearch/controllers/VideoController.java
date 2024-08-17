package stark.stellasearch.controllers;

import io.minio.errors.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.VideoInfo;
import stark.stellasearch.dto.results.VideoUploadingOption;
import stark.stellasearch.service.ImageService;
import stark.stellasearch.service.VideoService;
import stark.stellasearch.service.VideoUploadingOptionHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequestMapping("/video")
public class VideoController
{
    @Autowired
    private VideoService videoService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private VideoUploadingOptionHolder videoUploadingOptionHolder;

    @GetMapping("/generate-task")
    public ServiceResponse<String> generateNewVideoUploadingTask(@ModelAttribute NewVideoUploadingTaskRequest request)
    {
        return videoService.generateNewVideoUploadingTask(request);
    }

    @PostMapping("/upload-chunk")
    public ServiceResponse<Boolean> uploadVideoChunk(@ModelAttribute VideoChunkUploadingRequest request, @RequestPart("videoChunk") MultipartFile videoChunk) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        request.setVideoChunk(videoChunk);
        return videoService.uploadVideoChunk(request);
    }

    @PostMapping("/compose-chunks")
    public ServiceResponse<Long> composeVideoChunks(@RequestBody ComposeVideoChunksRequest request) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        return videoService.composeVideoChunks(request);
    }

    @GetMapping("/uploading-options")
    public ServiceResponse<VideoUploadingOption> getVideoUploadingOptions()
    {
        return videoUploadingOptionHolder.getVideoUploadingOptions();
    }

    @PostMapping("/upload-cover")
    public ServiceResponse<String> uploadVideoCover(@RequestParam("coverFile") MultipartFile coverFile)
    {
        return imageService.uploadVideoCover(coverFile);
    }

    @GetMapping("/cover/{coverFileName}")
    public void getVideoCover(@PathVariable("coverFileName") String coverFileName, HttpServletResponse response)
    {
        imageService.getImage(coverFileName, response);
    }

    @PostMapping("/set-info")
    public ServiceResponse<Boolean> setVideoInfo(@RequestBody SetVideoInfoRequest request)
    {
        return videoService.setVideoInfo(request);
    }

    @GetMapping("/list")
    public ServiceResponse<List<VideoInfo>> getVideoInfoOfCurrentUser(@ModelAttribute PaginationParam paginationParam)
    {
        return videoService.getVideoInfoOfCurrentUser(paginationParam);
    }

    @GetMapping("/count")
    public ServiceResponse<Long> countVideoByUserId()
    {
        return videoService.countVideoByUserId();
    }

    @PutMapping("/update")
    public ServiceResponse<Boolean> updateVideoInfo(@RequestBody SetVideoInfoRequest request)
    {
        return videoService.updateVideoInfo(request);
    }

    @GetMapping("/info")
    public ServiceResponse<SetVideoInfoRequest> getVideoBaseInfoById(@RequestParam("videoId") long videoId)
    {
        return videoService.getVideoBaseInfoById(videoId);
    }

    @GetMapping("/stream/{videoId}")
    public ServiceResponse<String> getVideoPlayUrlByNameInOss(@PathVariable("videoId") long videoId) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException
    {
        return videoService.getVideoPlayUrlById(videoId);
    }
}
