package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.Date;

@Data
public class PlaylistWithVideoCount
{
    private long id;
    private long userId;
    private String name;
    private String description;
    private Date creationTime;
    private long modifierId;
    private Date modificationTime;
    private long videoCount;
}
