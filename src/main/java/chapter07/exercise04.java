package chapter07;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;

public class exercise04 {
	public static void main(String... args) {
		Result<Integer> failure1 = Result.failure("some generic error");
		Result<Integer> failure2 = Result.failure(new RuntimeException("some unchecked exception"));
		Result<Integer> failure3 = Result.failure(new InterruptedException("some checked exception"));
		Result<Integer> success = Result.success(42);

		System.out.println(failure1);
		System.out.println(failure2);
		System.out.println(failure3);
		System.out.println(success);

		System.out.println(failure1.map(x -> x + 1));
		System.out.println(failure1.flatMap(x -> Result.success(x + 1)));
		System.out.println(failure1.getOrElse(43));
		System.out.println(failure1.getOrElse(() -> 43));
		System.out.println(failure1.orElse(() -> Result.success(101)));

		System.out.println(success.map(x -> x + 1));
		System.out.println(success.flatMap(x -> Result.success(x + 1)));
		System.out.println(success.getOrElse(43));
		System.out.println(success.getOrElse(() -> 43));
		System.out.println(success.orElse(() -> Result.success(101)));
	}

	static abstract class Result<V> implements Serializable {
		public static <V> Result<V> failure(String message) {return new Failure<V>(message);}
		public static <V> Result<V> failure(Exception ex) {return new Failure<V>(ex);} // for checked exceptions
		public static <V> Result<V> failure(RuntimeException ex) {return new Failure<V>(ex);} // for unchecked exceptions
		public static <V> Result<V> success(V value) {return new Success<>(value);}

		public abstract V getOrElse(final V defaultValue);
		public abstract V getOrElse(final Supplier<V> defaultValueSupplier);

		public abstract <U> Result<U> map(Function<V, U> f);
		public abstract <U> Result<U> flatMap(Function<V, Result<U>> f);
		public Result<V> orElse(Supplier<Result<V>> defaultValueSupplier) {
			return map(x -> this).getOrElse(defaultValueSupplier);
		}

		private static class Failure<V> extends Result<V> {
			private Failure(String message) {
				super();
				this.exception = new IllegalStateException(message);
			}

			private Failure(RuntimeException ex) {
				super();
				this.exception = ex;
			}

			private Failure(Exception ex) {
				super();
				this.exception = new IllegalStateException(ex.getMessage(), ex);
			}

			@Override
			public V getOrElse(final V defaultValue) {
				return defaultValue;
			}

			@Override
			public V getOrElse(final Supplier<V> defaultValueSupplier) {
				return defaultValueSupplier.get();
			}

			@Override
			public <U> Result<U> map(Function<V, U> f) {
				return failure(exception);
			}

			@Override
			public <U> Result<U> flatMap(Function<V, Result<U>> f) {
				return failure(exception);
			}

			@Override
			public String toString() {
				Throwable current = exception;
				String result = "(Failure) ";
				while (current != null) {
					result += current.toString() + " ";
					current = current.getCause();
				}
				return result;
			}

			private final RuntimeException exception;
		}

		private static class Success<V> extends Result<V> {
			private Success(V value) {
				this.value = value;
			}

			@Override
			public V getOrElse(final V defaultValue) {
				return value;
			}

			@Override
			public V getOrElse(final Supplier<V> defaultValueSupplier) {
				return value;
			}

			@Override
			public <U> Result<U> map(Function<V, U> f) {
				try {
					return success(f.apply(value));
				}
				catch (Exception ex) {
					return failure(ex);
				}
			}

			@Override
			public <U> Result<U> flatMap(Function<V, Result<U>> f) {
				try {
					return f.apply(value);
				}
				catch (Exception ex) {
					return failure(ex);
				}
			}

			@Override
			public String toString() {
				return "(Success) " + value.toString();
			}

			private final V value;
		}
	}
}