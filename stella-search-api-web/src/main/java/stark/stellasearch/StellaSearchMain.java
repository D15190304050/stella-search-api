package stark.stellasearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"stark.stellasearch", "stark.dataworks.boot.autoconfig"})
public class StellaSearchMain
{
    public static void main(String[] args)
    {
        SpringApplication.run(StellaSearchMain.class);
    }
}
