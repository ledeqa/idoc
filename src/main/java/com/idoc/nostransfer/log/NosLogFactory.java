package com.idoc.nostransfer.log;
//package com.autox.nostransfer.log;
//
//import org.apache.log4j.Logger;
//
//import com.autox.nostransfer.EcomTransferManager;
//
///**
// * 
// * <p>Title: 日志工厂，屏蔽log4j中category和logger区别</p> 
// * 
// * <p>Copyright: Copyright (c) 2014</p> 
// * 
// * <p>Company: www.netease.com</p>
// * 
// * @author 	Barney Woo
// * @date 	2014-11-25
// * @version 1.0
// */
//public class NosLogFactory
//{
//	private static NosLog log = new DefaultNosLog();
//	public static void init(String loggerClass)
//	{
//		if (loggerClass != null && !"".equals(loggerClass))
//		{
//			try
//			{
//				log = (NosLog)Class.forName(loggerClass).newInstance();
//			}
//			catch (Exception e)
//			{
//				log.error("initialize logger exception : " + loggerClass, e);
//			}
//		}
//	}
//	
//	public static void info(String msg)
//	{
//		log.info(msg);
//	}
//	public static void error(String msg, Throwable t)
//	{
//		log.error(msg, t);
//	}
//	
//	public static interface NosLog
//	{
//		public void info(String msg);
//		public void error(String msg, Throwable t);
//	}
//	
//	public static class DefaultNosLog implements NosLog
//	{
//		private Logger logger = Logger.getLogger(EcomTransferManager.class);
//		@Override
//		public void info(String msg)
//		{
//			this.logger.info(msg);
//		}
//		@Override
//		public void error(String msg, Throwable t)
//		{
//			this.error(msg, t);
//		}
//	}
//}
