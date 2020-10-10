package com.wang.worker;

import com.wang.dao.BookDao;
import com.wang.pojo.Book;
import com.wang.pojo.Bookinfo;
import com.wang.util.Buffer;
import com.wang.util.SqlSessionUtil;
import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author wangz
 */
public class Consumer implements Runnable {


    private final ExecutorService pool;
    private final Buffer buffer;
    private final int threadCount;

    public Consumer(Buffer buffer, int threadNum) {
        this.buffer = buffer;
        threadCount = threadNum;
        pool = Executors.newFixedThreadPool(threadNum);
    }

    @Override
    public void run() {
        SqlSession session = SqlSessionUtil.getSession();

        var mapper = session.getMapper(BookDao.class);

        while (buffer.opened()) {

            Bookinfo info = null;
            try {
                info = buffer.getBookInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }


            Document document;
            try {
                assert info != null;
                document = Jsoup.connect(info.getHref()).get();

                var books = parse(document, info);

                mapper.insertBatch(books);
            } catch (Exception e) {

                System.out.println(getThreadCount()+" Thread Count");
                // 抛出异常会导致
                int currentThreanCount = getThreadCount();
                if (currentThreanCount < threadCount) {
                    pool.execute(this);
                }

            }


        }
    }


    @SneakyThrows
    private List<Book> parse(Document document, Bookinfo info) {


        if (info != null) {
            throw new Exception("test  thread caller");

        }

        List<Book> res = new ArrayList<>();

        Book baseBook = new Book(info);

        String borrowed = document.select("body > div.container-fluid.box > div > div.col-md-9.col-sm-9 > div.col-md-12.col-sm-12.bookInfo > div.col-md-3.col-sm-3 > div > span:nth-child(1) > span").text();
        baseBook.setBorrowingtimes(Integer.parseInt(borrowed));

        Elements image = document.select("body > div.container-fluid.box > div > div.col-md-9.col-sm-9 > div.col-md-12.col-sm-12.bookInfo > div.col-md-3.col-sm-3 > img");


        baseBook.setImglink(parseImage(image.toString()));

        String classNumber = "";
        Elements p = document.getElementsByTag("p");
        for (Element element : p) {
            if (element.text().contains("中图分类号")) {
                classNumber = element.text();
                break;
            }
        }
        baseBook.setClassnumber(classNumber);

        Elements elements = document.select("body > div.container-fluid.box > div > div.col-md-9.col-sm-9 > div:nth-child(2)");
        Elements tag = elements.get(0).getElementsByTag("p");
        baseBook.setDefaultcomment(tag.get(1).text());
        baseBook.setWriterinfo(tag.get(3).text());


        Element element = document.getElementById("guancanglist");

        Elements td = element.getElementsByTag("tr");

        for (Element element1 : td) {
            Book clone = (Book) baseBook.clone();

            Elements td1 = element1.getElementsByTag("td");

            String depart = td1.get(0).text();
            String barcode = td1.get(1).text();
            String index = td1.get(2).text();
            String status = td1.get(6).text();

            clone.setDepartment(depart);
            clone.setBarcode(barcode);
            clone.setIndexnumber(index);
            clone.setStatus(status);

            res.add(clone);
        }

        return res;
    }

    private String parseImage(String image) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < image.length(); i++) {
            if (image.charAt(i) == '"') {
                for (int j = i + 1; j < image.length(); j++) {
                    if (image.charAt(j) != '"') {
                        sb.append(image.charAt(j));
                    } else {
                        return sb.toString();
                    }
                }
            }
        }
        return "";
    }

    private synchronized int getThreadCount() {
        return ((ThreadPoolExecutor) pool).getPoolSize();
    }

    public void start() {
        for (int i = 0; i < threadCount; i++) {
            pool.execute(this);
        }
    }

}
