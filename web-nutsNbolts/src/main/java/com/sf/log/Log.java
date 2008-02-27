package com.sf.log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;


//import java.util.Date;

/**
 * @author i027910
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class Log {
	
	boolean enable = true;
	boolean isEnable(){
		return enable;
	}
	void enable(){
		enable = true;
	}
	void enable(boolean b){
		enable = b;
	}
	void disable(){
		enable = false;
	}
	
	static HashMap instanceList = new HashMap();
//	static HashMap<String, Boolean> instanceStatList = new HashMap<String, Boolean>();
	synchronized public static Log getInstance(String keyName) {
		if (keyName == null)
			return null;
		return (Log) (instanceList.get(keyName));
	}
	static synchronized public Log getInst(String keyName) {
		if (keyName == null)
			return getDefaultInst();
		Log ret = getInstance(keyName);
		if (ret == null)
			ret = createInstance(keyName);
		if (ret == null)
			return getDefaultInst();
		return ret;
	}
	synchronized public static Log createInstance(String keyName) {
		if (keyName == null)
			return null;
		Log l = new Log(keyName);
		l.bCached = _defInst.bCached;
		l.bLogEnable = _defInst.bLogEnable;
		l.bWarningEnable = _defInst.bWarningEnable;
		l.bErrorEnable = _defInst.bErrorEnable;

		l.bTraceEnable = _defInst.bTraceEnable;
		l.bPosEnable = _defInst.bPosEnable;
		l.bLogModuleName = _defInst.bLogModuleName;
		l.bCached = _defInst.bCached;
		l.nTraceLevel = _defInst.nTraceLevel;
		l.MAX_CACHESIZE = _defInst.MAX_CACHESIZE;
		l.SWEEP_INTERVAL = _defInst.SWEEP_INTERVAL;
		l.MAX_WAIT_NUMBER = _defInst.MAX_WAIT_NUMBER;
		l.curWaitNumber = _defInst.curWaitNumber;

		l.stackDeep = _defInst.stackDeep;
		l.file = _defInst.file;

		instanceList.put(keyName, l);
//		instanceStatList.put(keyName, true);
		return l;
	}
	
//	synchronized boolean isInstEnable(String category){
//		if (category == null)
//			return true;
//		Boolean b = instanceStatList.get(category);
//		if (b == null)
//			return true;
//		return b.booleanValue();
//	}
//	
//	synchronized void enableInstance(String category){
//		if (category == null)
//			return;
//		Boolean b = instanceStatList.get(category);
//		if (b != null)			
//			instanceStatList.put(category, true);
//	}
//	
//	synchronized void disableInstance(String category){
//		if (category == null)
//			return;
//		Boolean b = instanceStatList.get(category);
//		if (b != null)			
//			instanceStatList.put(category, false);
//	}
	
	public static void enableCategory(String category){
		getInst(category).enable();
	}
	public static void enableCategory(String category, boolean b){
		getInst(category).enable(b);
	}
	public static void disableCategory(String category){
		getInst(category).disable();
	}
	//	synchronized  
	public static Log getDefaultInst() {
		//		if (_defInst == null)
		//		 _defInst = new Log();		
		return _defInst;
	}

	public boolean bLogEnable = true;
	public boolean bWarningEnable = true;
	public boolean bErrorEnable = true;
	//	 public boolean bDebugEnable = true;       
	public boolean bTraceEnable = true;
	public boolean bPosEnable = true;
	public boolean bLogModuleName = true;
	public boolean bCached = true;
	public int nTraceLevel = TRACELEVEL_NORMAL;
	public int MAX_CACHESIZE = 1000;
	public int SWEEP_INTERVAL = 1000;
	public int MAX_WAIT_NUMBER = 10;
	public int curWaitNumber = 0;
	public String category = "";
	public int stackDeep = 1;
	public String file = null;

	public StringBuffer cache = new StringBuffer(1000);
	private Sweeper sweeper = new Sweeper();
	public Object dummy = new Object();
	private static Log _defInst = new Log(null);
	protected Log(String c) {
		if (c != null)
			this.category = c;
		else
			category = "";
		sweeper.start();
	}

	// define trace level                              
	static public final int TRACELEVEL_BASIC = 10;
	static public final int TRACELEVEL_NORMAL = 100;
	static public final int TRACELEVEL_DETAIL = 200;

	static public final int TRACELEVEL_STEP = 200;

	//	
	//	/*
	//	 * static variable
	//	 */
	//	static public boolean bLogEnable = true;
	//	static public boolean bWarningEnable = true;
	//	static public boolean bErrorEnable = true;
	//	//	static public boolean bDebugEnable = true;
	//	static public boolean bTraceEnable = true;
	//	static public boolean bPosEnable = true;
	//	static public boolean bLogModuleName = true;
	//	static public boolean bCached = true;
	//	static public StringBuffer m_cache = new StringBuffer(1000);
	//	static public int MAX_CACHESIZE = 1000;
	//	static public int SWEEP_INTERVAL = 1000;
	//	static private Sweeper m_sweeper = new Sweeper();
	//	static private int MAX_WAIT_NUMBER = 10;
	//	static private int m_curWaitNumber = 0;
	//
	//	// define trace level
	//	static public final int TRACELEVEL_BASIC = 10;
	//	static public final int TRACELEVEL_NORMAL = 100;
	//	static public final int TRACELEVEL_DETAIL = 200;
	//
	//	static public final int TRACELEVEL_STEP = 200;
	//
	//	static private int m_nTraceLevel = TRACELEVEL_NORMAL;

	class Sweeper extends Thread {
		public Sweeper() {
		}

		public void run() {
			while (true) {
				try {

					sleep(SWEEP_INTERVAL);
				} catch (Exception e) {
					System.out.println(e);
				}
				sweepCache();
				curWaitNumber++;
				if (curWaitNumber > MAX_WAIT_NUMBER) {
					_flush();
				}

			}
		}
	}

	//	static {
	//		m_sweeper.start();
	//	}

	synchronized static public void setCache(
		boolean bCacheLog,
		int nCacheSize,
		int nFlushInterval) {
		getDefaultInst()._setCache(bCacheLog, nCacheSize, nFlushInterval);
		Iterator it = instanceList.values().iterator();

		while (it.hasNext()) {
			Log inst = (Log) (it.next());
			inst._setCache(bCacheLog, nCacheSize, nFlushInterval);
		}
	}

	public void _setCache(
		boolean bCacheLog,
		int nCacheSize,
		int nFlushInterval) {
		bCached = bCacheLog;
		MAX_CACHESIZE = nCacheSize;
		SWEEP_INTERVAL = nFlushInterval;
		_flush();
	}

	static public String printExpStack(Throwable e) {
		return getDefaultInst()._printExpStack(e);
	}

	public String _printExpStack(Throwable e) {
		if (e == null)
			return "exception = null";
		StackTraceElement[] ret = e.getStackTrace();
		int i;
		String s = "" + e.toString() + "\r\n";
		for (i = 0; i < ret.length; i++) {
			s = s + ret[i].toString() + "\r\n";
		}
		s += "------>caused by<------: \r\n" + _printExpStack(e.getCause());
		return s;
	}

	private String getPos(int levelStart, int deep) {

		Exception e = new Exception();
		StackTraceElement[] ret = e.getStackTrace();
		String retStr = "";
		int j = 0;
		for (int i = levelStart; i < ret.length && j < deep; i++, j++) {
			if (j > 0)
				retStr += " <-- ";
			retStr += ret[i].toString();
		}

		return retStr;

	}

	static public String getStack() {
		return getDefaultInst()._getStack();

	}

	public String _getStack() {
		try {
			throw new Exception("dummy");
		} catch (Exception e) {
			return printExpStack(e);
		}

	}

	static private String convertToString(Object obj) {
		if (obj instanceof Throwable) {
			return ""
				+ ((Exception) obj) + ":" + ((Exception) obj).getMessage()
				+ "\r\n****stack***\r\n"
				+ printExpStack((Exception) obj)
				+ "\r\n****end of stack***\r\n";
		} else
			return String.valueOf(obj);
	}

	/**
	 * the physical action to put log content to target device
	 * @param s
	 */
	private void putReal(String s) {

		if (s == null || s.length() <= 0)
			return;

		if (file == null) // print to std out
			{

			System.out.println(s);
			curWaitNumber = 0;
			return;
		}
		try {

			//write buffer to file
			String currentFile = getDefaultInst().file;
			//String default_file = getDefaultInst().file;
			//Timestamp date = new Timestamp(System.currentTimeMillis());
			int nIndex = currentFile.indexOf(".");
			Calendar calendar = Calendar.getInstance();
			String sDate =
				""
					+ calendar.get(Calendar.YEAR)
					+ ""
					+ (calendar.get(Calendar.MONTH) + 1)
					+ ""
					+ calendar.get(Calendar.DAY_OF_MONTH);
			//System.out.println(sDate);
			//			System.out.println(currentFile);
			//			System.out.println(category);
			if (nIndex < 0) {
				if (category == null || category.length() == 0)
					currentFile = file + "_" + sDate;
				else
					currentFile = file + "_" + category + "_" + sDate;
			} else {
				String filename = currentFile.substring(0, nIndex);
				String fileext =
					currentFile.substring(nIndex, currentFile.length());
				if (category == null || category.length() == 0)
					currentFile = filename + "_" + sDate + fileext;
				else
					currentFile =
						filename + "_" + category + "_" + sDate + fileext;
			}
			//			System.out.println(currentFile);
			DataOutputStream a =
				new DataOutputStream(new FileOutputStream(currentFile, true));
			a.writeBytes(s);
			a.flush();
			a.close();

		} catch (Exception e) {			
			System.out.println(
				"---------------------\nwrite log to file "
					+ file
					+ " failed: exception("
					+ e
					+ "). log msg:\n"
					+ s
					+ "\n---------------------");
			e.printStackTrace();
		}
		curWaitNumber = 0;
	}

	private void sweepCache() {
		if (!bCached)
			return;

		if (cache.length() > MAX_CACHESIZE) {
			_flush();

		}
	}

	static public void flush() {
		getDefaultInst()._flush();
	}

	/**
	 * force to flush the cache
	 */
	public void _flush() {
		synchronized (dummy) {

			putReal(cache.toString());

			// clear buffer
			cache.setLength(0);

		}
	}
	/**
	 * put into cache or system out directly
	 * @param s
	 */
	private void put(String s) {

		synchronized (dummy) {

			if (bCached) {

				// put new log into cache
				cache.append(s + "\r\n");

			} else
				putReal(s + "\r\n");

		}
	}
	static public void setTraceLevel(int l) {
		getDefaultInst()._setTraceLevel(l);
		Iterator it = instanceList.values().iterator();

		while (it.hasNext()) {
			Log inst = (Log) (it.next());
			inst._setTraceLevel(l);
		}
	}
	public void _setTraceLevel(int l) {
		nTraceLevel = l;
	}

	synchronized static public void setFile(String filename) {
		getDefaultInst()._setFile(filename);
		Iterator it = instanceList.values().iterator();

		while (it.hasNext()) {
			Log inst = (Log) (it.next());
			inst._setFile(filename);
		}
	}
	
	public boolean _enableCache(boolean enable){
		boolean ret = bCached;
		this.bCached = enable;
		return ret;
	}
	
	public void _setFile(String filename) {
		boolean old = _enableCache(false);
		log("log redirect to file " + new File(filename).getAbsolutePath());
		_enableCache(old);
		file = filename;

		String title =
			"************************************************\r\n"
				+ "log redirect from std output on "
				+ (new Timestamp(System.currentTimeMillis())).toString()
				+ "\r\n"
				+ "************************************************\r\n";
		put(title);
		_flush();
	}

	synchronized static public void set(
		String sLogFile,
		boolean bLogEnabled,
		boolean bWarningEnabled,
		boolean bTraceEnabled,
		boolean bErrorEnabled,
		int tracelevel,
		int nLogRefreshInterval,
		boolean bCached,
		int nCacheSize)
		throws Exception {
		getDefaultInst()._set(
			sLogFile,
			bLogEnabled,
			bWarningEnabled,
			bTraceEnabled,
			bErrorEnabled,
			tracelevel,
			nLogRefreshInterval,
			bCached,
			nCacheSize);
		Iterator it = instanceList.values().iterator();

		while (it.hasNext()) {
			Log inst = (Log) (it.next());
			inst._set(
				sLogFile,
				bLogEnabled,
				bWarningEnabled,
				bTraceEnabled,
				bErrorEnabled,
				tracelevel,
				nLogRefreshInterval,
				bCached,
				nCacheSize);
		}
	}
	/**
	 * set log setting
	 * @param sLogFile				log file
	 * @param bLogEnabled			enable log or not
	 * @param bWarningEnabled		eable warning or not
	 * @param bTraceEnabled			enbale trace or not
	 * @param bErrorEnabled			enable error or not
	 * @param tracelevel			the level of trace, any trace <= this value will be put in to log content
	 * @param nLogRefreshInterval	the refresh time for log writer to peep the cache
	 * @param bCached				cache log content or not
	 * @param nCacheSize			the size of cache
	 * @throws Exception
	 */
	public void _set(
		String sLogFile,
		boolean bLogEnabled,
		boolean bWarningEnabled,
		boolean bTraceEnabled,
		boolean bErrorEnabled,
		int tracelevel,
		int nLogRefreshInterval,
		boolean bCached,
		int nCacheSize)
		throws Exception {
			
		_event("set log: " +
			"category=" + this.category  + "\r\n" +
			"sLogFile="+sLogFile + "\r\n" +
			"bLogEnabled="+bLogEnabled+ "\r\n" +
			"bWarningEnabled="+bWarningEnabled+ "\r\n" +
			"bTraceEnabled="+bTraceEnabled+ "\r\n" +
			"bErrorEnabled="+bErrorEnabled+ "\r\n" +
			"tracelevel="+tracelevel+ "\r\n" +
			"nLogRefreshInterval="+nLogRefreshInterval+	 "\r\n" +		
			"bCached="+bCached+ "\r\n" +
			"nCacheSize="+nCacheSize + "\r\n" );
			
		if (sLogFile != null && sLogFile.length() > 0)
			_setFile(sLogFile);

		_enableLog(bLogEnabled);
		_enableWarning(bWarningEnabled);
		_enableTrace(bTraceEnabled);
		_enableError(bErrorEnabled);

		if (tracelevel >= 0) {
			setTraceLevel(tracelevel);
		}
		if (nLogRefreshInterval <= 0) {
			Log.error(
				"the value("
					+ nLogRefreshInterval
					+ "ms) for log refresh interval is not valid.");
			//nLogRefreshInterval = 10000;
		} else if (nCacheSize <= 0) {
			Log.error(
				"the value("
					+ nCacheSize
					+ "Byte) for log cache size is not valid, defaut value.");
		} else
			_setCache(bCached, nCacheSize, nLogRefreshInterval);
		
	}

	static public void enablePos(boolean b) {
		getDefaultInst()._enablePos(b);

		Iterator it = instanceList.values().iterator();
		while (it.hasNext()) {
			Log inst = (Log) (it.next());
			inst._enablePos(b);
		}

	}
	static public void enableLog(boolean b) {
		getDefaultInst()._enableLog(b);
		Iterator it = instanceList.values().iterator();
		while (it.hasNext()) {
			Log inst = (Log) (it.next());
			inst._enableLog(b);
		}

	}

	static public void enableTrace(boolean b) {
		getDefaultInst()._enableTrace(b);
		Iterator it = instanceList.values().iterator();
		while (it.hasNext()) {
			Log inst = (Log) (it.next());
			inst._enableTrace(b);
		}
	}

	static public void enableError(boolean b) {
		getDefaultInst()._enableError(b);
		Iterator it = instanceList.values().iterator();
		while (it.hasNext()) {
			Log inst = (Log) (it.next());
			inst._enableError(b);
		}

	}

	//	static public void enableDebug(boolean b){
	//		bDebugEnable = b;
	//		if (b)
	//			event("debug enabled.");
	//		else
	//		   event("debug disabled.");
	//	}

	static public void enableWarning(boolean b) {
		getDefaultInst()._enableWarning(b);
		Iterator it = instanceList.values().iterator();
		while (it.hasNext()) {
			Log inst = (Log) (it.next());
			inst._enableWarning(b);
		}
	}

	static public void event(Object ob) {

		getDefaultInst()._event(ob);

	}

	static public void log(Object ob) {
		getDefaultInst()._log(ob);
	}

	static public void trace(Object ob) {
		getDefaultInst()._trace(ob);
	}

	static public void trace(Object ob, int nLevel) {
		getDefaultInst()._trace(ob, nLevel);
	}

	//	static public void debug(Object ob){
	//		if (!bDebugEnable)
	//					return;
	//		
	//		Timestamp t = new Timestamp(System.currentTimeMillis());
	//		String s = t.toString() + "\t[debug]\tthread:" + Thread.currentThread().getName() + "\t" + convertToString(ob);
	//		put(s);
	//	}
	static public void error(Object ob) {
		getDefaultInst()._error(ob);
	}

	static public void warning(Object ob) {
		getDefaultInst()._warning(ob);
	}

	static public void event(String category, Object ob) {
		getInst(category)._event(ob);
	}

	static public void log(String category, Object ob) {
		getInst(category)._log(ob);
	}

	static public void trace(String category, Object ob) {
		getInst(category)._trace(ob);
	}

	static public void trace(String category, Object ob, int nLevel) {
		getInst(category)._trace(ob, nLevel);
	}

	static public void error(String category, Object ob) {
		getInst(category)._error(ob);
	}

	static public void error(String msg, Exception e) {
		getDefaultInst()._error(msg+"caused by exception:"+ Log.printExpStack(e));		
	}
	
	static public void error(String category, String msg, Exception e) {
		getInst(category)._error(msg+"caused by exception:"+ Log.printExpStack(e));		
	}
	
	static public void warning(String category, Object ob) {
		getInst(category)._warning(ob);
	}

	static public void warning(String msg, Exception e) {
		getDefaultInst()._warning(msg+"caused by exception:"+ Log.printExpStack(e));
		
	}
	
	public void _enablePos(boolean b) {
		bPosEnable = b;
	}
	public void _enableLog(boolean b) {
		bLogEnable = b;
		if (b)
			event("log enabled.");
		else
			event("log disabled.");
	}

	public void _enableTrace(boolean b) {
		bTraceEnable = b;
		if (b)
			event("trace enabled.");
		else
			event("trace disabled.");
	}

	public void _enableError(boolean b) {
		bErrorEnable = b;
		if (b)
			event("error enabled.");
		else
			event("error disabled.");
	}

	//	static public void enableDebug(boolean b){
	//		bDebugEnable = b;
	//		if (b)
	//			event("debug enabled.");
	//		else
	//		   event("debug disabled.");
	//	}

	public void _enableWarning(boolean b) {
		bWarningEnable = b;
		if (b)
			event("warning enabled.");
		else
			event("warning disabled.");
	}

	public void _event(Object ob) {

		Timestamp t = new Timestamp(System.currentTimeMillis());
		String s =
			t.toString()
				+ "\t[event "
				+ category
				+ "]\tthread:"
				+ Thread.currentThread().getName()
				+ "\t"
				+ convertToString(ob);
		if (bPosEnable) {
			s += "(" + getPos(3, stackDeep) + ")";
		}

		put(s);
//		if (bLogToJ2EE)
//			LOGManager.Instance().Log("ACC", s, LOGSeverity.SYS_SEVERITY_INFO);

	}

	public void _log(Object ob) {

		if (!isEnable())
			return;
		
		if (!bLogEnable)
			return;
		Timestamp t = new Timestamp(System.currentTimeMillis());
		String s =
			t.toString()
				+ "\t[log "
				+ category
				+ "]\tthread:"
				+ Thread.currentThread().getName()
				+ "\t"
				+ convertToString(ob);
		if (bPosEnable) {
			s += "(" + getPos(3, stackDeep) + ")";
		}
		put(s);
//		if (bLogToJ2EE)
//			LOGManager.Instance().Log("ACC", s, LOGSeverity.SYS_SEVERITY_INFO);

	}

	public void _trace(Object ob) {
		if (!isEnable())
			return;
		
		
		// trace(ob, 100); can not can trace, because the stack will incorret
		if (!bTraceEnable)
			return;
		Timestamp t = new Timestamp(System.currentTimeMillis());
		String s =
			t.toString()
				+ "\t[trace-"
				+ 100
				+ " "
				+ category
				+ "]\tthread:"
				+ Thread.currentThread().getName()
				+ "\t"
				+ convertToString(ob);
		if (bPosEnable) {
			s += "(" + getPos(3, stackDeep) + ")";
		}
		put(s);
	}

	public void _trace(Object ob, int nLevel) {
		if (!isEnable())
			return;
		
		if (!bTraceEnable || nLevel > nTraceLevel)
			return;
		Timestamp t = new Timestamp(System.currentTimeMillis());
		String s =
			t.toString()
				+ "\t[trace-"
				+ nLevel
				+ " "
				+ category
				+ "]\tthread:"
				+ Thread.currentThread().getName()
				+ "\t"
				+ convertToString(ob);
		if (bPosEnable) {
			s += "(" + getPos(3, stackDeep) + ")";
		}
		put(s);
	}

	//	static public void debug(Object ob){
	//		if (!bDebugEnable)
	//					return;
	//		
	//		Timestamp t = new Timestamp(System.currentTimeMillis());
	//		String s = t.toString() + "\t[debug]\tthread:" + Thread.currentThread().getName() + "\t" + convertToString(ob);
	//		put(s);
	//	}
	public void _error(Object ob) {
		if (!isEnable())
			return;
		
		if (!bErrorEnable)
			return;

		Timestamp t = new Timestamp(System.currentTimeMillis());
		String s =
			t.toString()
				+ "\t[error "
				+ category
				+ "]\tthread:"
				+ Thread.currentThread().getName()
				+ "\t"
				+ convertToString(ob);
		if (bPosEnable) {
			s += "(" + getPos(3, stackDeep) + ")";
		}
		put(s);
//		if (bLogToJ2EE)
//			LOGManager.Instance().Log("ACC", s, LOGSeverity.SYS_SEVERITY_ERROR);
	}

	public void _warning(Object ob) {
		if (!isEnable())
			return;
		
		if (!bWarningEnable)
			return;

		Timestamp t = new Timestamp(System.currentTimeMillis());
		String s =
			t.toString()
				+ "\t[warning "
				+ category
				+ "]\tthread:"
				+ Thread.currentThread().getName()
				+ "\t"
				+ convertToString(ob);
		if (bPosEnable) {
			s += "(" + getPos(3, stackDeep) + ")";
		}
		put(s);
//		if (bLogToJ2EE)
//			LOGManager.Instance().Log(
//				"ACC",
//				s,
//				LOGSeverity.SYS_SEVERITY_WARNING);
	}
	
	///////////////////////////////////////
	// comply with the new log interface
	///////////////////////////////////////

	static public void event(Class cl, Object ob) {

		getDefaultInst()._event(ob);

	}

	static public void log(Class cl, Object ob) {
		getDefaultInst()._log(ob);
	}

	static public void trace(Class cl, Object ob) {
		getDefaultInst()._trace(ob);
	}

	static public void trace(Class cl, Object ob, int nLevel) {
		getDefaultInst()._trace(ob, nLevel);
	}

	//	static public void debug(Object ob){
	//		if (!bDebugEnable)
	//					return;
	//		
	//		Timestamp t = new Timestamp(System.currentTimeMillis());
	//		String s = t.toString() + "\t[debug]\tthread:" + Thread.currentThread().getName() + "\t" + convertToString(ob);
	//		put(s);
	//	}
	static public void error(Class cl, Object ob) {
		getDefaultInst()._error(ob);
	}

	static public void warning(Class cl, Object ob) {
		getDefaultInst()._warning(ob);
	}

	static public void event(Class cl, String category, Object ob) {
		getInst(category)._event(ob);
	}

	static public void log(Class cl, String category, Object ob) {
		getInst(category)._log(ob);
	}

	static public void trace(Class cl, String category, Object ob) {
		getInst(category)._trace(ob);
	}

	static public void trace(Class cl, String category, Object ob, int nLevel) {
		getInst(category)._trace(ob, nLevel);
	}

	static public void error(Class cl, String category, Object ob) {
		getInst(category)._error(ob);
	}

	static public void warning(Class cl, String category, Object ob) {
		getInst(category)._warning(ob);
	}
	static public void criticalLog(String file, Object ob) {
		Timestamp t = new Timestamp(System.currentTimeMillis());
		String s =
			t.toString()
				+ "\t[critical "
				+ "]\tthread:"
				+ Thread.currentThread().getName()
				+ "\t"
				+ convertToString(ob);
		
			s += "(" + getStack() + ")" + "\r\n";
	
		try {
			DataOutputStream a =
				new DataOutputStream(new FileOutputStream(file, true));
			a.writeBytes(s);
			a.flush();
			a.close();

		} catch (Exception e) {
			System.out.println(
				"---------------------\nwrite log to file "
					+ file
					+ " failed: exception("
					+ e
					+ "). log msg:\n"
					+ s
					+ "\n---------------------");
			e.printStackTrace();
		}
	}
	
}