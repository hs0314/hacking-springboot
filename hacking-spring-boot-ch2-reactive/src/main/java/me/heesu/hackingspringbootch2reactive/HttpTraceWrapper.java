package me.heesu.hackingspringbootch2reactive;

import org.springframework.boot.actuate.trace.http.HttpTrace;
import org.springframework.data.annotation.Id;

/**
 * Actuator의 httptrace를 활용하기 위한 HttpTraceRepository 인터피스 커스텀 구현체
 *  - InMemoryHttpTraceRepository()의 한계(휘발성, 인스턴스가 각자의 정보만 알고있음)를 해결하기위함
 *  -
 */
public class HttpTraceWrapper {

    private @Id String id;

    private HttpTrace httpTrace;

    public HttpTraceWrapper(HttpTrace httpTrace) {
        this.httpTrace = httpTrace;
    }

    public HttpTrace getHttpTrace() {
        return httpTrace;
    }
}
