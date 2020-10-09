import org.openqa.selenium.WebElement;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author wangz
 */
public class Buffer {
    /**
     * 标识缓冲区是否开启的量,当为 false 标识缓冲池应当关闭
     */
    private final AtomicBoolean start = new AtomicBoolean(true);

    private BlockingQueue<WebElement> queue = new LinkedBlockingQueue<>();

    public void addElement(WebElement webElement) throws Exception {
        if (start.get()) {
            queue.put(webElement);
        }
    }


    public WebElement getElement() {
        if (start.get()) {
            return queue.poll();
        }
        return null;
    }

    public synchronized void close(boolean force) {

        if (force) {
            queue = null;
            start.set(false);
        } else {
            while (queue != null) {
                if (queue.isEmpty()) {
                    queue = null;
                    start.set(false);
                } else {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    public boolean bufferInUse() {
        return start.get();
    }
}
