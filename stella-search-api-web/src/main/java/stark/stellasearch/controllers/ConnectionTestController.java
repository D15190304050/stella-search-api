package stark.stellasearch.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/connection")
public class ConnectionTestController
{
    @GetMapping("/hello")
    public String hello()
    {
        return "Hello";
    }
}
