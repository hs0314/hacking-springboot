package me.heesu.hackingspringbootch2reactive;

import org.bson.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;
import org.springframework.boot.actuate.trace.http.InMemoryHttpTraceRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.thymeleaf.TemplateEngine;
import reactor.blockhound.BlockHound;

import java.net.URI;
import java.util.Collections;
import java.util.Map;


@SpringBootApplication
public class HackingSpringBootCh2ReactiveApplication {

	@Bean
	HttpTraceRepository traceRepository(HttpTraceWrapperRepository repository){
		// actuator의 httptrace는 특정 인터페이스의 구현체를 개발자가 지정해줘야 해당 endpoint를 활성화

		// 해당 구현체는 한계가 있기 때문에(데이터 휘발성, 인스턴스가 자신에게 들어온 요청만 알 수 있음) 커스텀 구현체 구현 가능
		//return new InMemoryHttpTraceRepository();

		// 커스텀 구현체
		return new SpringDataHttpTraceRepository(repository);
	}

	// MongoDB 조회결과(Document)를 HttpTraceWrapper로 변환하기위한 컨버터
	static Converter<Document, HttpTraceWrapper> CONVERTER = new Converter<Document, HttpTraceWrapper>() {
		@Override
		public HttpTraceWrapper convert(Document document) {
			Document httpTrace = document.get("httpTrace", Document.class);
			Document request = httpTrace.get("request", Document.class);
			Document response = httpTrace.get("response", Document.class);

			return new HttpTraceWrapper(new HttpTrace(
				new HttpTrace.Request(
						request.getString("method"),
						URI.create(request.getString("uri")),
						request.get("headers", Map.class),
						null
				),
				new HttpTrace.Response(
						response.getInteger("status"),
						request.get("headers", Map.class)
				),
				httpTrace.getDate("timestamp").toInstant(),
				null,
				null,
				httpTrace.getLong("timeTaken")
			));
		}
	};

	@Bean
	public MappingMongoConverter mappingMongoConverter(MongoMappingContext context){
		// 커스텀 mongoDb converter 셋팅
		MappingMongoConverter converter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, context);

		converter.setCustomConversions(new MongoCustomConversions(Collections.singletonList(CONVERTER)));

		return converter;
	}

	public static void main(String[] args) {
		//BlockHound.install();
		// reactor.blockhound.BlockingOperationError: Blocking call! java.io.RandomAccessFile#readBytes

		BlockHound.builder()
				.allowBlockingCallsInside(
						TemplateEngine.class.getCanonicalName(), "process") // 특정 메서드콜에 대해서 blockhound 허용 리스트에 추가가 가능
				.install();

		SpringApplication.run(HackingSpringBootCh2ReactiveApplication.class, args);
	}

}
