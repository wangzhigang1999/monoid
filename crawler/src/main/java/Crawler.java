import com.wang.util.Buffer;
import com.wang.worker.Consumer;
import com.wang.worker.Provider;
import lombok.SneakyThrows;

/**
 * @author wangz
 */
public class Crawler {
    @SneakyThrows
    public static void main(String[] args) {
        Buffer buffer = new Buffer(5000 );

        Provider provider = new Provider(buffer,1);

        Consumer consumer = new Consumer(buffer, 2);

        provider.start();
        consumer.start();


    }
}
