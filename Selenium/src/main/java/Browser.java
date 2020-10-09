import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author wangz
 */
@Slf4j
public class Browser {
    private final WebDriver driver;

    public static volatile AtomicInteger sleepTime = new AtomicInteger(3);

    public Browser(boolean headless) {
        String property = System.getProperty("os.name");

        log.info("Systeam Version : {}", property);

        // todo driver 的路径要改成相对路径
        String windows = "Windows";
        if (property.contains(windows)) {
            System.setProperty("webdriver.gecko.driver", "C:\\Users\\wangz\\IdeaProjects\\monoid\\Selenium\\src\\main\\resources\\geckodriver.exe");
        } else {
            System.setProperty("webdriver.gecko.driver", "/mnt/hgfs/WindowsIdeaProjects/monoid/Selenium/src/main/resources/geckodriver");
        }

        FirefoxOptions opts = new FirefoxOptions();


        if (headless) {
            opts.addArguments("--headless");
        }

        this.driver = new FirefoxDriver(opts);
    }

    public void init(String url, String keyword) {
        if (driver == null) {
            log.error("failed to initialize driver");
            throw new NullPointerException();
        }

        driver.get(url);


        driver.findElement(By.id("searchType")).click();
        driver.findElement(By.xpath("/html/body/div[1]/ul/li/form/div/div[2]/div/div/ul/li[3]")).click();
        driver.findElement(By.id("keyword")).sendKeys(keyword);
        driver.findElement(By.id("searchbtn")).click();
    }

    public void destroy(Buffer buffer) {
        buffer.close(false);
        driver.close();
    }


    public void getData(Buffer buffer) throws Exception {

        int pageSize = 50;
        driver.findElement(By.id("page-size")).sendKeys(String.valueOf(pageSize));


        try {
            SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebElement jumpNum = driver.findElement(By.id("jumpnum"));

        int total = Integer.parseInt(driver.findElement(By.id("total_result")).getText());

        System.out.println("total = " + total);

        WebElement button = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/div[2]/div[2]/nav/ul[2]/li[7]/button"));

        for (int i = 1; (i <= (total / pageSize) + 1) && buffer.bufferInUse() ; i++) {
            jumpNum.clear();
            jumpNum.sendKeys(String.valueOf(i));
            button.click();


            try {
                SECONDS.sleep(sleepTime.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            buffer.addElement(driver.findElement(By.id("list")));

        }

        buffer.close(false);

    }

}
