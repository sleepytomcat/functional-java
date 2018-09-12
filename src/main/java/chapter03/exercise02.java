package chapter03;

import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class exercise02 {
	static final Pattern emailPattern = Pattern.compile("^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$");

	static Function<String, Result<String>> emailChecker = s -> Case.match(
			Case.mcase(() -> Result.failure("email " + s + " is invalid.")),
			Case.mcase(() -> s == null, () -> Result.failure("email must not be null")),
			Case.mcase(() -> s.length() == 0, () -> Result.failure("email must not be empty")),
			Case.mcase(() -> emailPattern.matcher(s).matches(), () -> Result.success(s))
	);

	public static void main(String... args) {
		Effect<String> success = email -> System.out.println("Mail sent to " + email);
		Effect<String> failure = errorMessage -> System.err.println("Error message logged: " + errorMessage);
		emailChecker.apply("this.is@my.email").bind(success, failure);
		emailChecker.apply(null).bind(success, failure);
		emailChecker.apply("").bind(success, failure);
		emailChecker.apply("john.doe@acme.com").bind(success, failure);
	}

	interface Result<T> {
		void bind(Effect<T> success, Effect<T> failure);

		static Result<String> failure(String message) {return new Failure(message);}
		static <T> Result<T> success(T value) {return new Success<T>(value);}

		class Success<T> implements Result<T>{
			public Success(T value) {this.value = value;}
			public void bind(Effect<T> successEffect, Effect<T> failureEffect) {successEffect.apply(value);}
			T value;
		};

		class Failure implements Result<String> {
			private final String errorMessage;
			public Failure(String errorMessage) {this.errorMessage = errorMessage;}
			public void bind(Effect<String> successEffect, Effect<String> failureEffect) {failureEffect.apply(errorMessage);}
		}
	}

	@FunctionalInterface
	interface Effect<T> {
		void apply(T t);
	}

	static class Case<T> {
		public Supplier<Boolean> condition;
		public Supplier<Result<T>> value;
		protected Case(Supplier<Boolean> condition, Supplier<Result<T>> value) {
			this.condition = condition;
			this.value = value;
		}

		public static <T> Case<T> mcase(Supplier<Boolean> condition, Supplier<Result<T>> value) {
			return new Case<T>(condition, value);
		}

		public static <T> DefaultCase<T> mcase(Supplier<Result<T>> value) {
			return new DefaultCase<T>(value);
		}

		@SafeVarargs
		public static <T> Result<T> match(DefaultCase<T> defaultCase, Case<T>... matchers) {
			return Arrays.asList(matchers)
					.stream()
					.filter(matcher -> matcher.condition.get())
					.map(matcher -> matcher.value.get())
					.findFirst()
					.orElse(defaultCase.value.get());
		}
	}

	static class DefaultCase<T> extends Case<T> {
		public DefaultCase(Supplier<Result<T>> value) {
			super(()->true, value);
		}
	}
}