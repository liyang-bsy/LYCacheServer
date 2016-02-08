package net.vicp.lylab.server.action;

import java.util.Arrays;

import net.vicp.lylab.core.AbstractAction;
import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.exceptions.LYException;
import net.vicp.lylab.core.model.CacheMessage;
import net.vicp.lylab.core.model.Pair;
import net.vicp.lylab.server.ServerRuntime;
import net.vicp.lylab.utils.cache.LYCache;

public class RDMAAction extends AbstractAction {

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
		Pair<String, byte[]> pair = getRequest().getPair();
		switch (getRequest().getAction()) {
		case "Set":
			synchronized (lock) {
				cache.set(pair.getLeft(), pair.getRight(), getRequest().getExpireTime());
			}
			getResponse().success();
			break;
		case "CompareAndSet":
			synchronized (lock) {
				byte[] oldData = cache.get(pair.getLeft(), getRequest().isRenew());
				if (oldData != null && !Arrays.equals(oldData, getRequest().getCmpData())) {
					getResponse().fail(2);
					break;
				}
				cache.set(pair.getLeft(), pair.getRight(), getRequest().getExpireTime());
			}
			getResponse().success();
			break;
		case "Get":
			getResponse().getPair().setRight(cache.get(pair.getLeft(), getRequest().isRenew()));
			getResponse().success();
			break;
		case "Delete":
			synchronized (lock) {
				getResponse().getPair().setRight(cache.delete(pair.getLeft()));
			}
			getResponse().success();
			break;
		case "Stop":
			ServerRuntime.close();
			break;
		}
	}

	@Override
	public void exec() {
		throw new LYException("Method not supported");
	}

}
