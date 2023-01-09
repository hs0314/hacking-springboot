package me.heesu.hackingspringbootch2reactive;

import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.Repository;

import java.util.stream.Stream;

public interface HttpTraceWrapperRepository extends Repository<HttpTraceWrapper, String> {

    Stream<HttpTraceWrapper> findAll();

    void save(HttpTraceWrapper trace);
}
