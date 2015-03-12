package com.packtpub.reactive.three;

import static com.packtpub.reactive.common.Helpers.subscribePrint;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import com.packtpub.reactive.common.Program;

public class ObservableCreateExample implements Program {

	@Override
	public String name() {
		return "Showcases the Observable.create method.";
	}

	@Override
	public int chapter() {
		return 3;
	}

	/*
	 * FIXME 
	 * Since RxJava has a from(Iterable) method which handles unsubscription and backpressure,
	 * I assume this example is to give a feel about how an observable may constructed, but
	 * must be accompanied by a warning that disregarding backpressure has consequences when
	 * this Observable is combined with other observables.
	 */
	public static <T> Observable<T> fromIterable(final Iterable<T> iterable) {
		return Observable.create(new OnSubscribe<T>() {
			@Override
			public void call(Subscriber<? super T> subscriber) {
				Iterator<T> iterator = iterable.iterator();

				while (iterator.hasNext()) {
					if (subscriber.isUnsubscribed()) {
						return;
					}
					subscriber.onNext(iterator.next());
				}

				if (!subscriber.isUnsubscribed()) {
					subscriber.onCompleted();
				}

			}
		});
	}

	@Override
	public void run() {
		subscribePrint(fromIterable(Arrays.asList(1, 3, 5)), "List1");
		subscribePrint(fromIterable(Arrays.asList(2, 4, 6)), "List2");

		try {
			Path path = Paths.get("src", "main", "resources", "lorem_big.txt");
			List<String> data = Files.readAllLines(path);

			Observable<String> observable = fromIterable(data).subscribeOn(
					Schedulers.computation());

			Subscription subscription = subscribePrint(observable, "File");
			System.out.println("Before unsubscribe!");
			System.out.println("-------------------");

            /*
             * FIXME Warning! Since observable runs on another thread, 
             * the unsubscription may happen any time, even before any line was read, making
             * this example highly non-deterministic.
             * I suggest sleeping a bit before unsubscribing and even slowing down line delivery
             */
			subscription.unsubscribe();

			System.out.println("-------------------");
			System.out.println("After unsubscribe!");

			/*
			 * FIXME It may be non-obvious why one needs to wait after unsubscription: the reason
			 * is that Schudulers.io() and Schedulers.computation() use daemon threads which stop
			 * if all other non-daemon threads complete. If one can't wait for the terminal event of
			 * an observer, the only thing remaining is to sleep some arbitrary amount of time.
			 */
			Thread.sleep(100L);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
        new ObservableCreateExample().run();
    }
}
