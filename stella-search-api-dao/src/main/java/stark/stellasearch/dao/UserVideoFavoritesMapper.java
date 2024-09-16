package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.UserVideoFavorites;
import stark.stellasearch.dto.params.GetUserFavoritesByPlaylistIdParam;

import java.util.List;

@Mapper
public interface UserVideoFavoritesMapper
{
    int add(UserVideoFavorites userVideoFavorites);
    int delete(@Param("videoId") long videoId, @Param("playlistId") long playlistId, @Param("userId") long userId);
    UserVideoFavorites getVideoByVideoIdAndPlaylistId(@Param("videoId") long videoId, @Param("playlistId") long playlistId, @Param("userId") long userId);
    List<UserVideoFavorites> getFavoritesByPlaylistId(GetUserFavoritesByPlaylistIdParam userFavoritesByPlaylistIdParam);
}
