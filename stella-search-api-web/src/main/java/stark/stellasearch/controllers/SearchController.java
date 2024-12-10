package stark.stellasearch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.params.SearchVideoRequest;
import stark.stellasearch.dto.results.VideoPlayInfo;
import stark.stellasearch.service.VideoSearchService;

import java.io.IOException;

@RestController
@RequestMapping("/search")
public class SearchController
{
    @Autowired
    private VideoSearchService videoSearchService;

    @GetMapping("/search-video")
    public ServiceResponse<PaginatedData<VideoPlayInfo>> searchVideo(@ModelAttribute SearchVideoRequest request) throws IOException, InterruptedException
    {
        return videoSearchService.searchVideo(request);
    }
}
