package it.vitalegi.test.logger;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;

@SpringBootTest
public class LoggerTests {

	final String PATTERN = "%d{yyyy-MM-dd HH:mm:ss.SSS};%X{userId};%msg%n";
	final String REGEX = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3};[^;]+;[^;]+$";

	Logger log = (Logger) LoggerFactory.getLogger(this.getClass());

	MemoryAppender memoryAppender;

	PatternLayout layout;

	@BeforeEach
	public void setup() {
		memoryAppender = new MemoryAppender();
		LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

		layout = new PatternLayout();
		layout.setPattern(PATTERN);
		layout.setContext(context);
		layout.start();

		memoryAppender.setContext(context);
		log.addAppender(memoryAppender);
		memoryAppender.start();
	}

	@Test
	public void test_loggerShouldFormatLine() {
		MDC.put("userId", "user1");
		log.info("aaaaaa");
		log.info("bbbbbb");
		memoryAppender.stop();
		List<String> messages = memoryAppender.formatAll(layout);
		Assertions.assertThat(messages.size()).isEqualTo(2);
		String pattern = REGEX;
		messages.forEach(msg -> {
			Assertions.assertThat(msg.trim()).matches(pattern);
		});
	}

	@Test
	public void test_additivity() {
		org.slf4j.Logger log1 = LoggerFactory.getLogger(this.getClass());
		org.slf4j.Logger log2 = LoggerFactory.getLogger("gdpr");
		org.slf4j.Logger log3 = LoggerFactory.getLogger("com.acme");
		log1.debug("1 - should be visible in ICT");
		log1.info("2 - should be visible in ICT");
		log1.error("3 - should be visible in ICT");
		log2.debug("4 - should NOT be visible in SIEM");
		log2.info("5 - should be visible in SIEM");
		log2.error("6 - should be visible in SIEM");
		log3.debug("7 - should NOT be visible in ICT");
		log3.info("8 - should be visible in ICT");
		log3.error("9 - should be visible in ICT");
	}
}
