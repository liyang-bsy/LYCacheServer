package net.vicp.lylab.server.filter;

import java.net.Socket;

import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.NonCloneableBaseObject;
import net.vicp.lylab.core.model.CacheMessage;
import net.vicp.lylab.utils.Utils;

public class IPFilter extends NonCloneableBaseObject implements Filter<CacheMessage, CacheMessage> {

	public CacheMessage doFilter(Socket socket, CacheMessage request) {
		String host = socket.getInetAddress().getHostAddress();

		if (request.getAction().startsWith("Stop")) {
			if ("127.0.0.1".equals(host))
				return null;
			else
				return new CacheMessage(-2);
		}
		if (Utils.inList(CoreDef.config.getString("ipWhiteList").split(","), host))
			return null;
		else
			return new CacheMessage(-2);
	}

	@Override
	public void initialize() {
	}

	@Override
	public void close() {
	}

}
