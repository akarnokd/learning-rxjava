package com.packtpub.reactive.three;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;

import com.packtpub.reactive.common.Program;

public class ObservableCreationFromExamples implements Program {

	public static Action1<?> N = (v) -> {
	};
	public static Action1<Throwable> NE = (e) -> {
	};

	@Override
	public String name() {
		return "Creating Observables With Observable.from";
	}

	@Override
	public int chapter() {
		return 3;
	}

	@Override
	public void run() {
		// from(list)
		List<String> list = Arrays.asList("blue", "red", "green", "yellow",
				"orange", "cyan", "purple");

		Observable<String> listObservable = Observable.from(list);
		listObservable.subscribe(System.out::println);
		listObservable.subscribe(color -> System.out.print(color + "|"), NE,
				System.out::println);
		listObservable.subscribe(color -> System.out.print(color + "/"), NE,
				System.out::println);

		// from(Iterable)
		Path resources = Paths.get("src", "main", "resources");
		DirectoryStream<Path> dStream;
		try {
			dStream = Files.newDirectoryStream(resources);
			/*
			 * FIXME Warning! DirectoryStream is a single-use only, meaning that it doesn't support
			 * concurrent streaming and can't be repeated because further iterations yield empty results.
			 * In addition, the contract on DirectoryStream requires it to be closed after use which
			 * Observable.from() can't do.
			 */
			Observable<Path> dirObservable = Observable.from(dStream);
			dirObservable.subscribe(System.out::println);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// from(array)
		Observable<Integer> arrayObservable = Observable.from(new Integer[] {
				3, 5, 8 });
		arrayObservable.subscribe(System.out::println);
	}

}
