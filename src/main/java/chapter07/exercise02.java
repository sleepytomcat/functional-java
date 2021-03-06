package chapter07;

import java.util.function.Function;

public class exercise02 {
	public static void main(String... args) {
		Either<Throwable, String> value1 = Either.left(new RuntimeException("error!"));
		Either<Throwable, String> value2 = Either.right("hello, world");

		Function<String, Either<Throwable, String>> mapper = x -> Either.right(x + "!");
		System.out.println(value1.flatMap(mapper));
		System.out.println(value2.flatMap(mapper));
	}

	static abstract class Either<E, T> {
		public abstract <U> Either<E, U> map(Function<T, U> f);
		public abstract <U> Either<E, U> flatMap(Function<T, Either<E, U>> f);

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
			public String toString() {
				return "Right is " + value.toString();
			}

			private T value;
		}
	}
}

