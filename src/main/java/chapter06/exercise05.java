package chapter06;

import java.util.function.Function;
import java.util.function.Supplier;

public class exercise05 {
	public static void main(String... args) {
		Option<String> maybe = Option.some("hello, world!");
		System.out.println(maybe.orElse(() -> Option.some("...hmmm")));

		Option<String> nothing = Option.none();
		System.out.println(nothing.orElse(() -> Option.some("...hmmm")));
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

