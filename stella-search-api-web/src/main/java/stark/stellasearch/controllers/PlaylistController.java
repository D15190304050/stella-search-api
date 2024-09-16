package stark.stellasearch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.domain.UserVideoFavorites;
import stark.stellasearch.domain.UserVideoPlaylist;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.PlaylistWithVideoCount;
import stark.stellasearch.service.PlaylistService;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping("/playlist")
public class PlaylistController
{
    // TODO: Implement playlist controller
    // 1. 将视频加入收藏夹(默认收藏夹)--
    // 2. 将视频从收藏夹中移除--
    // 3. 添加收藏夹--
    // 4. 删除收藏夹（连同删除视频）--
    // 5. 修改收藏夹信息--
    // 6. 展示收藏夹列表--
    // 7. 展示收藏夹中的视频列表--
    @Autowired
    private PlaylistService playlistService;

    @PostMapping("/create")
    public ServiceResponse<UserVideoPlaylist> createPlaylist(@RequestBody CreatePlaylistRequest request)
    {
        return playlistService.createPlaylist(request);
    }

    @PostMapping("/delete")
    public ServiceResponse<Boolean> deletePlaylist(@RequestBody DeletePlaylistRequest request)
    {
        return playlistService.deletePlaylist(request);
    }

    @PostMapping("/modify")
    public ServiceResponse<Boolean> modifyPlaylist(@RequestBody ModifyPlaylistRequest request)
    {
        return playlistService.modifyPlaylist(request);
    }

    @GetMapping("/list")
    public ServiceResponse<List<PlaylistWithVideoCount>> getPlaylistList()
    {
        return playlistService.getPlaylist();
    }

    @PostMapping("/add-video")
    public ServiceResponse<Long> addVideoToPlaylist(@RequestBody AddVideoToPlaylistRequest request)
    {
        return playlistService.addVideoToPlaylist(request);
    }

    @PostMapping("/remove-video")
    public ServiceResponse<Boolean> removeVideoFromPlaylist(@RequestBody RemoveVideoFromPlaylistRequest request)
    {
        return playlistService.removeVideoFromPlaylist(request);
    }

    @GetMapping("/show-favorites")
    public ServiceResponse<List<UserVideoFavorites>> showFavoritesByPlaylist(@ModelAttribute ShowFavoritesByPlaylistRequest request)
    {
        return playlistService.showFavoritesByPlaylist(request);
    }

}
