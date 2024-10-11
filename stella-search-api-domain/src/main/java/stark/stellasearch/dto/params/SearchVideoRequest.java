package stark.stellasearch.dto.params;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SearchVideoRequest extends PaginationRequestParam
{
    @NotBlank(message = "Keyword must not be null.")
    private String keyword;
}
