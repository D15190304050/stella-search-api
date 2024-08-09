package stark.stellasearch.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public class VideoLabel extends DomainBase
{
    /**
     * The label.
     */
    private String label;
}
