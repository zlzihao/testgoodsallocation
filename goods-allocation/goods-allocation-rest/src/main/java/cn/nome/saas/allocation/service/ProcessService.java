package cn.nome.saas.allocation.service;

import org.springframework.web.multipart.MultipartFile;

/*
 * @description 加工服务接口
 * @author godsfer
 * @version 1.0.0
 * @date 2019/8/13 14:31
 */
public interface ProcessService {
    /**
     * 导入数据
     *
     * @param isClear
     * @param fileName
     * @param file
     * @param downTemplateCode
     * @throws Exception
     */
    int importData(Integer isClear, String fileName, MultipartFile file,String downTemplateCode)  throws Exception;

    /**
     * 二次加工
     *
     * @param downTemplateCode
     * @param limitNum
     */
    void secProcess(String downTemplateCode,int limitNum);
}
