package stark.stellasearch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoCreationType extends DomainBase
{
    /**
     * Video creation type: 0 - Original; 1 - Reprinting.
     */
    private String type;
}
