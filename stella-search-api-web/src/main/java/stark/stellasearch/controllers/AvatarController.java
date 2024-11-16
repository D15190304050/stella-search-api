package stark.stellasearch.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import stark.dataworks.boot.web.ServiceResponse;
import stark.stellasearch.service.ImageService;

import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/avatar")
public class AvatarController
{
    @Autowired
    private ImageService imageService;

    @PostMapping("/upload")
    public ServiceResponse<String> uploadAvatar(@RequestParam("avatarFile") MultipartFile avatarFile)
    {
        return imageService.uploadAvatar(avatarFile);
    }

    @GetMapping("/{avatarFileName}")
    public void getAvatar(@PathVariable("avatarFileName") String avatarFileName, HttpServletResponse response)
    {
        imageService.getImage(avatarFileName, response);
    }
}
