/**
 * @author wangz
 */
public class Main {

    public static void main(String[] args) throws Exception {

        // 指定爬取的年份
        int year = 2013;

        // 创建一个缓冲区
        Buffer buffer = new Buffer();

        // 创建消费者的线程池
        ConsumerPool consumerPool = new ConsumerPool(buffer, year, 2);

        // 启动线程池
        consumerPool.start();

        // 初始化 selenium
        Browser browser = new Browser(true);

        browser.init("http://opac.bupt.edu.cn:8080/index.html", String.valueOf(year));

        browser.getData(buffer);


        browser.destroy(buffer);

        consumerPool.shutdown();

    }
}

