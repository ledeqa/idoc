package com.idoc.protocol.server;

import org.apache.mina.core.session.IoSession;

import com.idoc.protocol.message.RequestMessage;

public interface RequestMessageDispatcher {
	String dispatch(IoSession session, RequestMessage req);
}