package stark.stellasearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"stark.stellasearch", "stark.dataworks.boot.autoconfig"})
@EnableTransactionManagement
public class StellaSearchMain
{
    public static void main(String[] args)
    {
        SpringApplication.run(StellaSearchMain.class);
    }
}
