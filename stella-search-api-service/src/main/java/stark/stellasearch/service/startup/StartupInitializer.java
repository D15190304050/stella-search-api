package stark.stellasearch.service.startup;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import stark.stellasearch.service.VideoUploadingOptionHolder;

/**
 * Initializer of the application, run only once, just after startup of the application.
 */
@Component
public class StartupInitializer implements ApplicationContextAware, ApplicationListener<WebServerInitializedEvent>
{
    @Autowired
    private VideoUploadingOptionHolder videoUploadingOptionHolder;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {

    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event)
    {
        try
        {
            videoUploadingOptionHolder.setVideoUploadingOptions();
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
