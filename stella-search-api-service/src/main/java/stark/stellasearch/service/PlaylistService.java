package stark.stellasearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.UserVideoFavoritesMapper;
import stark.stellasearch.dao.UserVideoInfoMapper;
import stark.stellasearch.dao.UserVideoPlaylistMapper;
import stark.stellasearch.domain.UserVideoFavorites;
import stark.stellasearch.domain.UserVideoInfo;
import stark.stellasearch.domain.UserVideoPlaylist;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.PlaylistWithVideoCount;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class PlaylistService
{
    public static final int MAX_PLAYLISTS = 20;

    @Autowired
    private UserVideoPlaylistMapper userVideoPlaylistMapper;

    @Autowired
    private UserVideoInfoMapper userVideoInfoMapper;

    @Autowired
    private UserVideoFavoritesMapper userVideoFavoritesMapper;

    public ServiceResponse<UserVideoPlaylist> createPlaylist(@Valid CreatePlaylistRequest request)
    {
        // The playlists are limited to 20.
        if (userVideoPlaylistMapper.countPlaylistByUserId(UserContextService.getCurrentUser().getId()) >= MAX_PLAYLISTS)
            return ServiceResponse.buildErrorResponse(-1, "You can only create up to 20 playlists.");

        UserVideoPlaylist playlist = new UserVideoPlaylist();
        Date now = new Date();
        playlist.setName(request.getName());
        playlist.setDescription(request.getDescription());
        playlist.setUserId(UserContextService.getCurrentUser().getId());
        playlist.setCreatorId(UserContextService.getCurrentUser().getId());
        playlist.setModifierId(UserContextService.getCurrentUser().getId());
        playlist.setCreationTime(now);
        playlist.setModificationTime(now);

        userVideoPlaylistMapper.insert(playlist);

        return ServiceResponse.buildSuccessResponse(playlist);
    }

    public ServiceResponse<Long> addVideoToPlaylist(@Valid AddVideoToPlaylistRequest request)
    {
        // Validate if the video exists.
        UserVideoInfo video = userVideoInfoMapper.getVideoBaseInfoById(request.getVideoId());
        if (video == null)
            return ServiceResponse.buildErrorResponse(-1, "The video does not exist.");

        // Validate if the playlist exists.
        UserVideoPlaylist playlist = userVideoPlaylistMapper.getPlaylistById(request.getPlaylistId());
        if (playlist == null)
            return ServiceResponse.buildErrorResponse(-1, "The playlist does not exist.");

        // Validate if the video is already in the playlist.
        if (userVideoFavoritesMapper.getVideoByVideoIdAndPlaylistId(request.getVideoId(), request.getPlaylistId(), UserContextService.getCurrentUser().getId()) != null)
            return ServiceResponse.buildErrorResponse(-1, "The video is already in the playlist.");

        UserVideoFavorites favorite = new UserVideoFavorites();
        Date now = new Date();
        favorite.setUserId(playlist.getUserId());
        favorite.setVideoId(video.getId());
        favorite.setPlaylistId(playlist.getId());
        favorite.setCreatorId(video.getCreatorId());
        favorite.setModifierId(playlist.getUserId());
        favorite.setCreationTime(now);
        favorite.setModificationTime(now);

        userVideoFavoritesMapper.add(favorite);

        return ServiceResponse.buildSuccessResponse(favorite.getId());
    }

    public ServiceResponse<Boolean> removeVideoFromPlaylist(@Valid RemoveVideoFromPlaylistRequest request)
    {
        // Validate if the video exists.
        UserVideoInfo video = userVideoInfoMapper.getVideoBaseInfoById(request.getVideoId());
        if (video == null)
            return ServiceResponse.buildErrorResponse(-1, "The video does not exist.");

        // Validate if the playlist exists.
        UserVideoPlaylist playlist = userVideoPlaylistMapper.getPlaylistById(request.getPlaylistId());
        if (playlist == null)
            return ServiceResponse.buildErrorResponse(-1, "The playlist does not exist.");

        // Delete the video from the video favorites table.
        userVideoFavoritesMapper.delete(request.getVideoId(), request.getPlaylistId(), UserContextService.getCurrentUser().getId());

        return ServiceResponse.buildSuccessResponse(true);
    }

    public ServiceResponse<Boolean> deletePlaylist(@Valid DeletePlaylistRequest request)
    {
        // Validate if the playlist exists.
        UserVideoPlaylist playlist = userVideoPlaylistMapper.getPlaylistById(request.getId());
        if (playlist == null)
            return ServiceResponse.buildErrorResponse(-1, "The playlist does not exist.");

        // Delete the playlist and relevant videos
        userVideoPlaylistMapper.delete(request.getId());

        return ServiceResponse.buildSuccessResponse(true);
    }

    public ServiceResponse<Boolean> modifyPlaylist(@Valid ModifyPlaylistRequest request)
    {
        // Validate if the playlist exists.
        UserVideoPlaylist playlist = userVideoPlaylistMapper.getPlaylistById(request.getId());
        if (playlist == null)
            return ServiceResponse.buildErrorResponse(-1, "The playlist does not exist.");

        UserVideoPlaylist userVideoPlaylist = new UserVideoPlaylist();
        userVideoPlaylist.setId(request.getId());
        userVideoPlaylist.setModifierId(UserContextService.getCurrentUser().getId());
        userVideoPlaylist.setName(request.getName());
        userVideoPlaylist.setDescription(request.getDescription());
        userVideoPlaylist.setModificationTime(new Date());
        userVideoPlaylistMapper.update(userVideoPlaylist);

        return ServiceResponse.buildSuccessResponse(true);
    }

    public ServiceResponse<List<PlaylistWithVideoCount>> getPlaylist()
    {
        List<PlaylistWithVideoCount> UserPlaylists = userVideoPlaylistMapper.getPlaylistsByUserId(UserContextService.getCurrentUser().getId());
        log.info("The number of playlist is: {}", UserPlaylists.size());
        return ServiceResponse.buildSuccessResponse(UserPlaylists);
    }

    public ServiceResponse<List<UserVideoFavorites>> showFavoritesByPlaylist(@Valid ShowFavoritesByPlaylistRequest request)
    {
        // Validate if the playlist exists.
        UserVideoPlaylist playlist = userVideoPlaylistMapper.getPlaylistById(request.getPlaylistId());
        if (playlist == null)
            return ServiceResponse.buildErrorResponse(-1, "The playlist does not exist.");

        GetUserFavoritesByPlaylistIdParam favoritesByPlaylistIdParam = new GetUserFavoritesByPlaylistIdParam();
        favoritesByPlaylistIdParam.setPlaylistId(request.getPlaylistId());
        favoritesByPlaylistIdParam.setPageCapacity(request.getPageCapacity());
        favoritesByPlaylistIdParam.setOffset(request.getPageCapacity() * (request.getPageIndex() - 1));
        favoritesByPlaylistIdParam.setUserId(UserContextService.getCurrentUser().getId());
        List<UserVideoFavorites> favoritesList = userVideoFavoritesMapper.getFavoritesByPlaylistId(favoritesByPlaylistIdParam);
        ServiceResponse<List<UserVideoFavorites>> response = ServiceResponse.buildSuccessResponse(favoritesList);
        response.putExtra("total", favoritesList.size());

        return response;
    }

}
