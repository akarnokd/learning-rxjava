package com.packtpub.reactive.five;

import static com.packtpub.reactive.common.Helpers.subscribePrint;

import java.util.concurrent.TimeUnit;

import rx.Observable;

import com.packtpub.reactive.common.Program;

public class ConditionalsExample implements Program {

	@Override
	public String name() {
		return "Some examples of using conditionals.";
	}

	@Override
	public int chapter() {
		return 5;
	}

	@Override
	public void run() {

		Observable<String> words = Observable.just("Some", "Other");
		Observable<Long> interval = Observable
				.interval(500L, TimeUnit.MILLISECONDS).take(2);

		Observable<? extends Object> amb = Observable.amb(words, interval);
		subscribePrint(amb, "Amb 1");

		amb = Observable.amb(words.zipWith(interval, (x, y) -> x), interval);
		subscribePrint(amb, "Amb 2");
		

		amb = Observable.amb(interval, words.zipWith(interval, (x, y) -> x));
		subscribePrint(amb, "Amb 3");
		
		
		words = Observable.just("one", "way", "or", "another", "I'll", "learn", "RxJava")
				.zipWith(Observable.interval(200L, TimeUnit.MILLISECONDS), (x, y) -> x);
		
		subscribePrint(words.takeUntil(interval).delay(1L, TimeUnit.SECONDS), "takeUntil");
		subscribePrint(words.delay(800L, TimeUnit.MILLISECONDS).takeWhile(word -> word.length() > 2), "takeWhile");

		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
		}

		subscribePrint(words.skipUntil(interval), "skipUntil");
		
		
		Observable<Object> test = Observable.empty().defaultIfEmpty(5);
		subscribePrint(test, "defaultIfEmpty");
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
		}
	}

}
