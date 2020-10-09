package com.wang.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * (Bookinfo)实体类
 *
 * @author makejava
 * @since 2020-10-09 08:48:55
 */
@AllArgsConstructor
@Data
@Accessors
public class Bookinfo implements Serializable {
    private static final long serialVersionUID = 243758908691352489L;

    private Integer id;

    private String href;

    private String name;

    private String writer;

    private String press;

    private String pressyear;

    private String isbn;

    private Integer star;


}