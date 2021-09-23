package cn.nome.saas.allocation.utils.old;

import cn.nome.platform.common.exception.BusinessException;

public class BizException extends BusinessException {
    public BizException(String msg) {
        super("999999",msg);
        //this.setMsg(msg);
    }
}
