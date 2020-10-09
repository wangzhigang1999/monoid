package com.wang.util;

import com.wang.dao.BookinfoDao;
import com.wang.pojo.Bookinfo;
import org.apache.ibatis.session.SqlSession;
import org.junit.jupiter.api.Test;

class MybatisUtilsTest {
    @Test
    public void utilTest() {

        SqlSession session = MybatisUtils.getSession();

        var mapper = session.getMapper(BookinfoDao.class);


        Bookinfo bookinfo = mapper.queryById(2);

        System.out.println(bookinfo);

        session.close();
    }
}