package com.wang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (Book)实体类
 *
 * @author makejava
 * @since 2020-10-09 08:21:11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book implements Serializable {
    private static final long serialVersionUID = 141519677285335089L;

    private Integer id;

    private String barcode;

    private String name;

    private String isbn;

    private String classnumber;

    private String indexnumber;

    private String writer;

    private String press;

    private Integer pressingyear;

    private Integer borrowingtimes;

    private Integer status;

    private String area;

    private String defaultcomment;

    private String department;

    private String writerinfo;

    private String html;

    private Integer star;

    private String imglink;


}