package cn.nome.saas.allocation.rest.handler;

import cn.nome.platform.common.constant.Constants;
import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.exception.SystemException;
import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.annotation.Resulted;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
import cn.nome.saas.allocation.component.LangComponent;
import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author bare
 */
@Component
@ControllerAdvice
public class RequestResponseAdvice implements ResponseBodyAdvice<Object> {

    private static Logger logger = LoggerFactory.getLogger(RequestResponseAdvice.class);

    @Autowired
    LangComponent langComponent;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        boolean hasClassResultedAnnotation = AnnotationUtils.findAnnotation(returnType.getContainingClass(),
                Resulted.class) != null;
        boolean hasMethodResultedAnnotation = returnType.getMethodAnnotation(Resulted.class) != null;
        return hasClassResultedAnnotation || hasMethodResultedAnnotation;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        return body instanceof Result ? body : ResultUtil.handleSuccessReturn(body);
    }

    /**
     * Description: 用于处理业务层面抛出的异常
     *
     * @param request 请求
     * @param ex      异常
     * @return Result
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleBusinessException(HttpServletRequest request, BusinessException ex) {
        String msg = ex.getMessage();
        if (msg == null) {
            msg = langComponent.getMessage(ex.getCode());
        }
        return ResultUtil.handleFailtureReturn(Constants.RESULT_BIZ, ex.getCode(), msg);
    }

    /**
     * Description: 用于处理系统抛出的异常
     *
     * @param request 请求
     * @param ex      异常
     * @return Result
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleSystemException(HttpServletRequest request, SystemException ex) {
        String msg = ex.getMessage();
        if (msg == null) {
            msg = langComponent.getMessage(ex.getCode());
        }
        return ResultUtil.handleFailtureReturn(Constants.RESULT_SYS, ex.getCode(), msg);
    }

    /**
     * 系统级别的异常
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleException(HttpServletRequest request, Exception ex) {
//        ex.printStackTrace();
//        logger.error("handleException:{}", ex.getMessage());
        logger.error("[handleException]", ex);
        return ResultUtil.handleFailtureReturn(Constants.RESULT_SYS, "系统异常");
    }

    /**
     * 验证异常处理
     *
     * @param request
     *            请求
     * @param ex
     *            异常
     * @return 返回结果
     */
    /**
     * 验证异常处理
     *
     * @param request 请求
     * @param ex      异常
     * @return 返回结果
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleBindException(HttpServletRequest request, BindException ex) {
        LoggerUtil.info(ex, logger, ex.getMessage());
        logger.error("handleBindException:{}", ex.getMessage());
        BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) ex.getBindingResult();
        return this.handleHibernateValidator(bindingResult);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result handleMethodArgumentNotValidException(HttpServletRequest request, MethodArgumentNotValidException ex) {
        LoggerUtil.info(ex, logger, ex.getMessage());
        logger.error("handleMethodArgumentNotValidException:{}", ex.getMessage());
        BeanPropertyBindingResult bindingResult = (BeanPropertyBindingResult) ex.getBindingResult();
        return this.handleHibernateValidator(bindingResult);
    }

    private Result handleHibernateValidator(BeanPropertyBindingResult bindingResult) {
        List<FieldError> errors = bindingResult.getFieldErrors();
        List<String> messages = new ArrayList<>();
        for (FieldError fieldError : errors) {
            messages.add(fieldError.getDefaultMessage());
        }
        char symbol = ',';
        String tips = StringUtils.join(messages, symbol);
        logger.error("handleHibernateValidator:{}", tips);
        //return ResultUtil.handleFailtureReturn(Constants.RESULT_BIZ, Constants.RESULT_TYPE_PARA, StringUtils.join(messages, symbol));
        return ResultUtil.handleFailtureReturn(Constants.RESULT_SYS, "验证异常");
    }
}
