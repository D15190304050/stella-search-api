package stark.stellasearch.dao;

import org.apache.ibatis.annotations.Mapper;
import stark.stellasearch.domain.VideoCreationType;
import stark.stellasearch.domain.VideoLabel;
import stark.stellasearch.domain.VideoSection;

import java.util.List;

@Mapper
public interface VideoUploadingOptionMapper
{
    List<VideoCreationType> getAllVideoCreationTypes();
    List<VideoLabel> getAllVideoLabels();
    List<VideoSection> getAllVideoSections();
}
