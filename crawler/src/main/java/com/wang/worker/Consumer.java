package com.wang.worker;

import com.wang.dao.BookDao;
import com.wang.pojo.Book;
import com.wang.pojo.Bookinfo;
import com.wang.util.Buffer;
import com.wang.util.MybatisUtils;
import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangz
 */
public class Consumer implements Runnable {

    private final ExecutorService pool;
    private final Buffer buffer;
    private final int threadCount;

    static int cnt = 0;

    public Consumer(Buffer buffer, int threadNum) {
        this.buffer = buffer;
        threadCount = threadNum;
        pool = Executors.newFixedThreadPool(threadNum);
    }

    @Override
    @SneakyThrows
    public void run() {
        SqlSession session = MybatisUtils.getSession();

        var mapper = session.getMapper(BookDao.class);

        while (buffer.opened()) {

            Bookinfo info = buffer.getBookInfo();

            Document document = Jsoup.connect(info.getHref()).get();

            System.out.println(document.title().charAt(0) + "         cnt =: "+cnt++);
//
//            Book book = parse(document, info);
//
//            mapper.insert(book);
        }
    }

    private Book parse(Document document, Bookinfo info) {
        Book book = new Book();
        String borrowed = document.select("body > div.container-fluid.box > div > div.col-md-9.col-sm-9 > div.col-md-12.col-sm-12.bookInfo > div.col-md-3.col-sm-3 > div > span:nth-child(1) > span").text();
        Elements image = document.select("body > div.container-fluid.box > div > div.col-md-9.col-sm-9 > div.col-md-12.col-sm-12.bookInfo > div.col-md-3.col-sm-3 > img");

        System.out.println(image);

        String classNumber = "";

        Elements p = document.getElementsByTag("p");

        for (Element element : p) {
            if (element.text().contains("中图分类号")) {
                classNumber = element.text();
                break;
            }
        }
        System.out.println(classNumber);


        Elements elements = document.select("body > div.container-fluid.box > div > div.col-md-9.col-sm-9 > div:nth-child(2)");
        Elements tag = elements.get(0).getElementsByTag("p");

        System.out.println(tag.size());

        for (Element element : tag) {
            System.out.println(element.text());
        }


        Element element = document.getElementById("guancanglist");

        Elements td = element.getElementsByTag("tr");

        System.out.println(td.size());

        for (Element element1 : td) {

            Elements td1 = element1.getElementsByTag("td");

            System.out.println("===============");
            String depart = td1.get(0).text();
            String baicode = td1.get(1).text();
            String index = td1.get(2).text();
            String login = td1.get(3).text();
            String position = td1.get(4).text();
            String status = td1.get(6).text();

            System.out.println(depart + baicode + index + login + status);

            System.out.println("====================================");
        }
        return book;
    }

    public void start() {
        for (int i = 0; i < threadCount; i++) {
            pool.execute(this);
        }
    }

    public void close() {
        pool.shutdown();
    }
}
