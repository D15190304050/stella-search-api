package stark.stellasearch.service;

import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.FastByteArrayOutputStream;
import stark.dataworks.basic.data.redis.RedisQuickOperation;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.dto.results.CaptchaResponse;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@LogArgumentsAndResponse
public class CaptchaService
{
    @Autowired
    private Producer captchaProducer;

    @Autowired
    private RedisQuickOperation redisQuickOperation;

    public ServiceResponse<CaptchaResponse> generateCaptcha(HttpServletResponse response) throws IOException
    {
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        String captchaText = captchaProducer.createText();
        BufferedImage captchaImage = captchaProducer.createImage(captchaText);
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        ImageIO.write(captchaImage, "jpg", outputStream);
        String captchaBytes = "data:image/jpeg;base64," + Base64Utils.encodeToString(outputStream.toByteArray());

        String captchaId = UUID.randomUUID().toString();
        redisQuickOperation.set(captchaId, captchaText, 5, TimeUnit.MINUTES);

        log.info("Captcha text = {}", captchaText);

        CaptchaResponse captchaResponse = new CaptchaResponse(captchaId, captchaBytes);
        return ServiceResponse.buildSuccessResponse(captchaResponse);
    }
}
