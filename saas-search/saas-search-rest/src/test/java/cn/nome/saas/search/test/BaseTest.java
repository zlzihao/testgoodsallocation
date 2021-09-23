package cn.nome.saas.search.test;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.ribbon.proxy.annotation.Http.HttpMethod;

import cn.nome.platform.common.logger.LoggerUtil;
import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.saas.search.SearchBootstrap;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SearchBootstrap.class)
@WebAppConfiguration
public class BaseTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);
	
	private ObjectMapper mapper = new ObjectMapper();
	
	@Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    Date start;
	Date end;
	
	public WebApplicationContext getContext() {
		return context;
	}

	public void setContext(WebApplicationContext context) {
		this.context = context;
	}

	public MockMvc getMockMvc() {
		return mockMvc;
	}

	public void setMockMvc(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	@Before
	public void before() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
		start = new Date();
		LoggerUtil.info(LOGGER, "----------------------------------------------Start time:{0}", start);
	}

	@After
	public void after() {
		end = new Date();
		LoggerUtil.info(LOGGER, "----------------------------------------------End time:{0}", end);
	}

	Long id =1l;
	
	public <T> Result<?> request(Map<String, String> param, Map<String, String> header, HttpMethod method, String uri) throws UnsupportedEncodingException, JsonProcessingException, Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = null;
		if(HttpMethod.POST == method) {
			mockHttpServletRequestBuilder = MockMvcRequestBuilders.post(uri);
		} else if(HttpMethod.GET == method) {
			mockHttpServletRequestBuilder = MockMvcRequestBuilders.get(uri);
		} else {
			return null;
		}
		mockHttpServletRequestBuilder.contentType(MediaType.TEXT_PLAIN_VALUE);
		if(header != null) {
			for(Map.Entry<String, String> entry:header.entrySet()) {
				mockHttpServletRequestBuilder.header(entry.getKey(), entry.getValue());
			}
		}
		if(param != null) {
			for(Map.Entry<String, String> entry:param.entrySet()) {
				mockHttpServletRequestBuilder.param(entry.getKey(), entry.getValue());
			}
		}
		String result = getMockMvc().perform(mockHttpServletRequestBuilder.content(mapper.writeValueAsString(id)))
				.andReturn().getResponse().getContentAsString();
		return JSON.parseObject(result,Result.class);
	}
	
	public Result<?> request(Object param, Map<String, String> header, HttpMethod method, String uri) throws UnsupportedEncodingException, JsonProcessingException, Exception {
		MockHttpServletRequestBuilder mockHttpServletRequestBuilder = null;
		if(HttpMethod.POST == method) {
			mockHttpServletRequestBuilder = MockMvcRequestBuilders.post(uri);
		} else if(HttpMethod.GET == method) {
			mockHttpServletRequestBuilder = MockMvcRequestBuilders.get(uri);
		} else {
			return null;
		}
		mockHttpServletRequestBuilder.contentType(MediaType.APPLICATION_JSON_VALUE);
		if(header != null) {
			for(Map.Entry<String, String> entry:header.entrySet()) {
				mockHttpServletRequestBuilder.header(entry.getKey(), entry.getValue());
			}
		}
		String result = getMockMvc().perform(mockHttpServletRequestBuilder.content(JSON.toJSONString(param)))
				.andReturn().getResponse().getContentAsString();
		return JSON.parseObject(result,Result.class);
	}
}
