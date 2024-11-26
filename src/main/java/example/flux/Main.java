package example.flux;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class Main {
    public static void main(String[] args) {
      //  example1();
      //  example2();
        example3BackPressure();
    }

    private static void example3BackPressure() {
        Flux.range(10, 7)
                .doOnRequest((x)-> System.out.println("Получен запрос на "+x+" чисел"))
                .subscribe(new Subscriber<>() {
                    int counter=0;
                    Subscription sub;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        System.out.println("подписка оформлена");
                        this.sub= subscription;
                        System.out.println("запрашиваем 2 числа");
                        sub.request(2);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("integer = " + integer);
                        counter++;
                        if(counter % 2 == 0) {
                            System.out.println("еще 2 числа, пожалуйста");
                            sub.request(2);
                        }
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {
                        System.out.println("конец обработки");
                    }
                });

            Flux.just("Сеня", "Ваня", "Миша")
                    .onBackpressureBuffer(2)
                    .doOnRequest((x)-> System.out.println("Получен запрос на "+x+" имен"))
                    .subscribe((s -> {
                        System.out.println("s = " + s);
                    }));

    }

    private static void example2() {
        //Flux.interval(new Duration(1,0));
        Flux<Integer> range1 = Flux.range(1, 20)
                                .delayElements(Duration.ofMillis(500));
        range1.doOnEach((i)-> System.out.println("поток: "+Thread.currentThread().getName()))
                .subscribe(i-> System.out.println("i = " + i));

        range1.doOnEach((i)-> System.out.println(Thread.currentThread().getName())).subscribe();

        try {
            long c = range1.count().block(Duration.ofMillis(3000));
            System.out.println("c = " + c);
        } catch (Exception e) {
            System.out.println("не дождались");
        }
        Mono<String> mono = Mono.just("Единичная строка");
        Mono<String> mono2 = Mono.just("Еще строка");
        mono.concatWith(mono2)
                .doOnNext(System.out::println)
                .subscribe();
                
    }

    private static void example1() {
        Flux<String> names = Flux.just("Вася", "Петя", "Артур", "Роман");
        names.subscribe(s-> System.out.println("s = " + s));
        names.sort()
                .doFirst(()-> System.out.println("Первый пошел"))
                .doOnNext(name-> System.out.println("name = " + name))
                .doOnComplete(()-> System.out.println("Это всё"))
                .subscribe();

        Flux<String> empty = Flux.empty();
        empty.sort().doOnNext(e-> System.out.println("e = " + e))
                .doOnComplete(()-> System.out.println("конец пустого Flux"))
                .subscribe();
        long a = empty.count().block();
        System.out.println("a = " + a);
        long b = names.count().block();
        System.out.println("b = " + b);
    }
    
    
}