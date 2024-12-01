package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VideoPlayInfo
{
    private long id;
    private String nameInOss;
    private String title;
    private String coverUrl;
    private String introduction;
    private long creatorId;
    private String creatorName;
    private Date creationTime;
    private Date modificationTime;
    private long playCount;
    private long favoritesCount;
    private long likeCount;
    private long commentCount;
    private String videoPlayUrl;
    private boolean userLikes;
    private boolean userFavorites;
    private List<String> labels;
    private String summary;
}