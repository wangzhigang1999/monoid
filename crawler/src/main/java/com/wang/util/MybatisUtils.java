package com.wang.util;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author wangz
 */
public class MybatisUtils {
    private static final SqlSessionFactory SQL_SESSION_FACTORY;

    static {
        String res = "mybatis-config.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(res);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SQL_SESSION_FACTORY = new SqlSessionFactoryBuilder().build(inputStream);
    }

    public synchronized static SqlSession getSession() {
        return SQL_SESSION_FACTORY.openSession();
    }
}
