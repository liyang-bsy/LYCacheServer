package net.vicp.lylab.server.action;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import net.vicp.lylab.core.BaseAction;
import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.utils.Utils;
import net.vicp.lylab.utils.cache.LYCache;

public class DeleteCacheAction extends BaseAction {

	private String key;

	@Override
	public boolean foundBadParameter() {
		key = (String) getRequest().getBody().get("key");
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

			byte[] bytes;
			synchronized (lock) {
				bytes = cache.delete(key);
			}
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
