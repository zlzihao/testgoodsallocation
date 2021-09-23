package cn.nome.saas.search.rest.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;

import cn.nome.platform.common.exception.BaseException;
import cn.nome.platform.common.web.controller.BaseRequestResponseAdvice;
import cn.nome.saas.search.component.LangComponent;
/**
 * 
 * @author bare
 *
 */
@Component
@ControllerAdvice
public class RequestResponseAdvice extends BaseRequestResponseAdvice {

	@Autowired
	private LangComponent langComponent;

	@Override
	public String getMsg(BaseException ex) {
		return langComponent.getMessage(ex.getCode());
	}

	@Override
	public Logger getLogger() {
		return LoggerFactory.getLogger(RequestResponseAdvice.class);
	}
}
