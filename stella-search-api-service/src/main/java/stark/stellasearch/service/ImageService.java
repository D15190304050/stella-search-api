package stark.stellasearch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stark.dataworks.boot.ExceptionLogger;
import stark.dataworks.boot.autoconfig.minio.EasyMinio;
import stark.dataworks.boot.autoconfig.web.LogArgumentsAndResponse;
import stark.dataworks.boot.web.ServiceResponse;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@LogArgumentsAndResponse
public class ImageService
{
    public static final List<String> ACCEPTABLE_IMAGE_FILE_SUFFIXES;
    public static final String ACCEPTABLE_IMAGE_FILE_SUFFIX_TEXT;

    @Value("${dataworks.easy-minio.bucket-name-images}")
    private String bucketNameImages;

    @Value("${images.avatar-url-prefix}")
    private String avatarUrlPrefix;

    @Value("${images.video-cover-prefix}")
    private String videoCoverUrlPrefix;

    @Autowired
    private EasyMinio easyMinio;

    static
    {
        ACCEPTABLE_IMAGE_FILE_SUFFIXES = new ArrayList<>()
        {{
            add(".jpg");
            add(".jpeg");
            add(".png");
        }};

        ACCEPTABLE_IMAGE_FILE_SUFFIX_TEXT = String.join(", ", ACCEPTABLE_IMAGE_FILE_SUFFIXES);
    }

    public ServiceResponse<String> uploadAvatar(MultipartFile avatarFile)
    {
        return uploadImage(avatarFile, avatarUrlPrefix);
    }

    public void getImage(String avatarFileName, HttpServletResponse response)
    {
        try
        {
            InputStream avatarFileInputStream = easyMinio.getObjectInputStream(bucketNameImages, avatarFileName);
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            ServletOutputStream outputStream = response.getOutputStream();

            byte[] bytes = new byte[1024];
            int readLength;
            while ((readLength = avatarFileInputStream.read(bytes)) > 0)
                outputStream.write(bytes, 0, readLength);

            outputStream.close();
            avatarFileInputStream.close();
        }
        catch (Exception e)
        {
            ExceptionLogger.logExceptionInfo(e);
        }
    }

    public ServiceResponse<String> uploadVideoCover(MultipartFile coverFile)
    {
        return uploadImage(coverFile, videoCoverUrlPrefix);
    }

    private ServiceResponse<String> uploadImage(MultipartFile imageFile, String urlPrefix)
    {
        if (imageFile == null)
            return ServiceResponse.buildErrorResponse(-3, "Argument null.");

        String originalFilename = imageFile.getOriginalFilename();

        if (originalFilename == null)
            return ServiceResponse.buildErrorResponse(-4, "Original file name is null.");

        int lastIndexOfDot = originalFilename.lastIndexOf(".");
        String fileSuffix = originalFilename.substring(lastIndexOfDot);

        if (!ACCEPTABLE_IMAGE_FILE_SUFFIXES.contains(fileSuffix))
            return ServiceResponse.buildErrorResponse(-5, String.format("Unacceptable image suffix: %s, acceptable file formats are: %s.", fileSuffix, ACCEPTABLE_IMAGE_FILE_SUFFIX_TEXT));

        String imageFileName = UUID.randomUUID().toString();
        imageFileName += fileSuffix;

        try
        {
            easyMinio.uploadFileByStream(bucketNameImages, imageFileName, imageFile.getInputStream());
        }
        catch (Exception e)
        {
            ExceptionLogger.logExceptionInfo(e);
            return ServiceResponse.buildErrorResponse(-1, "Upload failure, see log for more information.");
        }

        String imageUrl = urlPrefix + imageFileName;
        return ServiceResponse.buildSuccessResponse(imageUrl);
    }
}
