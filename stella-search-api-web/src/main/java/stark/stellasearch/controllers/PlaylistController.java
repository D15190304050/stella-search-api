package stark.stellasearch.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.PlaylistInfo;
import stark.stellasearch.dto.results.PlaylistWithVideoCheck;
import stark.stellasearch.service.PlaylistService;

import java.util.List;

@RestController
@RequestMapping("/playlist")
public class PlaylistController
{
    @Autowired
    private PlaylistService playlistService;

    @PostMapping("/create")
    public ServiceResponse<PlaylistWithVideoCheck> createPlaylist(@RequestBody CreatePlaylistRequest request)
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
    public ServiceResponse<List<PlaylistInfo>> getPlaylistListsOfCurrentUser()
    {
        return playlistService.getPlaylistsOfCurrentUser();
    }

    @PostMapping("/remove-video")
    public ServiceResponse<Boolean> removeVideoFromPlaylist(@RequestBody RemoveVideoFromPlaylistRequest request)
    {
        return playlistService.removeVideoFromPlaylist(request);
    }

    @GetMapping("/playlist-with-checks")
    public ServiceResponse<List<PlaylistWithVideoCheck>> getPlaylistWithVideoChecks(@RequestParam("videoId") long videoId)
    {
        return playlistService.getPlaylistWithVideoChecks(videoId);
    }

    @PostMapping("/set-favorites")
    public ServiceResponse<Boolean> setVideoFavorites(@RequestBody SetVideoFavoritesRequest request)
    {
        return playlistService.setVideoFavorites(request);
    }
}
