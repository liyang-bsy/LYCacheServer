package net.vicp.lylab.server.dispatcher;

import java.net.Socket;

import org.apache.commons.lang3.StringUtils;

import net.vicp.lylab.core.AbstractAction;
import net.vicp.lylab.core.BaseAction;
import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.model.CacheMessage;
import net.vicp.lylab.server.action.CacheAction;
import net.vicp.lylab.server.utils.Logger;
import net.vicp.lylab.utils.Utils;

public class CacheMessageDispatcher extends AbstractDispatcher<CacheMessage, CacheMessage> {

	@Override
	protected void logger(CacheMessage request, CacheMessage response) {
		((Logger) CoreDef.config.getConfig("Singleton").getObject("Logger"))
				.appendLine("Access key:" + request.getKey() + "\nBefore:" + request + "\nAfter:" + response);
	}
	
	protected void dispatcher(AbstractAction action, Socket client, CacheMessage request, CacheMessage response) {
		// gain key from request
		String key = request.getKey();
		if (StringUtils.isBlank(key)) {
			response.setCode(0x00000005);
			return;
		}
		// get action related to key
		action = new CacheAction();

		// Initialize action
		action.setSocket(client);
		action.setRequest(request);
		action.setResponse(response);
		// do action
		action.doAction();
	}

	@Override
	public CacheMessage doAction(Socket client, CacheMessage request) {
		BaseAction action = null;
		CacheMessage response = new CacheMessage();
		try {
			do {
				// filter chain
				try {
					CacheMessage tmp = filterChain(client, request);
					if (tmp != null) {
						response = tmp;
						break;
					}
				} catch (Exception e) {
					response.setCode(0x00000003);
					break;
				}
				// sync response and request
				response.copyBasicInfo(request);

				try {
					dispatcher(action, client, request, response);
				} catch (Throwable t) {
					String reason = Utils.getStringFromThrowable(t);
					log.error(reason);
					response.setCode(0x00000004);
				}
			} while (false);
		} catch (Exception e) {
			log.error(Utils.getStringFromException(e));
		}
		try {
			// save access log
			logger(request, response);
		} catch (Exception e) {
			log.error("Logger failed:" + Utils.getStringFromException(e));
		}
		return response;
	}

}
