package stark.stellasearch.service.doubao;

import com.volcengine.ark.runtime.service.ArkService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Setter
@Service
public class DoubaoMultiRoundChatSessionFactory
{
    private String doubaoApiKey;
    private ArkService arkService;

    @Value("${doubao.model-endpoint}")
    private String modelEndpoint;

    public DoubaoMultiRoundChatSession build()
    {
        return new DoubaoMultiRoundChatSession(arkService, modelEndpoint);
    }
}
