package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.Date;

@Data
public class VideoInfo
{
    private long id;
    private String videoUrl;
    private String title;
    private String coverUrl;
    private String introduction;
    private Date uploadedTime;
    private long playCount;
    private long favoritesCount;
    private long likeCount;
    private long commentCount;
}