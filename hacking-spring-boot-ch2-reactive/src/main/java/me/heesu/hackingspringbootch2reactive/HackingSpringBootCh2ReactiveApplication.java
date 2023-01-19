package me.heesu.hackingspringbootch2reactive;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.TemplateEngine;
import reactor.blockhound.BlockHound;
@SpringBootApplication
public class HackingSpringBootCh2ReactiveApplication {

	@Bean
	Jackson2JsonMessageConverter jackson2JsonMessageConverter(){
		return new Jackson2JsonMessageConverter();
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
