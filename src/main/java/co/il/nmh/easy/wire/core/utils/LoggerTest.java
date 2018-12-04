package co.il.nmh.easy.wire.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.event.Level;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Maor Hamami
 */

@Slf4j
public class LoggerTest implements Logger
{
	private Map<Level, List<String>> logs;

	public LoggerTest()
	{
		logs = new HashMap<>();

		for (Level level : Level.values())
		{
			logs.put(level, new ArrayList<>());
		}
	}

	@Override
	public String getName()
	{
		return log.getName();
	}

	@Override
	public boolean isTraceEnabled()
	{
		return log.isTraceEnabled();
	}

	@Override
	public void trace(String msg)
	{
		logs.get(Level.TRACE).add(msg);
		log.trace(msg);
	}

	@Override
	public void trace(String format, Object arg)
	{
		String message = format(format, arg);
		logs.get(Level.TRACE).add(message);
		log.trace(format, arg);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2)
	{
		String message = format(format, arg1, arg2);
		logs.get(Level.TRACE).add(message);
		log.trace(format, arg1, arg2);
	}

	@Override
	public void trace(String format, Object... arguments)
	{
		String message = format(format, arguments);
		logs.get(Level.TRACE).add(message);
		log.trace(format, arguments);
	}

	@Override
	public void trace(String msg, Throwable t)
	{
		logs.get(Level.WARN).add(msg);
		log.trace(msg, t);
	}

	@Override
	public boolean isTraceEnabled(Marker marker)
	{
		return log.isTraceEnabled(marker);
	}

