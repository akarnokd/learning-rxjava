package com.packtpub.reactive.three;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import rx.Observable;
import rx.schedulers.Schedulers;

import com.packtpub.reactive.common.Program;

/*
 * FIXME If we target Java 8, it is worth creating an example to interact with CompletableFuture.
 */
public class ObservableCreationFromFutures implements Program {

	@Override
	public String name() {
		return "Using Observable.from with Future";
	}

	@Override
	public int chapter() {
		return 3;
	}

	@Override
	public void run() {
		ByteBuffer buffer = ByteBuffer.allocate(512);
		Path path = Paths.get("src", "main", "resources", "lorem.txt");

		/*
		 * FIXME Warning! The try may close the async channel at any time and even prevent 
		 * the observable from receiving any value.
		 */
		try (AsynchronousFileChannel asyncChannel = AsynchronousFileChannel
				.open(path)) {

			Observable
					.from(asyncChannel.read(buffer, 0))
					.subscribeOn(Schedulers.io())
					.subscribe(
							(b) -> buffer.flip(),
							System.err::println,
							() -> System.out.println(StandardCharsets.UTF_8
									.decode(buffer)));

			System.out.println("Before Lorem");
			System.out.println("We can do other things!");

		} catch (IOException e) {
		}

		/*
		 * FIXME, consider using CountDownLatch instead of a timed wait.
		 */
		try {
			System.out.println("Waiting for the thread to end...");
			System.out.println("--------------------------------");
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
        new ObservableCreationFromFutures().run();
    }
}
