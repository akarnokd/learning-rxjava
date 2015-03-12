package com.packtpub.reactive.two;

import java.util.regex.Pattern;

import rx.Observable;

import com.packtpub.reactive.common.CreateObservable;
import com.packtpub.reactive.common.Program;

public class ReactiveSumWithLambdas implements Program {

	public static Observable<Double> varStream(final String varName,
			Observable<String> input) {
		final Pattern pattern = Pattern.compile("\\s*" + varName
				+ "\\s*[:|=]\\s*(-?\\d+\\.?\\d*)$");

		return input
				.map(str -> pattern.matcher(str))
				.filter(matcher -> {
                    boolean m = matcher.matches();
                    if (m) {
                        String g = matcher.group(1);
                        return g != null;
                    }
                    return false;
                })
				.map(matcher -> matcher.group(1))
				.map(str -> Double.parseDouble(str))
				.doOnNext(System.out::println);
	}

	public static void reactiveSum(Observable<Double> a, Observable<Double> b) {
		Observable.combineLatest(a, b, (x, y) -> x + y).subscribe(
				sum -> System.out.println("update : a + b = " + sum),
				error -> {
					System.out.println("Got an error!");
					error.printStackTrace();
				}, () -> System.out.println("Exiting..."));
	}

	public String name() {
		return "Reactive Sum : Lambdas";
	}

	public void run() {
		Observable<String> input = CreateObservable.input();

		Observable<Double> a = varStream("a", input);
		Observable<Double> b = varStream("b", input);

		reactiveSum(a, b);

		// FIXME main thread has to wait for exit too
		input.filter(s -> "exit".equals(s)).toBlocking().lastOrDefault("");
	}

	public static void main(String[] args) {
		new ReactiveSumWithLambdas().run();
	}

	@Override
	public int chapter() {
		return 2;
	}
}
