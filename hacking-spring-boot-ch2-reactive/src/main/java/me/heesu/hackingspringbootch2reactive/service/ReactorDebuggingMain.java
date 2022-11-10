package me.heesu.hackingspringbootch2reactive.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReactorDebuggingMain {


    public static void main(String[] args){

        /**
         * case 1. 명령형 코드
         */
        /*
        ExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        List<Integer> source;
        if(new Random().nextBoolean()){
            source = IntStream.range(1,11).boxed()
                    .collect(Collectors.toList());
        }else{
            source = Arrays.asList(1,2,3,4);
        }

        try{
            executor.submit(() -> source.get(5)).get(); // error 발생 부분
            // xxx: executor를 통해서 새로운 쓰레드 위에서 돌기 때문에 어떤 경로를 통해서 리스트 생성이 됐는지 알 수 없음
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            executor.shutdown();
        }
         */

        /**
         * case 2. 리액터 예시
         */
        Mono<Integer> source;

        Hooks.onOperatorDebug(); // 운영환경에서의 호출은 지양
        /*
        *____Flux.elementAt ⇢ at me.heesu.hackingspringbootch2reactive.service.ReactorDebuggingMain.main(ReactorDebuggingMain.java:56)
        |_ Mono.subscribeOn ⇢ at me.heesu.hackingspringbootch2reactive.service.ReactorDebuggingMain.main(ReactorDebuggingMain.java:61)
         */

        if(new Random().nextBoolean()){
            source = Flux.range(1,10).elementAt(5);
        }else{
            source = Flux.just(1,2,3,4).elementAt(5);
        }

        //xxx: 리액터 플로우가 여러 쓰레드에서 병렬처리가 되고 실제 문제가되는 Flux생성 시점까지는 트레이스 출력 불가하지만
        // Hooks.onOperatorDebug(); 활성화를 통해서 트레이스 출력 가능
        source.subscribeOn(Schedulers.parallel()).block();

    }
}
