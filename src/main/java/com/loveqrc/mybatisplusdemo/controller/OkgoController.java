package com.loveqrc.mybatisplusdemo.controller;

import lombok.val;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
public class OkgoController {

    @RequestMapping("/down")
    public String downFile(HttpServletResponse response) {
//        String srcFilePath = "I:\\springboot\\springBoot_atguigu-master\\mybatis-plus-demo\\src\\main\\resources\\test.txt";
        String srcFilePath = "I:\\springboot\\springBoot_atguigu-master\\mybatis-plus-demo\\src\\main\\resources\\1.doc";
//        String downFileName = "hello.mp4";
        String downFileName = "hello.doc";
        File file = new File(srcFilePath);
        if (file.exists()) {
//            response.setContentType("application/force-download");
            response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "attachment;filename=" + downFileName);
            byte[] buffer = new byte[1024];
            FileInputStream fis = null;
            BufferedInputStream bis = null;

            try {
                fis = new FileInputStream(file);
                bis = new BufferedInputStream(fis);
                OutputStream outputStream = response.getOutputStream();
                int i = bis.read(buffer);
                while (i != -1) {
                    outputStream.write(buffer, 0, i);
                    i = bis.read(buffer);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return "Hello World aa123";
    }

    @RequestMapping("/downRange")
    public void downFileByRange(HttpServletRequest request, HttpServletResponse response) {
        String srcFilePath = "I:\\springboot\\springBoot_atguigu-master\\mybatis-plus-demo\\src\\main\\resources\\test.txt";
        File downloadFile = new File(srcFilePath);

        ServletContext context = request.getServletContext();

        String mimeType = context.getMimeType(srcFilePath);
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        response.setContentType(mimeType);

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", downloadFile.getName());
        response.setHeader(headerKey, headerValue);
        // 解析断点续传相关信息
        response.setHeader("Accept-Ranges", "bytes");
        long downloadSize = downloadFile.length();
        long fromPos = 0, toPos = 0;
        if (request.getHeader("Range") == null) {
            response.setHeader("Content-Length", downloadSize + "");
        } else {
            // 若客户端传来Range，说明之前下载了一部分，设置206状态(SC_PARTIAL_CONTENT)
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            String range = request.getHeader("Range");
            String bytes = range.replaceAll("bytes=", "");
            String[] ary = bytes.split("-");
            fromPos = Long.parseLong(ary[0]);
            if (ary.length == 2) {
                toPos = Long.parseLong(ary[1]);
            }
            int size;
            if (toPos > fromPos) {
                size = (int) (toPos - fromPos);
            } else {
                size = (int) (downloadSize - fromPos);
            }
            response.setHeader("Content-Length", size + "");
            downloadSize = size;
        }
        // Copy the stream to the response's output stream.
        RandomAccessFile in = null;
        OutputStream out = null;
        try {
            in = new RandomAccessFile(downloadFile, "rw");
            // 设置下载起始位置
            if (fromPos > 0) {
                in.seek(fromPos);
            }
            // 缓冲区大小
            int bufLen = (int) (downloadSize < 2048 ? downloadSize : 2048);
            byte[] buffer = new byte[bufLen];
            int num;
            int count = 0; // 当前写到客户端的大小
            out = response.getOutputStream();
            while ((num = in.read(buffer)) != -1) {
                out.write(buffer, 0, num);
                count += num;
                //处理最后一段，计算不满缓冲区的大小
                if (downloadSize - count < bufLen) {
                    bufLen = (int) (downloadSize - count);
                    if (bufLen == 0) {
                        break;
                    }
                    buffer = new byte[bufLen];
                }
            }
            response.flushBuffer();
        } catch (IOException e) {
//            logger.info("数据被暂停或中断。");
//            e.printStackTrace();
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
//                    logger.info("数据被暂停或中断。");
//                    e.printStackTrace();
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
//                    logger.info("数据被暂停或中断。");
//                    e.printStackTrace();
                }
            }
        }

    }

}

