package stark.stellasearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import stark.dataworks.basic.data.json.JsonSerializer;
import stark.dataworks.boot.web.PaginatedData;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dao.UserVideoFavoritesMapper;
import stark.stellasearch.dao.UserVideoInfoMapper;
import stark.stellasearch.dao.UserVideoPlaylistMapper;
import stark.stellasearch.domain.UserVideoFavorites;
import stark.stellasearch.domain.UserVideoInfo;
import stark.stellasearch.domain.UserVideoPlaylist;
import stark.stellasearch.dto.params.*;
import stark.stellasearch.dto.results.PlaylistInfo;

import jakarta.validation.Valid;
import stark.stellasearch.dto.results.PlaylistWithVideoCheck;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class PlaylistService
{
    /**
     * At most 20 playlists for each user.
     */
    public static final long MAX_PLAYLIST_COUNT = 20L;

    public static final String DEFAULT_PLAYLIST_NAME = "Default";
    public static final String DEFAULT_PLAYLIST_DESCRIPTION = "Default playlist";

    @Autowired
    private UserVideoPlaylistMapper userVideoPlaylistMapper;

    @Autowired
    private UserVideoInfoMapper userVideoInfoMapper;

    @Autowired
    private UserVideoFavoritesMapper userVideoFavoritesMapper;

    @Transactional(rollbackFor = Exception.class)
    public ServiceResponse<PlaylistWithVideoCheck> createPlaylist(@Valid CreatePlaylistRequest request)
    {
        long userId = UserContextService.getCurrentUser().getId();

        if (userVideoPlaylistMapper.countPlaylistByUserId(userId) >= MAX_PLAYLIST_COUNT)
            return ServiceResponse.buildErrorResponse(-1, "You can only create up to 20 playlists.");

        String playlistName = request.getName();
        String description = request.getDescription();
        UserVideoPlaylist newPlaylist = addPlaylistForUser(playlistName, description, userId);

        PlaylistWithVideoCheck playlistInfo = new PlaylistWithVideoCheck();
        BeanUtils.copyProperties(newPlaylist, playlistInfo);
        playlistInfo.setVideoCount(0);
        playlistInfo.setVideoId(-1);
        playlistInfo.setContainsVideo(false);

        return ServiceResponse.buildSuccessResponse(playlistInfo);
    }

    public UserVideoPlaylist addPlaylistForUser(String name, String description, long userId)
    {
        UserVideoPlaylist playlist = new UserVideoPlaylist();
        Date now = new Date();
        playlist.setName(name);
        playlist.setDescription(description);
        playlist.setUserId(userId);
        playlist.setCreatorId(userId);
        playlist.setModifierId(userId);
        playlist.setCreationTime(now);
        playlist.setModificationTime(now);

        userVideoPlaylistMapper.insert(playlist);
        return playlist;
    }

    public ServiceResponse<Boolean> addVideoToPlaylist(@Valid AddVideoToPlaylistRequest request)
    {
        long videoId = request.getVideoId();
        long playlistId = request.getPlaylistId();
        long userId = UserContextService.getCurrentUser().getId();

        // Validate if the video exists.
        long videoCount = userVideoInfoMapper.countVideoById(videoId);
        if (videoCount == 0)
            return ServiceResponse.buildErrorResponse(-1, "The video does not exist.");

        // Validate if the playlist exists.
        long playlistCount = userVideoPlaylistMapper.countPlaylistById(playlistId);
        if (playlistCount == 0)
            return ServiceResponse.buildErrorResponse(-1, "The playlist does not exist.");

        // Validate if the video is already in the playlist.
        long videoCountInPlaylist = userVideoFavoritesMapper.countVideoInPlaylist(videoId, playlistId, userId);
        if (videoCountInPlaylist > 0)
            return ServiceResponse.buildErrorResponse(-1, "The video is already in the playlist.");

        insertVideoFavorite(userId, videoId, playlistId);

        return ServiceResponse.buildSuccessResponse(true);
    }

    private void insertVideoFavorite(long userId, long videoId, long playlistId)
    {
        UserVideoFavorites favorite = new UserVideoFavorites();
        favorite.setUserId(userId);
        favorite.setVideoId(videoId);
        favorite.setPlaylistId(playlistId);
        favorite.setCreatorId(userId);
        favorite.setModifierId(userId);

        userVideoFavoritesMapper.insert(favorite);
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

    public ServiceResponse<List<PlaylistInfo>> getPlaylist()
    {
        long userId = UserContextService.getCurrentUser().getId();
        List<PlaylistInfo> userPlaylists = userVideoPlaylistMapper.getPlaylistsByUserId(userId);

        ServiceResponse<List<PlaylistInfo>> response = ServiceResponse.buildSuccessResponse(userPlaylists);
        response.putExtra("PlaylistCount", userPlaylists.size());
        return response;
    }

    public ServiceResponse<PaginatedData<UserVideoFavorites>> showFavoritesByPlaylist(@Valid ShowFavoritesByPlaylistRequest request)
    {
        long playlistId = request.getPlaylistId();

        // Validate if the playlist exists.
        long playlistCount = userVideoPlaylistMapper.countPlaylistById(playlistId);
        if (playlistCount == 0)
            return ServiceResponse.buildErrorResponse(-1, "The playlist with id " + playlistId + " does not exist.");

        long userId = UserContextService.getCurrentUser().getId();
        GetUserFavoritesByPlaylistIdParam favoritesByPlaylistIdParam = new GetUserFavoritesByPlaylistIdParam();
        favoritesByPlaylistIdParam.setPlaylistId(playlistId);
        favoritesByPlaylistIdParam.setPaginationParam(request);
        favoritesByPlaylistIdParam.setUserId(userId);
        
        List<UserVideoFavorites> favoritesList = userVideoFavoritesMapper.getFavoritesByPlaylistId(favoritesByPlaylistIdParam);
        long favoritesCount = userVideoFavoritesMapper.countVideoByPlaylistId(playlistId, userId);
        PaginatedData<UserVideoFavorites> paginatedData = new PaginatedData<>();
        paginatedData.setData(favoritesList);
        paginatedData.setTotal(favoritesCount);

        ServiceResponse<PaginatedData<UserVideoFavorites>> response = ServiceResponse.buildSuccessResponse(paginatedData);
        response.putExtra("pageSize", favoritesList.size());

        return response;
    }

    public ServiceResponse<List<PlaylistWithVideoCheck>> getPlaylistWithVideoChecks(long videoId)
    {
        long userId = UserContextService.getCurrentUser().getId();

        long videoCount = userVideoInfoMapper.countVideoById(videoId);
        if (videoCount == 0)
            return ServiceResponse.buildErrorResponse(-1, "The video with ID: " + videoCount + " does not exist.");

        List<PlaylistWithVideoCheck> playlistWithVideoChecks = userVideoPlaylistMapper.getPlaylistWithVideoChecks(userId, videoId);
        return ServiceResponse.buildSuccessResponse(playlistWithVideoChecks);
    }

    @Transactional(rollbackFor = Exception.class)
    public ServiceResponse<Boolean> setVideoFavorites(@Valid SetVideoFavoritesRequest request)
    {
        long userId = UserContextService.getCurrentUser().getId();
        List<Long> playlistIds = request.getPlaylistIds();
        long videoId = request.getVideoId();

        // Validate if the video ID exists.
        long videoCount = userVideoInfoMapper.countVideoById(videoId);
        if (videoCount == 0)
            return ServiceResponse.buildErrorResponse(-1, "The video with ID: " + videoCount + " does not exist.");

        // Delete all records of <userId, videoId> if playlistIds is empty.
        // Otherwise, remove playlist from favorites whose ID are not listed, add into playlist favorites whose ID are listed.
        if (CollectionUtils.isEmpty(playlistIds))
        {
            userVideoFavoritesMapper.deleteFavoritesByUserAndVideoId(userId, videoId);
            return ServiceResponse.buildSuccessResponse(true);
        }
        else
        {
            // Validate if the playlist IDs exist and belongs to the current user.
            List<Long> userPlaylistIds = userVideoPlaylistMapper.getPlaylistIdsByUserId(userId);

            // TODO: Implement set in dataworks, and then replace with standard library API here.
            HashSet<Long> playlistIdSet = new HashSet<>(playlistIds);
            userPlaylistIds.forEach(playlistIdSet::remove);

            if (!playlistIdSet.isEmpty())
                return ServiceResponse.buildErrorResponse(-15, "You do not have playlist(s) with following IDs: " + JsonSerializer.serialize(playlistIdSet));

            // Remove <userId, videoId, playlistId> from favorites.
            userVideoFavoritesMapper.deleteFavoritesNotInRange(userId, videoId, playlistIds);

            // Add <userId, videoId, playlistId> into favorites.
            userVideoFavoritesMapper.addFavoritesInRange(userId, videoId, playlistIds);
            
            return ServiceResponse.buildSuccessResponse(true);
        }
    }
}
