package mms.tools;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class AgentFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		return record.getMillis() + " " +
			record.getMessage() + "\n";
	}

}
