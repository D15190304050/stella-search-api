package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.Date;

@Data
public class PlaylistInfo
{
    private long id;
    private long userId;
    private String name;
    private String description;
    private long videoCount;
}