	@Override
	public void trace(Marker marker, String msg)
	{
		log.trace(marker, msg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg)
	{
		log.trace(marker, format, arg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2)
	{
		log.trace(marker, format, arg1, arg2);
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray)
	{
		log.trace(marker, format, argArray);
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t)
	{
		log.trace(marker, msg, t);
	}

	@Override
	public boolean isDebugEnabled()
	{
		return log.isDebugEnabled();
	}

	@Override
	public void debug(String msg)
	{
		logs.get(Level.DEBUG).add(msg);
		log.debug(msg);
	}

	@Override
	public void debug(String format, Object arg)
	{
		String message = format(format, arg);
		logs.get(Level.DEBUG).add(message);
		log.debug(format, arg);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2)
	{
		String message = format(format, arg1, arg2);
		logs.get(Level.DEBUG).add(message);
		log.debug(format, arg1, arg2);
	}

	@Override
	public void debug(String format, Object... arguments)
	{
		String message = format(format, arguments);
		logs.get(Level.DEBUG).add(message);
		log.debug(format, arguments);
	}

	@Override
	public void debug(String msg, Throwable t)
	{
		logs.get(Level.DEBUG).add(msg);
		log.debug(msg, t);
	}

	@Override
	public boolean isDebugEnabled(Marker marker)
	{
		return log.isDebugEnabled(marker);
	}

	@Override
	public void debug(Marker marker, String msg)
	{
		log.debug(marker, msg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg)
	{
		log.debug(marker, format, arg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2)
	{
		log.debug(marker, format, arg1, arg2);
	}

	@Override
	public void debug(Marker marker, String format, Object... arguments)
	{
		log.debug(marker, format, arguments);
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t)
	{
		log.debug(marker, msg, t);
	}

	@Override
	public boolean isInfoEnabled()
	{
		return log.isInfoEnabled();
	}

	@Override
	public void info(String msg)
	{
		logs.get(Level.INFO).add(msg);
		log.info(msg);
	}

	@Override
	public void info(String format, Object arg)
	{
		String message = format(format, arg);
		logs.get(Level.INFO).add(message);
		log.info(format, arg);
	}

	@Override
	public void info(String format, Object arg1, Object arg2)
	{
		String message = format(format, arg1, arg2);
		logs.get(Level.INFO).add(message);
		log.info(format, arg1, arg2);
	}

	@Override
	public void info(String format, Object... arguments)
	{
		String message = format(format, arguments);
		logs.get(Level.INFO).add(message);
		log.info(format, arguments);
	}

	@Override
	public void info(String msg, Throwable t)
	{
		logs.get(Level.INFO).add(msg);
		log.info(msg, t);
	}

	@Override
	public boolean isInfoEnabled(Marker marker)
	{
		return log.isInfoEnabled(marker);
	}

	@Override
	public void info(Marker marker, String msg)
	{
		log.info(marker, msg);
	}

	@Override
	public void info(Marker marker, String format, Object arg)
	{
		log.info(marker, format, arg);
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2)
	{
		log.info(marker, format, arg1, arg2);
	}

	@Override
	public void info(Marker marker, String format, Object... arguments)
	{
		log.info(marker, format, arguments);
	}

	@Override
	public void info(Marker marker, String msg, Throwable t)
	{
		log.info(marker, msg, t);
	}

	@Override
	public boolean isWarnEnabled()
	{
		return log.isWarnEnabled();
	}

	@Override
	public void warn(String msg)
	{
		logs.get(Level.WARN).add(msg);
		log.warn(msg);
	}

	@Override
	public void warn(String format, Object arg)
	{
		String message = format(format, arg);
		logs.get(Level.WARN).add(message);
		log.warn(format, arg);
	}

	@Override
	public void warn(String format, Object... arguments)
	{
		String message = format(format, arguments);
		logs.get(Level.WARN).add(message);
		log.warn(format, arguments);
	}

	@Override
	public void warn(String format, Object arg1, Object arg2)
	{
		String message = format(format, arg1, arg2);
		logs.get(Level.WARN).add(message);
		log.warn(format, arg1, arg2);
	}

	@Override
	public void warn(String msg, Throwable t)
	{
		log.warn(msg, t);
	}

	@Override
	public boolean isWarnEnabled(Marker marker)
	{
		return log.isWarnEnabled(marker);
	}

	@Override
	public void warn(Marker marker, String msg)
	{
		log.warn(marker, msg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg)
	{
		log.warn(marker, format, arg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2)
	{
		log.warn(marker, format, arg1, arg2);
	}

	@Override
	public void warn(Marker marker, String format, Object... arguments)
	{
		log.warn(marker, format, arguments);
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t)
	{
		log.warn(marker, msg, t);
	}

	@Override
	public boolean isErrorEnabled()
	{
		return log.isErrorEnabled();
	}

	@Override
	public void error(String msg)
	{
		logs.get(Level.ERROR).add(msg);
		log.error(msg);
	}

	@Override
	public void error(String format, Object arg)
	{
		String message = format(format, arg);
		logs.get(Level.ERROR).add(message);
		log.error(format, arg);
	}

	@Override
	public void error(String format, Object arg1, Object arg2)
	{
		String message = format(format, arg1, arg2);
		logs.get(Level.ERROR).add(message);
		log.error(format, arg1, arg2);
	}

	@Override
	public void error(String format, Object... arguments)
	{
		String message = format(format, arguments);
		logs.get(Level.ERROR).add(message);
		log.error(format, arguments);
	}

	@Override
	public void error(String msg, Throwable t)
	{
		logs.get(Level.ERROR).add(msg);
		log.error(msg, t);
	}

	@Override
	public boolean isErrorEnabled(Marker marker)
	{
		return log.isErrorEnabled(marker);
	}

	@Override
	public void error(Marker marker, String msg)
	{
		log.error(marker, msg);
	}

	@Override
	public void error(Marker marker, String format, Object arg)
	{
		log.error(marker, format, arg);
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2)
	{
		log.error(marker, format, arg1, arg2);
	}

	@Override
	public void error(Marker marker, String format, Object... arguments)
	{
		log.error(marker, format, arguments);
	}

	@Override
	public void error(Marker marker, String msg, Throwable t)
	{
		log.error(marker, msg, t);
	}

	public String getLastLog(Level level)
	{
		List<String> list = logs.get(level);

		if (!list.isEmpty())
		{
			return list.get(list.size() - 1);
		}

		return null;
	}

	public List<String> getLogs(Level level)
	{
		return logs.get(level);
	}

	public void clearLogs()
	{
		for (Level level : Level.values())
		{
			logs.get(level).clear();
		}
	}

	public void clearLogs(Level level)
	{
		logs.get(level).clear();
	}

	public static String format(String msg, Object... arguments)
	{
		if (null == msg)
		{
			return null;
		}
		int parameterIndex;
		int index = 0;
		do
		{
			parameterIndex = msg.indexOf("{}");
			if (parameterIndex > -1 && arguments.length > index)
			{
				String newMessage = msg.substring(0, parameterIndex);
				newMessage += arguments[index++];
				newMessage += msg.substring(parameterIndex + 2);
				msg = newMessage;
			}
		} while (parameterIndex > -1 && arguments.length > index);
		return msg;
	}
}
