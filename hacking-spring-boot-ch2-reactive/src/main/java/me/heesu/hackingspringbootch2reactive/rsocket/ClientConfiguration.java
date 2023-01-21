package me.heesu.hackingspringbootch2reactive.rsocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;
import static org.springframework.http.MediaType.parseMediaType;

@Configuration
public class ClientConfiguration {

    @Bean
    public RSocketRequester rSocketRequester(RSocketStrategies rSocketStrategies){
        RSocketRequester.Builder builder = RSocketRequester.builder();

        return builder
                .rsocketStrategies(rSocketStrategies)
                .metadataMimeType(parseMediaType("message/x.rsocket.routing.v0"))
                .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
                .tcp("localhost", 7000);
                //.connectTcp("localhost", 7000)
                //.retry(5)

    }
}
