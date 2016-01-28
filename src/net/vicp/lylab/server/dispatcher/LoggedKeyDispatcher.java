package net.vicp.lylab.server.dispatcher;

import net.vicp.lylab.core.CoreDef;
import net.vicp.lylab.core.model.Message;
import net.vicp.lylab.server.utils.Logger;

public class LoggedKeyDispatcher extends SimpleKeyDispatcher<Message> {
	@Override
	protected void logger(Message request, Message response) {
		((Logger) CoreDef.config.getConfig("Singleton").getObject("Logger"))
				.appendLine("Access key:" + request.getKey() + 
						"\nBefore:" + request + "\nAfter:" + response);
	}

}