package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.UserVideoPlaylist;
import stark.stellasearch.dto.results.PlaylistInfo;
import stark.stellasearch.dto.results.PlaylistWithVideoCheck;

import java.util.List;

@Mapper
public interface UserVideoPlaylistMapper
{
    long countPlaylistByUserId(long userId);
    int insert(UserVideoPlaylist userVideoPlaylist);
    int delete(long id);
    UserVideoPlaylist getPlaylistById(long id);
    long countPlaylistById(long id);
    List<Long> getPlaylistIdsByUserId(long userId);
    int update(UserVideoPlaylist userVideoPlaylist);
    List<PlaylistInfo> getPlaylistsByUserId(long userId);
    List<PlaylistWithVideoCheck> getPlaylistWithVideoChecks(@Param("userId") long userId, @Param("videoId") long videoId);
}
