package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.UserVideoPlaylist;
import stark.stellasearch.dto.results.PlaylistWithVideoCount;

import java.util.List;

@Mapper
public interface UserVideoPlaylistMapper
{
    int countPlaylistByUserId(long userId);
    int insert(UserVideoPlaylist userVideoPlaylist);
    int delete(long id);
    UserVideoPlaylist getPlaylistById(long id);
    int update(UserVideoPlaylist userVideoPlaylist);
    List<PlaylistWithVideoCount> getPlaylistsByUserId(long userId);
}
