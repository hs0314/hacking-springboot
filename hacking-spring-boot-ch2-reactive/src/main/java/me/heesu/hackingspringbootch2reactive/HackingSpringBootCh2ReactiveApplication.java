package me.heesu.hackingspringbootch2reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.blockhound.BlockHound;


@SpringBootApplication
public class HackingSpringBootCh2ReactiveApplication {

	public static void main(String[] args) {
		BlockHound.install();
		// reactor.blockhound.BlockingOperationError: Blocking call! java.io.RandomAccessFile#readBytes

		SpringApplication.run(HackingSpringBootCh2ReactiveApplication.class, args);
	}

}
