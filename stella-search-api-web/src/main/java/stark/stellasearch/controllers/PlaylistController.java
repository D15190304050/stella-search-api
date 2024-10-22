package stark.stellasearch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import stark.dataworks.boot.web.PaginatedData;
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
    // 1. Put a video into a playlist.
    // 2. Remove a video from a playlist.
    // 3. Create a new playlist.
    // 4. Delete a playlist with videos.
    // 5. Modify a playlist.
    // 6. List all playlists.
    // 7. Show favorites by playlist.

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
    public ServiceResponse<PaginatedData<UserVideoFavorites>> showFavoritesByPlaylist(@ModelAttribute ShowFavoritesByPlaylistRequest request)
    {
        return playlistService.showFavoritesByPlaylist(request);
    }

}
