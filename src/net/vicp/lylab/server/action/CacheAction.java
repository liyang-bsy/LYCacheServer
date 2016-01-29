package net.vicp.lylab.server.action;

import net.vicp.lylab.core.AbstractAction;
import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.exceptions.LYException;
import net.vicp.lylab.core.model.CacheMessage;
import net.vicp.lylab.core.model.Pair;
import net.vicp.lylab.utils.cache.LYCache;

public class CacheAction extends AbstractAction {

	protected LYCache cache = (LYCache) CoreDef.config.getConfig("Singleton").getObject("LYCache");

	@Override
	public CacheMessage getRequest() {
		return (CacheMessage) super.getRequest();
	}

	@Override
	public CacheMessage getResponse() {
		return (CacheMessage) super.getResponse();
	}
	
	@Override
	public void doAction() {
		do {
			Pair<String, byte[]> pair = getRequest().getPair();
			switch (getRequest().getKey()) {
			case "Set":
				cache.set(pair.getLeft(), pair.getRight(), getRequest().getExpireTime());
				break;
			case "Get":
				getResponse().getPair().setRight(cache.get(pair.getLeft(), getRequest().isRenew()));
				break;
			case "Delete":
				getResponse().getPair().setRight(cache.delete(pair.getLeft()));
				break;
			}

		getResponse().success(); } while (false);
	}

	@Override
	public void exec() {
		throw new LYException("Method not supported");
	}

}
