package stark.stellasearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"stark.stellasearch", "stark.dataworks.boot.autoconfig"})
public class StellaSearchMain
{
    public static void main(String[] args)
    {
        SpringApplication.run(StellaSearchMain.class);
    }
}
