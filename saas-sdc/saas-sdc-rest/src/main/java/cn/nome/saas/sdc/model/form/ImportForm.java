package cn.nome.saas.sdc.model.form;

import cn.nome.platform.common.utils.ToString;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/11/8 17:02
 */
public class ImportForm extends ToString {

    @NotNull(message = "请选择Excel文件")
    private MultipartFile file;

    private Integer corpId;

    private Long userId;

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

    public Integer getCorpId() {
        return corpId;
    }

    public void setCorpId(Integer corpId) {
        this.corpId = corpId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
