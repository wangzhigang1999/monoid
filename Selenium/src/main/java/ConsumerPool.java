import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author wangz
 */
@Slf4j
public class ConsumerPool implements Runnable {
    private final int year;
    private final int threadNumber;
    private final Buffer buffer;
    private final ExecutorService poolExecutor;


    private final String windowsUrl = "jdbc:mysql://localhost:3306/monoid?user=root&password=root&serverTimezone=UTC";
    private final String linuxUrl = "jdbc:mysql://10.128.226.204:3306/monoid?user=wangzhigang&password=zhigang911&serverTimezone=UTC";
    private final String url = System.getProperty("os.name").contains("Windows") ? windowsUrl : linuxUrl;

    private static final AtomicInteger FAILED_CNT = new AtomicInteger(0);
    private static final AtomicInteger SUCCESS_CNT = new AtomicInteger(0);

    private static final int MIN_SLEEP_TIME = 3;

    private static final AtomicInteger CNT = new AtomicInteger(0);

    public ConsumerPool(Buffer buffer, int year, int threadNumber) {
        this.buffer = buffer;
        this.year = year;
        this.threadNumber = threadNumber;

        int coreSize = Runtime.getRuntime().availableProcessors();

        if (threadNumber > coreSize) {
            threadNumber = coreSize;
            log.info("The number of user-defined threads is greater than the actual number of CPU cores, and the CPU shall prevail.");
        }

        poolExecutor = Executors.newFixedThreadPool(threadNumber);

    }


    @Override
    public void run() {

        // 这里为每个线程都绑定一个connection
        try (Connection connection = DriverManager.getConnection(url)) {

            connection.setAutoCommit(true);

            String sql = "insert ignore  into monoid.bookinfo (href, name, writer, press, pressYear, isbn, star) VALUES (?,?,?,?,?,?,?)";

            PreparedStatement statement = connection.prepareStatement(sql);

            while (buffer.bufferInUse()) {

                WebElement element = buffer.getElement();

                if (element != null) {

                    for (WebElement webElement : element.findElements(By.className("tableList"))) {

                        try {
                            List<WebElement> td = webElement.findElements(By.tagName("td"));
                            if (td.size() == 6) {

                                String name = parseElement(statement, td);

                                int res = statement.executeUpdate();

                                if (res > 0) {
                                    log.info("当前爬取的书籍名称是: " + name.substring(0, 5) + "...  cnt : {}", CNT.getAndIncrement());
                                    if (SUCCESS_CNT.incrementAndGet() > 100 && Browser.sleepTime.get() > MIN_SLEEP_TIME) {
                                        Browser.sleepTime.decrementAndGet();
                                        SUCCESS_CNT.set(0);
                                    }
                                } else {
                                    int failed = FAILED_CNT.incrementAndGet();
                                    if (Browser.sleepTime.get() >= 5) {
                                        log.error("页面sleep时间过长,可能发生错误,请检查!");
                                        buffer.close(true);
                                    } else {
                                        if (failed > 50) {
                                            log.info("当前的失败次数为 {} ,当前的sleep值为 {} ,增加后的值为{}.", FAILED_CNT.get(), Browser.sleepTime.getAndIncrement(), Browser.sleepTime.get());
                                            FAILED_CNT.set(0);
                                        } else {
                                            log.info("当前的失败次数为 {}", FAILED_CNT.get());
                                        }
                                    }

                                }
                            }
                        } catch (Exception ignored) {

                        }
                    }
                } else {
                    // 不要做无意义的自旋
                    SECONDS.sleep(2);
                }
            }


        } catch (Exception ignored) {
        }
    }

    private String parseElement(PreparedStatement statement, List<WebElement> td) throws SQLException {
        String href = td.get(0).findElement(By.tagName("a")).getAttribute("href");
        String name = process(td.get(0).getText());
        String writer = td.get(1).getText();
        String press = td.get(2).getText();
        String isbn = td.get(3).getText();
        String star = td.get(4).getText().replace("星", "");


        statement.setString(1, href);
        statement.setString(2, name);
        statement.setString(3, writer);
        statement.setString(4, press);
        statement.setInt(5, year);
        statement.setString(6, isbn);
        statement.setInt(7, Integer.parseInt(star));
        return name;
    }

    private String process(String text) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        while (text.charAt(index) != '.') {
            index++;
        }
        index++;
        for (int i = index; i < text.length(); i++) {
            builder.append(text.charAt(i));
        }
        return builder.toString();
    }

    public void start() {
        for (int i = 0; i < threadNumber; i++) {
            poolExecutor.execute(this);
        }
    }

    public void shutdown() {
        poolExecutor.shutdown();
    }
}
