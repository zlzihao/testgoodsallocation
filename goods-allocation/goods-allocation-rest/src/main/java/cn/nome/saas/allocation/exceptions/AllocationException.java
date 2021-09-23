package cn.nome.saas.allocation.exceptions;

/**
 * @author ：godsfer
 * @date ：Created in 2019/7/26 11:46
 * @description：scm回写异常
 * @modified By：
 * @version: 1.0.0$
 */
public class AllocationException extends RuntimeException {
    private String message;

    public AllocationException() {
        super();
    }
    public AllocationException(String msg) {
        super(msg);
        this.message=message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
