package net.vicp.lylab.server.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import net.vicp.lylab.core.BaseAction;
import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.utils.Utils;
import net.vicp.lylab.utils.cache.LYCache;

public class GetCacheAction extends BaseAction {

	private String server;
	private String module;
	private String key;
	private Boolean renew;

	@Override
	public boolean foundBadParameter() {
		server = (String) getRequest().getBody().get("server");
		module = (String) getRequest().getBody().get("module");
		key = (String) getRequest().getBody().get("key");
		renew = (Boolean) getRequest().getBody().get("renew");
		if(StringUtils.isBlank(server)) {
			badParameter = "server";
			return true;
		}
		if(StringUtils.isBlank(module)) {
			badParameter = "module";
			return true;
		}
		if(StringUtils.isBlank(key)) {
			badParameter = "key";
			return true;
		}
		return false;
	}
	
	@Override
	public void exec() {
		do {
			LYCache cache = (LYCache) CoreDef.config.getConfig("Singleton").getObject("LYCache");

			if(renew == null)
				renew = false;
			
			byte[] bytes = cache.get(server + "_" + module + "_" + key, renew);
			
			String json = null;
			try {
				json = new String(bytes, CoreDef.CHARSET());
			} catch (UnsupportedEncodingException e) {
				getResponse().setCode(0x00010002);
				getResponse().setMessage("Cached data needs specific encoding");
				break;
			}
			
			Object data = Utils.deserialize(HashMap.class, json);
			
			getResponse().getBody().put("data", data);

		getResponse().success(); } while (false);
	}

}
