package com.packtpub.reactive.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

import com.packtpub.reactive.eight.ResourceManagementExample;
import com.packtpub.reactive.five.CombiningObservablesExample;
import com.packtpub.reactive.five.ConditionalsExample;
import com.packtpub.reactive.five.HandlingErrorsExamples;
import com.packtpub.reactive.five.HttpRequestsExample;
import com.packtpub.reactive.four.FilteringExamples;
import com.packtpub.reactive.four.FlatMapAndFiles;
import com.packtpub.reactive.four.GroupingExamples;
import com.packtpub.reactive.four.MappingExamples;
import com.packtpub.reactive.four.OtherTransformationExample;
import com.packtpub.reactive.four.ScanExamples;
import com.packtpub.reactive.one.IteratorExamples;
import com.packtpub.reactive.one.ReactiveSumExample;
import com.packtpub.reactive.seven.BlockingExample;
import com.packtpub.reactive.six.BufferingExamples;
import com.packtpub.reactive.six.IntervalAndSchedulers;
import com.packtpub.reactive.six.ParallelRequestsExample;
import com.packtpub.reactive.six.SchedulersExamples;
import com.packtpub.reactive.six.SchedulersTypesExamples;
import com.packtpub.reactive.three.ConnectableObservableExample;
import com.packtpub.reactive.three.ObservableCreateExample;
import com.packtpub.reactive.three.ObservableCreateJustExample;
import com.packtpub.reactive.three.ObservableCreateVariousExample;
import com.packtpub.reactive.three.ObservableCreationFromExamples;
import com.packtpub.reactive.three.ObservableCreationFromFutures;
import com.packtpub.reactive.three.SubjectsExample;
import com.packtpub.reactive.two.FunctionsExample;
import com.packtpub.reactive.two.IntroToLambdasExample;
import com.packtpub.reactive.two.MonadsExample;
import com.packtpub.reactive.two.ObservablesAndMonads;
import com.packtpub.reactive.two.ReactiveSumWithLambdas;

public class Runner {

	private final List<Program> programs = new ArrayList<Program>();
	private final Map<String, Action1<String[]>> actions = new HashMap<String, Action1<String[]>>();

	public Runner() {
		actions.put("list", this::listPrograms);
		actions.put("run", this::runProgram);
	}

	public void registerPrograms(Program... programs) {
		this.programs.addAll(Arrays.asList(programs));
	}

	public void listPrograms(String... params) {
		Observable
				.from(this.programs)
				.distinct()
				.map(program -> program.name())
				.lift(new WithIndex<>())
				.map(v -> v.getIndex() + ". " + v.getValue())
				.subscribe(System.out::println, System.err::println,
						System.out::println);
	}

	public void runProgram(String... params) {
		programFromParams(params).doOnNext(this::wellcome).subscribe(
				program -> program.run(),
				e -> System.err.println(e.getMessage()));
	}

	private Observable<Program> programFromParams(String... params) {
		Observable<Program> simpleRun = Observable
				.from(params)
				.first()
				.filter(this::isNumber)
				.map(this::strToInt)
				.filter(index -> 0 <= index && index < this.programs.size())
				.flatMap(
						index -> Observable.from(this.programs)
								.elementAt(index));

		Observable<Program> programByChapter = Observable
				.from(params)
				.skipWhile(p -> p.equals("chapter"))
				.filter(this::isNumber)
				.map(this::strToInt)
				.buffer(2)
				.filter(a -> a.size() == 2)
				.flatMap(
						indices -> Observable.from(this.programs)
								.groupBy(program -> program.chapter())
								.elementAt(indices.get(0)).flatMap(p -> p)
								.elementAt(indices.get(1)));

		return Observable.concat(simpleRun, programByChapter);
	}

	private int strToInt(String str) {
		return Integer.parseInt(str) - 1;
	}

	private boolean isNumber(String p) {
		return p.matches("[0-9]+");
	}

	public void wellcome(Program program) {
		output("Running " + program.name() + "...");
	}

	private Observable<String> commands(String str) {
		return Observable.from(str.split(" ")).map(command -> command.trim());
	}

	public void run() {
		Observable<String> input = CreateObservable.input();

		input.flatMap(
				line -> Observable.zip(toActionObservable(line),
						toActionParamsObservable(line),
						this::commandToActionRunner))
				.cast(ActionRunner.class)
				.subscribe(action -> action.run(),
						e -> System.err.println(e.getMessage()),
						() -> System.out.println("End!"));
	}

	private ActionRunner commandToActionRunner(Action1<String[]> command,
			List<String> args) {
		return new ActionRunner(command, args.toArray(new String[0]));
	}

	private Observable<List<String>> toActionParamsObservable(String line) {
		return commands(line)
				.skip(1)
				.defaultIfEmpty("")
				.<String> flatMap(
						args -> Observable.from(args.split(",")).map(
								arg -> arg.trim())).buffer(9);
	}

	private Observable<Action1<String[]>> toActionObservable(String line) {
		return commands(line).first()
				.filter(action -> actions.containsKey(action))
				.map(action -> actions.get(action));
	}

	public void output(String... messages) {
		Observable.from(messages).subscribe(System.out::println);
	}

	private static Runner init() {
		Runner runner = new Runner();
		runner.registerPrograms(new ReactiveSumExample(),
				new IteratorExamples(), new IntroToLambdasExample(),
				new ReactiveSumWithLambdas(), new FunctionsExample(),
				new MonadsExample(), new ObservablesAndMonads(),
				new ObservableCreationFromExamples(),
				new ObservableCreationFromFutures(),
				new ObservableCreateJustExample(),
				new ObservableCreateVariousExample(),
				new ObservableCreateExample(),
				new ConnectableObservableExample(), new SubjectsExample(),
				new MappingExamples(), new FlatMapAndFiles(),
				new GroupingExamples(), new OtherTransformationExample(),
				new FilteringExamples(), new ScanExamples(),
				new CombiningObservablesExample(),
				new ConditionalsExample(),
				new HandlingErrorsExamples(),
				new HttpRequestsExample(),
				new IntervalAndSchedulers(),
				new SchedulersTypesExamples(),
				new SchedulersExamples(),
				new ParallelRequestsExample(),
				new BufferingExamples(),
				new BlockingExample(),
				new ResourceManagementExample());

		return runner;
	}

	public static void main(String[] args) {
		Runner runner = init();

		runner.output("Example selection interface.",
				"Available commands - 'list', 'describe <number>', 'run <number>'");

		runner.run();
	}
}
