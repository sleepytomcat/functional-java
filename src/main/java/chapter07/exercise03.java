package chapter07;

import java.util.function.Function;
import java.util.function.Supplier;

public class exercise03 {
	public static void main(String... args) {
		Either<Throwable, String> value1 = Either.left(new RuntimeException("error!"));
		Either<Throwable, String> value2 = Either.right("hello, world");

		Function<String, Either<Throwable, String>> mapper = x -> Either.right(x + "!");
		System.out.println(value1.getOrElse(() -> "no value"));
		System.out.println(value1.orElse(() -> Either.left(new IllegalStateException())));

		System.out.println(value2.getOrElse(() -> "no value"));
		System.out.println(value2.orElse(() -> Either.left(new IllegalStateException())));
	}

	static abstract class Either<E, T> {
		public abstract <U> Either<E, U> map(Function<T, U> f);
		public abstract <U> Either<E, U> flatMap(Function<T, Either<E, U>> f);
		public abstract T getOrElse(Supplier<T> defaultValueSupplier);
		public Either<E, T> orElse(Supplier<Either<E, T>> defaultValue) {
			return map(x -> this).getOrElse(defaultValue);
		}

		public static <T, U> Either<T, U> left(T value) {
			return new Left<>(value);
		}

		public static <T, U> Either<T, U> right(U value) {
			return new Right<>(value);
		}

		private static class Left<E, T> extends Either<E, T> {
			private Left(E value) {
				this.value = value;
			}

			@Override
			public <U> Either<E, U> map(Function<T, U> f) {
				return left(value);
			}

			@Override
			public <U> Either<E, U> flatMap(Function<T, Either<E, U>> f) {
				return left(value);
			}

			@Override
			public T getOrElse(Supplier<T> defaultValueSupplier) {
				return defaultValueSupplier.get();
			}

			@Override
			public Either<E, T> orElse(Supplier<Either<E, T>> defaultValue) {
				return defaultValue.get();
			}

			@Override
			public String toString() {
				return "Left is " + value.toString();
			}

			private E value;
		}

		private static class Right<E, T> extends Either<E, T> {
			private Right(T value) {
				this.value = value;
			}

			@Override
			public <U> Either<E, U> map(Function<T, U> f) {
				return right(f.apply(value));
			}

			@Override
			public <U> Either<E, U> flatMap(Function<T, Either<E, U>> f) {
				return f.apply(value);
			}

			@Override
			public T getOrElse(Supplier<T> defaultValueSupplier) {
				return value;
			}

			@Override
			public Either<E, T> orElse(Supplier<Either<E, T>> defaultValue) {
				return right(value);
			}

			@Override
			public String toString() {
				return "Right is " + value.toString();
			}

			private T value;
		}
	}
}

