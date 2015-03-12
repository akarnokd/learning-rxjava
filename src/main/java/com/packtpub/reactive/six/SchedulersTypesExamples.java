package com.packtpub.reactive.six;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import rx.Scheduler;
import rx.Scheduler.Worker;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import com.packtpub.reactive.common.Program;

public class SchedulersTypesExamples implements Program {

	@Override
	public String name() {
		return "Demonstrates the different Schedulers types.";
	}

	@Override
	public int chapter() {
		return 6;
	}
	
	
	public void schedule(Scheduler scheduler, int numberOfSubTasks, boolean onTheSameWorker) {
		List<Integer> list = new ArrayList<>(0);
		AtomicInteger current = new AtomicInteger(0);
		
		Random random = new Random();
		Worker worker = scheduler.createWorker();

		Action0 addWork = () -> {

			synchronized (current) {
				System.out.println("  Add : " + Thread.currentThread().getName() + " " + current.get());
				list.add(random.nextInt(current.get()));
				System.out.println("  End add : " + Thread.currentThread().getName() + " " + current.get());
			}

			
		};

		Action0 removeWork = () -> {

			synchronized (current) {
				if (!list.isEmpty()) {
					System.out.println("  Remove : " + Thread.currentThread().getName());
					list.remove(0);
					System.out.println("  End remove : " + Thread.currentThread().getName());
				}
			}

			
		};


		Action0 work = () -> {
			System.out.println(Thread.currentThread().getName());

			for (int i = 1; i <= numberOfSubTasks; i++) {
				current.set(i);
				
				System.out.println("Begin add!");
				if (onTheSameWorker) {
					worker.schedule(addWork);
				} else {
				    /*
				     * FIXME this is dangerous as it causes Scheduler.Worker (memory) leak.
				     */
					scheduler.createWorker().schedule(addWork);
				}
				System.out.println("End add!");
			}
			
			while (!list.isEmpty()) {
				System.out.println("Begin remove!");
					
				if (onTheSameWorker) {
					worker.schedule(removeWork);
				} else {
                    /*
                     * FIXME this is dangerous as it causes Scheduler.Worker (memory) leak.
                     */
					scheduler.createWorker().schedule(removeWork);
				}
				
				System.out.println("End remove!");
			}
			
		};
		
		worker.schedule(work);
		
        /*
         * FIXME worker is not unsubscribed anywhere and causes Scheduler.Worker (memory) leak
         */

	}

	@Override
	public void run() {

		System.out.println("Immediate");
		schedule(Schedulers.immediate(), 2, true);
		System.out.println("Spawn!");
		schedule(Schedulers.immediate(), 2, false);

		System.out.println("Trampoline");
		schedule(Schedulers.trampoline(), 2, true);
		System.out.println("Spawn!");
		schedule(Schedulers.trampoline(), 2, false);
		
		
		System.out.println("New thread");
		schedule(Schedulers.newThread(), 2, true);
		System.out.println("Spawn!");
		schedule(Schedulers.newThread(), 2, false);
		
		System.out.println("Computation thread");
		schedule(Schedulers.computation(), 5, true);
		System.out.println("Spawn!");
		schedule(Schedulers.computation(), 5, false);
		
		System.out.println("IO thread");
		schedule(Schedulers.io(), 2, true);
		System.out.println("Spawn!");
		schedule(Schedulers.io(), 2, false);
		
	}

}
