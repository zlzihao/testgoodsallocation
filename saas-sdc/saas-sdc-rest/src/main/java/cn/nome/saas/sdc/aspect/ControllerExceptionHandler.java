package cn.nome.saas.sdc.aspect;

import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.exception.SystemException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.sdc.enums.ReturnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;

/**
 * @author 曾德武（Alfred）<zengdewu@nome.com>
 * @touch 2019/9/9 11:06
 */
@ControllerAdvice
public class ControllerExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ExceptionHandler({BusinessException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleBusinessException(HttpServletRequest request, BusinessException ex) {
        LoggerUtil.debug(ex, LOGGER, ex.getMessage());
        return ResultUtil.handleBizFailtureReturn(ex.getCode(), ex.getMsg());
    }

    @ExceptionHandler({SystemException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleSystemException(HttpServletRequest request, SystemException ex) {
        LoggerUtil.error(ex, LOGGER, ex.getMessage());
        return ResultUtil.handleFailtureReturn("SYS", ex.getCode(), ex.getMsg());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleException(HttpServletRequest request, Exception ex) {
        if (ex instanceof MissingServletRequestParameterException) {
            return ResultUtil.handleBizFailtureReturn(ReturnType.VALIDATION_FAIL.getType(), ReturnType.VALIDATION_FAIL.getMsg());
        }
        if (ex instanceof HttpRequestMethodNotSupportedException) {
            return ResultUtil.handleBizFailtureReturn(ReturnType.METHOD_NOT_FOUND.getType(), ReturnType.METHOD_NOT_FOUND.getMsg());
        }
        LoggerUtil.error(ex, LOGGER, ex.getMessage());
        return ResultUtil.handleFailtureReturn("SYS", ReturnType.SYSTEM_FAIL.getType(), ReturnType.SYSTEM_FAIL.getMsg());
    }

    @ExceptionHandler({BindException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleBindException(HttpServletRequest request, BindException ex) {
        LoggerUtil.error(ex, LOGGER, ex.getMessage());
        BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) ex.getBindingResult();
        return this.handleHibernateValidator(bindingResult);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<?> handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        LoggerUtil.error(ex, LOGGER, ex.getMessage());
        BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) ex.getBindingResult();
        return this.handleHibernateValidator(bindingResult);
    }

    private Result<?> handleHibernateValidator(BeanPropertyBindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        Iterator iter = errors.iterator();
        FieldError fieldError = (FieldError) iter.next();
        String msg = fieldError.getDefaultMessage();

        return ResultUtil.handleBizFailtureReturn(ReturnType.VALIDATION_FAIL.getType(), msg);
    }
}
