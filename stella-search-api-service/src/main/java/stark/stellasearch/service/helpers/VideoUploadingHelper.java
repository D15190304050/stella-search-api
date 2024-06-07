package stark.stellasearch.service.helpers;

import stark.stellasearch.service.UserContextService;
import stark.stellasearch.service.dto.User;

public class VideoUploadingHelper
{
    private VideoUploadingHelper()
    {}

    public static String getVideoUploadingTaskIdPrefix(String videoName)
    {
        User user = UserContextService.getCurrentUser();
        long userId = user.getId();
        String taskIdPrefix = userId + "-" + videoName;
        return null;
    }
}
