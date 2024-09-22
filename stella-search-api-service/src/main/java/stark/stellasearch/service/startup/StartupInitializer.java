package stark.stellasearch.service.startup;

import com.volcengine.ark.runtime.service.ArkService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import stark.stellasearch.service.doubao.DoubaoMultiRoundChatSessionFactory;
import stark.stellasearch.service.VideoUploadingOptionHolder;

/**
 * Initializer of the application, run only once, just after startup of the application.
 */
@Component
public class StartupInitializer implements ApplicationContextAware, ApplicationListener<WebServerInitializedEvent>
{
    @Value("${doubao.api-key-evn-variable-name}")
    private String doubaoApiKeyEnvironmentVariableName;

    @Autowired
    private DoubaoMultiRoundChatSessionFactory doubaoMultiRoundChatSessionFactory;

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
            String doubaoApiKey = System.getenv(doubaoApiKeyEnvironmentVariableName);
            doubaoMultiRoundChatSessionFactory.setDoubaoApiKey(doubaoApiKey);
            doubaoMultiRoundChatSessionFactory.setArkService(ArkService.builder().apiKey(doubaoApiKey).build());

            videoUploadingOptionHolder.setVideoUploadingOptions();
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
