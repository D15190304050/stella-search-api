package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.Date;

@Data
public class VideoPlayInfo
{
    private long id;
    private String nameInOss;
    private String title;
    private String coverUrl;
    private String introduction;
    private Date creationTime;
    private Date uploadedTime;
    private long playCount;
    private long favoritesCount;
    private long likeCount;
    private long commentCount;
    private String videoPlayUrl;
}