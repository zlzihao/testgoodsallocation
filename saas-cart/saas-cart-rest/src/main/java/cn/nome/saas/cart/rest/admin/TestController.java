package cn.nome.saas.cart.rest.admin;

import cn.nome.platform.common.web.controller.protocol.Result;
import cn.nome.platform.common.web.controller.protocol.ResultUtil;
/*import cn.nome.saas.cart.model.CartSkuModel;
import cn.nome.saas.cart.model.CartWrap;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//import redis.clients.jedis.Jedis;

//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;

//import cn.nome.saas.cart.model.wrap.SkuItem;

/**
 * @author bare
 * @create 2017/12/26.
 */
@RestController
@RequestMapping("/corporation/test")
public class TestController {

    private static Logger logger = LoggerFactory.getLogger(TestController.class);

	/*@Autowired
	private Jedis jedisCluster;
//	@Autowired
//	private CacheManager<CartWrap> cacheManager;

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	@ResponseBody
	public Result login(@RequestHeader("HEADER-NOME-CorpID") String corpId, @RequestHeader("HEADER-NOME-AppID") String appId, @RequestHeader("HEADER-NOME-UID") String uid)
			throws Exception {
		Map map = Maps.newHashMap();
		map.put("corpId", corpId);
		map.put("appId", appId);
		map.put("uid", uid);
		return ResultUtil.handleSuccessReturn(map);
	}

	@RequestMapping("/tips")
//	@CachePut(value = "cart", key = "2009")
//	@Cacheable(value = "findAll", key = "'test'")
	public Result login(String uid)
			throws Exception {
		Map map = Maps.newHashMap();
		map.put("tips", uid);
		map.put("project", "cart");
		System.out.println("123");
		jedisCluster.set("userName", "hello 123");
//		cacheManager.setCartCache("2005", null);
		return ResultUtil.handleSuccessReturn(map);
	}

	@RequestMapping("/set/{id}")
//	@CachePut(value = "cart", key = "2009")
//	@Cacheable(value = "findAll", key = "'test'")
	public Result set(String uid,@PathVariable("id") String id)
			throws Exception {
		Map map = Maps.newHashMap();
		map.put("tips", uid);
		map.put("project", jedisCluster.set(id, JSONObject.toJSONString(getCartWarp())));
		System.out.println("set");
//		cacheManager.setCartCache("2005", null);
		return ResultUtil.handleSuccessReturn(map);
	}

	@RequestMapping("/del/{id}")
//	@CachePut(value = "cart", key = "2009")
//	@Cacheable(value = "findAll", key = "'test'")
	public Result del(String uid,@PathVariable("id") String id)
			throws Exception {
		Map map = Maps.newHashMap();
		map.put("tips", uid);
		map.put("project", jedisCluster.del(id));
		System.out.println("del");
//		cacheManager.setCartCache("2005", null);
		return ResultUtil.handleSuccessReturn(map);
	}

	@RequestMapping("/get/{id}")
//	@CachePut(value = "cart", key = "2009")
//	@Cacheable(value = "findAll", key = "'test'")
	public Result get(String uid,@PathVariable("id") String id)
			throws Exception {
		Map map = Maps.newHashMap();
		map.put("tips", uid);
		map.put("project", jedisCluster.get(id));
		System.out.println("get");
//		cacheManager.setCartCache("2005", null);
		return ResultUtil.handleSuccessReturn(map);
	}

	public CartWrap getCartWarp() {
		CartWrap cartWrap = new CartWrap();
		List<CartSkuModel> invalidSkus = new ArrayList<>();
		CartSkuModel cartSkuModel = new CartSkuModel();
		cartSkuModel.setAddTime(123);
		cartSkuModel.setCount(123);
		cartSkuModel.setPrice(11);
		cartSkuModel.setSkuId(131313);
		cartSkuModel.setName("安居客了");
		invalidSkus.add(cartSkuModel);
		cartWrap.setInvalidSkus(invalidSkus);
		return cartWrap;
	}*/
    @RequestMapping(value = "/apollo/logger", method = RequestMethod.GET)
    @ResponseBody
    public Result apolloLoggerTest()
            throws Exception {
        logger.info("this is INFO level--------------");
        logger.error("this is ERROR level--------------");
        logger.warn("this is WARN level--------------");
        logger.debug("this is DEBUG level--------------");
        return ResultUtil.handleSuccessReturn("logger success");
    }
}


