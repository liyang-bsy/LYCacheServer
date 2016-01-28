package net.vicp.lylab.server.action.manager;

import java.util.Date;

import net.vicp.lylab.core.BaseAction;
import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.utils.Utils;
import net.vicp.lylab.utils.cache.LYCache;

public class CheckRuntimeAction extends BaseAction {

	@Override
	public boolean foundBadParameter() {
		return false;
	}
	
	@Override
	public void exec() {
		try {
			do {
				getResponse().getBody().put("Current runtime environment", CoreDef.config.getString("debug"));
				getResponse().getBody().put("Current time", Utils.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

				LYCache cache = (LYCache) CoreDef.config.getConfig("Singleton").getObject("LYCache");
				getResponse().getBody().put("Cache entries count", cache.getEntrySize());
				getResponse().getBody().put("Cache memory count", cache.getMemorySize());
				getResponse().success();
			} while (false);
		} catch (Exception e) {
			log.error("Exception detected:" + Utils.getStringFromException(e));
		}
	}

}
