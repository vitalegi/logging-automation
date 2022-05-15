package it.vitalegi.test.logger;

import java.util.List;
import java.util.stream.Collectors;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

public class MemoryAppender extends ListAppender<ILoggingEvent> {

	public List<String> formatAll(PatternLayout layout) {
		return this.list.stream().map(e -> layout.doLayout(e)).collect(Collectors.toList());
	}
}
