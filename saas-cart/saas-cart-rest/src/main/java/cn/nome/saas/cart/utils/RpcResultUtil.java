package cn.nome.saas.cart.utils;

import cn.nome.platform.common.api.result.RpcResult;
import cn.nome.platform.common.constant.Constants;
import cn.nome.platform.common.exception.BusinessException;
import cn.nome.platform.common.exception.SystemException;
import cn.nome.saas.cart.constant.Message;

public class RpcResultUtil {
	/**
	 * 对一些RPC调用统一做异常判断
	 * @param rpcResult
	 */
	@SuppressWarnings("rawtypes")
	public static void RpcResultCheck(RpcResult rpcResult) {
//		if(rpcResult == null) {
//			throw new SystemException(Message.RPCERROR0001);
//		}
//
//		if (rpcResult.getCode().equals(Constants.RESULT_BIZ)) {
//			throw new BusinessException(rpcResult.getType(), rpcResult.getMessage());
//		}
//
//		if (!rpcResult.getCode().equals(Constants.RESULT_BIZ)) {
//			throw new SystemException(rpcResult.getType(), rpcResult.getMessage());
//		}
	}
}
