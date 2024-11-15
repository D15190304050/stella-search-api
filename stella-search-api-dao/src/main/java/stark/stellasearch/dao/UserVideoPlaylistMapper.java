package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import stark.stellasearch.domain.UserVideoPlaylist;
import stark.stellasearch.dto.params.ModifyPlaylistInfoCommandParam;
import stark.stellasearch.dto.results.PlaylistInfo;
import stark.stellasearch.dto.results.PlaylistWithVideoCheck;

import java.util.List;

@Mapper
public interface UserVideoPlaylistMapper
{
    long countPlaylistByUserId(long userId);
    int insert(UserVideoPlaylist userVideoPlaylist);
    long deletePlaylistById(long id);
    UserVideoPlaylist getPlaylistById(long id);
    long countPlaylistById(long id);
    long countPlaylistByIdAndUserId(@Param("id") long id, @Param("userId") long userId);
    List<Long> getPlaylistIdsByUserId(long userId);
    int update(ModifyPlaylistInfoCommandParam userVideoPlaylist);
    List<PlaylistInfo> getPlaylistsByUserId(long userId);
    List<PlaylistWithVideoCheck> getPlaylistWithVideoChecks(@Param("userId") long userId, @Param("videoId") long videoId);
}
