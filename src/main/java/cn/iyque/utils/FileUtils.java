package cn.iyque.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


@Slf4j
public class FileUtils {
    public static File downloadImage(String imageUrl) {
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
    }
}
