package cn.iyque.utils;


import cn.hutool.extra.spring.SpringUtil;
import cn.iyque.config.IYqueParamConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


@Slf4j
public class FileUtils {

    public static final long MIN_FILE_SIZE = 5L; // 最小文件大小为5个字节

    public static final long IMAGE_MAX_SIZE = 10L * 1024 * 1024; // 图片最大尺寸为10MB
    public static final long VOICE_MAX_SIZE = 2L * 1024 * 1024; // 语音最大尺寸为2MB
    public static final long VIDEO_MAX_SIZE = 10L * 1024 * 1024; // 视频最大尺寸为10MB
    public static final long FILE_MAX_SIZE = 20L * 1024 * 1024; // 普通文件最大尺寸为20MB


    public static String getFileContentType(String fileExtension) {
        switch (fileExtension.toLowerCase()) {
            case ".jpg":
            case ".jpeg":
            case ".png":
                return "image";
            case ".amr":
                return "voice";
            case ".mp4":
                return "video";
            default:
                return "file";
        }
    }


    public static Boolean checkImageSizeAndFormat(MultipartFile file, String fileExtension) throws IOException {
        checkFileSize(file, IMAGE_MAX_SIZE);
        if (!fileExtension.equalsIgnoreCase(".jpg") && !fileExtension.equalsIgnoreCase(".png")) {
           return false;
        }
        return true;
    }

    public static void checkVoiceSizeAndFormat(MultipartFile file, String fileExtension) throws IOException {
        checkFileSize(file, VOICE_MAX_SIZE);
        if (!fileExtension.equalsIgnoreCase(".amr")) {
            throw new IOException("仅支持AMR格式的语音文件");
        }
    }

    public static void checkVideoSizeAndFormat(MultipartFile file, String fileExtension) throws IOException {
        checkFileSize(file, VIDEO_MAX_SIZE);
//        if (!fileExtension.equalsIgnoreCase(".mp4")) {
//            throw new IOException("仅支持MP4格式的视频文件");
//        }
    }

    public static void checkFileSize(MultipartFile file, long maxSize) throws IOException {
        long fileSize = file.getSize();
        if (fileSize > maxSize) {
            throw new IOException("文件大小超过限制");
        }
    }

    public static File downloadImage(String imageUrl) {
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")){
            try {
                URL url = new URL(imageUrl);
                ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
                File outputFile = File.createTempFile("image_", ".jpg");
                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                fileOutputStream.close();
                readableByteChannel.close();
                return outputFile;
            } catch (IOException e) {
                log.error("下载图片失败:"+e.getMessage());
                return null;
            }
        }else{
            // 图片URL不是以http或https开头，从本地读取文件
            File localUploadDir = new File(SpringUtil.getBean(IYqueParamConfig.class).getUploadDir());
            if (!localUploadDir.exists()) {
                localUploadDir.mkdirs(); // 确保上传目录存在
            }
            File localFile = new File(localUploadDir, imageUrl);
            if (localFile.exists()) {
                return localFile;
            } else {
                log.error("本地文件不存在: " + localFile.getAbsolutePath());
                return null;
            }
        }


    }
}
