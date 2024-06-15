package cn.iyque.controller;


import cn.iyque.config.IYqueParamConfig;
import cn.iyque.constant.HttpStatus;
import cn.iyque.domain.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/**
 * 文件处理控制器
 */
@RestController
@Slf4j
@RequestMapping("file")
public class FileController {


    @Autowired
    IYqueParamConfig iYqueParamConfig;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public ResponseResult<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // 创建上传目录
            File uploadDir = new File(iYqueParamConfig.getUploadDir());
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            // 获取原始文件名及其扩展名
            String originalFileName = file.getOriginalFilename();
            String fileExtension = originalFileName.substring(originalFileName.lastIndexOf('.'));

            // 获取文件名
            String fileName = UUID.randomUUID()+fileExtension;
            // 构建目标文件路径
            Path targetPath = Paths.get(iYqueParamConfig.getUploadDir(), fileName);
            // 将文件写入目标路径
            Files.write(targetPath, file.getBytes());

            return new ResponseResult<>(fileName);
        } catch (IOException e) {
             log.error("文件上传失败:"+e.getMessage());
            return new ResponseResult<>(HttpStatus.ERROR,"文件上传失败",null);
        }
    }


    @GetMapping("/fileView/{filename}")
    public ResponseEntity<Resource> readFile(@PathVariable String filename) {
        try {
            // 构建文件路径
            Path filePath = Paths.get(iYqueParamConfig.getUploadDir(), filename);
            // 获取文件资源
            Resource resource = new UrlResource(filePath.toUri());

            // 检查文件是否存在
            if (resource.exists() || resource.isReadable()) {
                // 设置响应头
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

                // 返回文件资源
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }


}
