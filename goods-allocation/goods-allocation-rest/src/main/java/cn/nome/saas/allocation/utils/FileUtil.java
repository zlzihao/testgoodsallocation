package cn.nome.saas.allocation.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);


    /**
     * 上传文件到服务器
     * @param file
     * @param filePath
     * @param fileName
     * @throws IOException
     */
    public static String uploadFile(MultipartFile file, String filePath, String fileName) throws IOException {
        if (file != null) {
            String uploadFile = filePath + fileName;
            File toFile = new File(uploadFile);
            if (!toFile.getParentFile().exists()) {
                // when file is not existed, will create.
                toFile.mkdirs();
            }
            // write to target file.
            file.transferTo(toFile);
            return uploadFile;
        }
        return null;
    }
}
