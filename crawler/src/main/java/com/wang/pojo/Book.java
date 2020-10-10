package com.wang.pojo;

import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;

/**
 * (Book)实体类
 *
 * @author makejava
 * @since 2020-10-09 08:21:11
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Book implements Serializable, Cloneable {
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

    private String status;

    private String area;

    private String defaultcomment;

    private String department;

    private String writerinfo;

    private String html;

    private Integer star;

    private String imglink;

    public Book(Bookinfo bookinfo) {
        this.name = bookinfo.getName();
        this.isbn = bookinfo.getIsbn();
        this.writer = bookinfo.getWriter();
        this.press = bookinfo.getPress();
        this.pressingyear = Integer.parseInt(bookinfo.getPressyear());
        this.html = bookinfo.getHref();
        this.press = bookinfo.getPress();
        this.star = bookinfo.getStar();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        super.clone();

        try {

            //字节数组输出流
            @Cleanup ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //对象输出流
            @Cleanup ObjectOutputStream oos = new ObjectOutputStream(bos);
            //将这个对象写到对象输出流中

            oos.writeObject(this);
            //强制发送
            oos.flush();

            @Cleanup ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
            @Cleanup ObjectInputStream ois = new ObjectInputStream(bin);

            return ois.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }
}