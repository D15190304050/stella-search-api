package stark.stellasearch.dto.results;

import lombok.Data;

import java.util.List;

@Data
public class VideoUploadingOption
{
    List<DropDownOption<Long>> creationTypeOptions;
    List<DropDownOption<Long>> videoLabelOptions;
    List<DropDownOption<Long>> videoSectionOptions;
}
