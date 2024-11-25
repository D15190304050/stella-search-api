package stark.stellasearch.dto.params;

import lombok.Data;

@Data
public class ModifyPlaylistInfoCommandParam
{
    private long id;
    private String name;
    private String description;
    private long modifierId;
}
