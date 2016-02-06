package net.vicp.lylab.server.action;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import net.vicp.lylab.core.BaseAction;
import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.utils.Utils;
import net.vicp.lylab.utils.cache.LYCache;

public class CompareAndSetCacheAction extends BaseAction {

	private String key;
	private Long expireTime;
	private Boolean renew;
	private Object oldData;
	private Object data;

	@Override
	public boolean foundBadParameter() {

		key = (String) getRequest().getBody().get("key");
		expireTime = (Long) getRequest().getBody().get("expireTime");
		renew = (Boolean) getRequest().getBody().get("renew");
		oldData = getRequest().getBody().get("oldData");
		data = getRequest().getBody().get("data");
		
		if(StringUtils.isBlank(key)) {
			badParameter = "key";
			return true;
		}
		if (!(data instanceof Map)) {
			getResponse().setCode(0x00010001);
			getResponse().setMessage("Data is not map, can not be saved");
			return true;
		}
		
		return false;
	}
	
	@Override
	public void exec() {
		do {
			LYCache cache = (LYCache) CoreDef.config.getConfig("Singleton").getObject("LYCache");

			if (renew == null)
				renew = false;

			byte[] bytesOld = cache.get(key, renew);

			String jsonCmp = Utils.serialize(oldData);
			byte[] bytesCmp;
			try {
				bytesCmp = jsonCmp.getBytes(CoreDef.CHARSET());
			} catch (UnsupportedEncodingException e) {
				getResponse().setCode(0x00010002);
				getResponse().setMessage("Param oldData needs specific encoding");
				break;
			}

			String json = Utils.serialize(data);
			byte[] bytes;
			try {
				bytes = json.getBytes(CoreDef.CHARSET());
			} catch (UnsupportedEncodingException e) {
				getResponse().setCode(0x00010002);
				getResponse().setMessage("Param data needs specific encoding");
				break;
			}

			synchronized (lock) {
				if (!Arrays.equals(bytesCmp, bytesOld)) {
					getResponse().setCode(0x00010003);
					getResponse().setMessage("Old data doesn't matches provided value");
					break;
				}
	
				if (expireTime == null)
					expireTime =0L;
				cache.set(key, bytes, expireTime.intValue());
			}

		getResponse().success();} while (false);
	}

}
