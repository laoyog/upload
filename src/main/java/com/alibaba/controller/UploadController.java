package com.alibaba.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

/**
 * @Author : chuangu
 * @Date : 2018/8/14 0014
 * @Desoription : 批量文件上传
 */
@RestController
public class UploadController {

    @Value("${prop.uploadFolder}")
    private String UPLOAD_FOLDER;

    private String sss;

    /**
     * 上传单个文件
     */
    @RequestMapping(value = "/upload/singleFileUpload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String singleFileUpload(MultipartFile file) {
        if (Objects.isNull(file) || file.isEmpty()) {
            return "文件为空!";
        }
        String allName = file.getOriginalFilename();
        if(!allName.substring(allName.lastIndexOf(".")).equals(".xlsx")){   //后缀名指定为.xlsx
            return "文件格式不正确!";
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER + "/"+getFilename(allName));  //    文件夹 +/+ 文件完整名称  a.txt

            //如果没有files文件夹，则创建
            if (!Files.isWritable(path)) {
                Files.createDirectories(Paths.get(UPLOAD_FOLDER));
            }
            //文件写入指定路径
            Files.write(path, bytes);
            System.out.println("-----------------");
            return "上传成功";

        } catch (IOException e) {
            return "上传失败";
        }
    }
    /**
     * 上传多个文件
     * @param: file 和标签name对应,否则无响应
     * @return string
     */
    @RequestMapping(value = "/upload/multiFileUpload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String multiFileUpload(MultipartFile[] file) {
        String msg = null;
        int count = 0;
        try {
            for (int i = 0; i < file.length; i++) {
                if(file[i]!=null){
                    msg = singleFileUpload(file[i]);
                }
                if(!msg.equals("文件上传成功")){
                    count = i;
                    continue;
                }
            }
            if(count>0){    //说明有失败的
                return "第"+count+"个文件上传失败,其他文件上传成功";
            }
            return msg;
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    /**
     * 时间+随机数+后缀 设置文件名
     * @param originalFilename 原名称
     * @return string
     */
    private String getFilename(String originalFilename){
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));  //后缀名  如 .txt
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        // 将此时时间转换为字符串
        String formatDate = format.format(new Date());
        // 随机生成文件编号
        int random = new Random().nextInt(10000);
        // 拼接文件名
        String filename = new StringBuffer().append(formatDate).append("_").append(random).append(suffix).toString();
        return filename;
    }

}
