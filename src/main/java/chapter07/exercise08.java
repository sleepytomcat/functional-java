package chapter07;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;

public class exercise08 {
	public static void main(String... args) {
		System.out.println(Result.of(null));
		System.out.println(Result.of(42));

		System.out.println(Result.of((Integer)null, "is null"));
		System.out.println(Result.of(42, "is null"));

		System.out.println(Result.of(x -> x < 42, (Integer)null));
		System.out.println(Result.of(x -> x < 42, 42));
		System.out.println(Result.of(x -> x >= 42, 42));

		System.out.println(Result.of(x -> x < 42, (Integer)null, "no match"));
		System.out.println(Result.of(x -> x < 42, 42, "no match"));
		System.out.println(Result.of(x -> x >= 42, 42, "no match"));
	}

	static abstract class Result<V> implements Serializable {
		public static <V> Result<V> failure(String message) {return new Failure<V>(message);}
		public static <V> Result<V> failure(Exception ex) {return new Failure<V>(ex);} // for checked exceptions
		public static <V> Result<V> failure(RuntimeException ex) {return new Failure<V>(ex);} // for unchecked exceptions
		public static <V> Result<V> success(V value) {return new Success<>(value);}
		@SuppressWarnings("unchecked")
		public static <V> Result<V> empty() {return empty;}

		public abstract V getOrElse(final V defaultValue);
		public abstract V getOrElse(final Supplier<V> defaultValueSupplier);

		public abstract <U> Result<U> map(Function<V, U> f);
		public abstract <U> Result<U> flatMap(Function<V, Result<U>> f);
		public Result<V> orElse(Supplier<Result<V>> defaultValueSupplier) {
			return map(x -> this).getOrElse(defaultValueSupplier);
		}

		public Result<V> filter(Function<V, Boolean> f) {
			return flatMap(x -> f.apply(x) ? this : failure("Condition not matched"));
		}

		public Result<V> filter(Function<V, Boolean> f, String errorMessage) {
			return flatMap(x -> f.apply(x) ? this : failure(errorMessage));
		}

		public boolean exists(Function<V, Boolean> p) {
			return map(x -> p.apply(x)).getOrElse(false);
		}

		public abstract Result<V> mapFailure(String newMessage);

		public static <V> Result<V> of(V value) {
			return value == null
					? failure("null value")
					: success(value);
		}

		public static <V> Result<V> of(V value, String message) {
			return value == null
					? failure(message)
					: success(value);
		}

		public static <V> Result<V> of(Function<V, Boolean> predicate, V value) {
			try {
				return predicate.apply(value)
						? success(value)
						: empty();
			}
			catch (Exception ex) {
				return failure(new IllegalStateException("Did not matched predicate", ex));
			}
		}

		public static <V> Result<V> of(Function<V, Boolean> predicate, V value, String message) {
			try {
				return predicate.apply(value)
						? success(value)
						: empty();
			}
			catch (Exception ex) {
				return failure(new IllegalStateException(message, ex));
			}
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
			public V getOrElse(final V defaultValue) {return defaultValue;}

			@Override
			public V getOrElse(final Supplier<V> defaultValueSupplier) {return defaultValueSupplier.get();}

			@Override
			public <U> Result<U> map(Function<V, U> f) {return failure(exception);}

			@Override
			public <U> Result<U> flatMap(Function<V, Result<U>> f) {return failure(exception);}

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

			@Override
			public Result<V> mapFailure(String newMessage) {return failure(new IllegalStateException(newMessage, exception));}

			private final RuntimeException exception;
		}

		private static class Success<V> extends Result<V> {
			private Success(V value) {this.value = value;}

			@Override
			public V getOrElse(final V defaultValue) {return value;}

			@Override
			public V getOrElse(final Supplier<V> defaultValueSupplier) {return value;}

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
			public String toString() {return "(Success) " + value.toString();}

			@Override
			public Result<V> mapFailure(String newMessage) {return this;}

			private final V value;
		}

		private static class Empty<V> extends Result<V> {
			private Empty() {super();}

			public V getOrElse(final V defaultValue) {return defaultValue;}
			public V getOrElse(final Supplier<V> defaultValueSupplier) {return defaultValueSupplier.get();}

			public <U> Result<U> map(Function<V, U> f) {return empty();}
			public <U> Result<U> flatMap(Function<V, Result<U>> f) {return empty();};
			public Result<V> orElse(Supplier<Result<V>> defaultValueSupplier) {return defaultValueSupplier.get();}

			@Override
			public Result<V> mapFailure(String newMessage) {return this;}

			@Override
			public String toString() {return "(Empty)";}
		}

		@SuppressWarnings("rawtypes")
		private static Result empty = new Empty();
	}
}