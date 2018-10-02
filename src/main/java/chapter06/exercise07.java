package chapter06;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import chapter06.ListUtils.List;

public class exercise07 {
	public static void main(String... args) {
		Function<List<Double>, Integer> length = list -> ListUtils.foldLeft(list, 0, __ -> y -> y + 1);
		Function<List<Double>, Double> sum = list -> ListUtils.foldLeft(list, 0d, a -> b -> a + b);
		Function<List<Double>, Option<Double>> mean = list -> list.isEmpty()
				? Option.none()
				: Option.some(sum.apply(list) / length.apply(list));

		Function<List<Double>, Option<Double>> variance = list -> mean
				.apply(list)
				.flatMap(m -> mean.apply(ListUtils.map(list, v -> Math.pow(v - m, 2))));

		List<Double> l0 = List.list();
		System.out.println(variance.apply(l0));

		List<Double> l1 = List.list(1d, 1d, 1d, 1d);
		System.out.println(variance.apply(l1));

		List<Double> l2 = List.list(1d, 2d, 3d, 4d);
		System.out.println(variance.apply(l2));
	}

	static abstract class Option<A> {
		@SuppressWarnings("rawtypes")
		private static Option none = new None();
		public abstract A getOrThrow();
		public abstract A getOrElse(A defaultValue);
		public abstract A getOrElse(Supplier<A> defaultValue);
		public Option<A> orElse(Supplier<Option<A>> defaultValue) {
			return map(x -> this).getOrElse(defaultValue);
		}
		public abstract <B> Option<B> map(Function<A, B> mapping);
		public abstract <B> Option<B> flatMap(Function<A, Option<B>> mapping);
		public Option<A> filter(Predicate<A> predicate) {
			return flatMap(x -> predicate.test(x) ? this : none());
		}

		public static <A> Option<A> some(A a) {
			return new Some<>(a);
		}

		@SuppressWarnings("unchecked")
		public static <A> Option<A> none() {
			return none;
		}

		private static class None<A> extends Option<A> {
			private None() {}

			@Override
			public A getOrThrow() {
				throw new IllegalStateException("getOrThrow() called on None");
			}

			@Override
			public A getOrElse(A defaultValue) {
				return defaultValue;
			}

			@Override
			public A getOrElse(Supplier<A> defaultValue) {
				return defaultValue.get();
			}

			@Override
			public <B> Option<B> map(Function<A, B> mapping) {
				return none();
			}

			@Override
			public <B> Option<B> flatMap(Function<A, Option<B>> mapping) {
				return none();
			}

			@Override
			public String toString() {
				return "None";
			}
		}

		private static class Some<A> extends Option<A> {
			private Some(A a) {
				value = a;
			}

			@Override
			public A getOrThrow() {
				return this.value;
			}

			@Override
			public A getOrElse(A defaultValue) {
				return value;
			}

			@Override
			public A getOrElse(Supplier<A> defaultValue) {
				return value;
			}

			@Override
			public <B> Option<B> map(Function<A, B> mapping) {
				return Option.some(mapping.apply(value));
			}

			@Override
			public <B> Option<B> flatMap(Function<A, Option<B>> mapping) {
				return mapping.apply(value);
			}

			@Override
			public String toString() {
				return String.format("Some (%s)", value);
			}

			private A value;
		}
	}
}

