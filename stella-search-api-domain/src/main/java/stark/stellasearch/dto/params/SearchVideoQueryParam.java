package stark.stellasearch.dto.params;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchVideoQueryParam extends PaginationQueryParam
{
    private String keyword;
}
