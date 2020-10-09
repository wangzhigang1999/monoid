package com.wang.dao;

import com.wang.pojo.Bookinfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * (Bookinfo)表数据库访问层
 *
 * @author makejava
 * @since 2020-10-09 08:49:02
 */
public interface BookinfoDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Bookinfo queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Bookinfo> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param bookinfo 实例对象
     * @return 对象列表
     */
    List<Bookinfo> queryAll(Bookinfo bookinfo);

    /**
     * 新增数据
     *
     * @param bookinfo 实例对象
     * @return 影响行数
     */
    int insert(Bookinfo bookinfo);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<Bookinfo> 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<Bookinfo> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<Bookinfo> 实例对象列表
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("entities") List<Bookinfo> entities);

    /**
     * 修改数据
     *
     * @param bookinfo 实例对象
     * @return 影响行数
     */
    int update(Bookinfo bookinfo);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

    /**
     * 获得数据的总条数
     *
     * @return 总行数
     */
    @Select("select  COUNT(0) from bookinfo")
    int getDataCnt();

}