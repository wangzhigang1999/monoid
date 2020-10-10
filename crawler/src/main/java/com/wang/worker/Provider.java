package com.wang.worker;

import com.wang.dao.BookinfoDao;
import com.wang.pojo.Bookinfo;
import com.wang.util.Buffer;
import com.wang.util.SqlSessionUtil;
import lombok.SneakyThrows;
import org.apache.ibatis.session.SqlSession;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wangz
 */
public class Provider implements Runnable {

    private final Buffer buffer;

    private final int threadNum;

    private int offset = 35000;

    private final int limit = 500;

    private final static int CNT = SqlSessionUtil.getSession().getMapper(BookinfoDao.class).getDataCnt();

    ExecutorService executorService;

    public Provider(Buffer buffer, int threadNum) {
        this.buffer = buffer;
        this.threadNum = threadNum;
        executorService = Executors.newFixedThreadPool(threadNum);
    }

    @Override
    @SneakyThrows
    public void run() {
        SqlSession session = SqlSessionUtil.getSession();

        BookinfoDao mapper = session.getMapper(BookinfoDao.class);

        while (offset < CNT) {
            List<Bookinfo> bookinfos = mapper.queryAllByLimit(getOffset(), limit);
            for (Bookinfo bookinfo : bookinfos) {
                if (buffer.opened()) {
                    buffer.addBookInfo(bookinfo);
                }

            }
        }

        buffer.close(false);

        if (buffer.opened()) {
            buffer.close(true);
        }

    }

    private  int getOffset() {
        var res = offset;

        offset += limit;

        return res;
    }

    public void start() {
        for (int i = 0; i < threadNum; i++) {
            executorService.execute(this);
        }
    }

}
