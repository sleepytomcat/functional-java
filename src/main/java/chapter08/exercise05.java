package chapter08;

import java.io.Serializable;
import java.util.function.Function;
import java.util.function.Supplier;

public class exercise05 {
	public static void main(String... args) {
		System.out.println(flattenResult(List.list()));
		System.out.println(flattenResult(List.list(Result.success(1), Result.success(2))));
		System.out.println(flattenResult(List.list(Result.success(1), Result.empty())));
		System.out.println(flattenResult(List.list(Result.success(1), Result.failure("some failure"))));
		System.out.println(flattenResult(List.list(Result.success(1), Result.empty(), Result.failure("some failure"))));
	}

	public static <A> List<A> flattenResult(List<Result<A>> list) {
		return foldRight(list, List.list(), x -> y -> x.map(v -> y.cons(v)).getOrElse(() -> y));
	}

	static abstract class List<T> {
		public abstract T head();
		public abstract List<T> tail();
		public abstract boolean isEmpty();
		public abstract long lengthMemoized();
		public Result<T> headOption() {return foldRight(this, Result.empty(), x -> __ -> Result.success(x));};
		public Result<T> lastOption() {return foldLeft(this, Result.empty(), x -> __ -> Result.success(x));}

		public static final List NIL = new Nil();
		public List<T> cons(T head) {return new Cons<>(head, this);}
		private List() {}

		private static class Nil<T> extends List<T> {
			@Override public T head() {throw new IllegalStateException("head() called on empty list");}
			@Override public List<T> tail() {throw new IllegalStateException("tail() called on empty list");}
			@Override public boolean isEmpty() {return true;}
			@Override public String toString() {return "[NIL]";}
			@Override public long lengthMemoized() {return 0;}
			@Override public Result<T> headOption() {return Result.empty();}
		}

		private static class Cons<T> extends List<T> {
			private Cons(T head, List<T> tail) {
				this.head = head;
				this.tail = tail;
				length = tail.isEmpty() ? 1: tail.lengthMemoized() + 1;
			}

			@Override public T head() {return head;}
			@Override public List<T> tail() {return tail;}
			@Override public boolean isEmpty() {return false;}
			@Override public String toString() {
				return toString("", this).eval();
			}
			@Override public long lengthMemoized() {return length;}
			@Override public Result<T> headOption() {return Result.success(head);}

			private static <T> TailCall<String> toString(String accumulator, List<T> list) {
				if (list.isEmpty())
					return TailCall.ret("[" + accumulator + " NIL]");
				else
					return TailCall.sus(() -> toString(accumulator + list.head() + ", ", list.tail()));
			}

			private T head;
			private List<T> tail;
			private long length;
		}

		@SuppressWarnings("unchecked")
		static <T> List<T> list() {
			return NIL;
		}

		@SafeVarargs
		static <T> List<T> list(T ... a) {
			List<T> n = list();
			for (int i = a.length - 1; i >= 0; i--) {
				n = new Cons<>(a[i], n);
			}
			return n;
		}

		public static <T> List<T> reverse(List<T> list) {
			return foldLeft(list, List.list(), head -> accumulator -> new Cons<>(head, accumulator));
		}
	}

	public static <U, V> V foldLeft(List<U> list, V identity, Function<U, Function<V, V>> folding) {
		return foldLeft_(list, identity, folding).eval();
	}

	private static <U, V> TailCall<V> foldLeft_(List<U> list, V accumulator, Function<U, Function<V, V>> folding) {
		return list.isEmpty()
				? TailCall.ret(accumulator)
				: TailCall.sus(() -> foldLeft_(list.tail(), folding.apply(list.head()).apply(accumulator), folding));
	}

	public static <U, V> V foldRight(List<U> list, V identity, Function<U, Function<V, V>> folding) {
		return foldRight_(List.reverse(list), identity, folding).eval();
	}

	private static <U, V> TailCall<V> foldRight_(List<U> list, V accumulator, Function<U, Function<V, V>> folding) {
		return list.isEmpty()
				? TailCall.ret(accumulator)
				: TailCall.sus(() -> foldRight_(list.tail(), folding.apply(list.head()).apply(accumulator), folding));
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

		public abstract void forEach(Effect<V> effect);
		public abstract void forEachOrThrow(Effect<V> effect);
		public abstract Result<RuntimeException> forEachOrException(Effect<V> effect);

		private static class Failure<V> extends Empty<V> {
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

			@Override
			public void forEachOrThrow(Effect<V> effect) {
				super.forEachOrThrow(effect);
				throw exception;
			}

			@Override
			public Result<RuntimeException> forEachOrException(Effect<V> effect) {
				return success(exception);
			}

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

			@Override
			public void forEach(Effect<V> effect) {
				effect.apply(value);
			}

			@Override
			public void forEachOrThrow(Effect<V> effect) {
				effect.apply(value);
			}

			@Override
			public Result<RuntimeException> forEachOrException(Effect<V> effect) {
				effect.apply(value);
				return empty();
			}

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
			public void forEach(Effect<V> __) {
				// Empty. Do nothing.
			}

			@Override
			public void forEachOrThrow(Effect<V> __) {
				// Empty. Do nothing.
			}

			@Override
			public Result<RuntimeException> forEachOrException(Effect<V> effect) {
				return empty();
			}

			@Override
			public String toString() {return "(Empty)";}
		}

		@SuppressWarnings("rawtypes")
		private static Result empty = new Empty();
	}

	interface Effect<T> {
		void apply(T t);
	}

	static <A, B> Function<Result<A>, Result<B>> lift(final Function<A, B> f) {
		return x -> {
			try {
				return x.map(v -> f.apply(v));
			}
			catch (Exception ex) {
				return Result.failure(ex);
			}
		};
	}

	public static <A, B, C> Function<Result<A>, Function<Result<B>, Result<C>>> lift2(Function<A, Function<B, C>> f) {
		return a -> b -> a.map(f).flatMap(b::map);
	}

	public static <A, B, C, D> Function<Result<A>, Function<Result<B>, Function<Result<C>, Result<D>>>> lift3(Function<A, Function<B, Function<C, D>>> f) {
		return a -> b -> c -> a.map(f).flatMap(b::map).flatMap(c::map);
	}

	public static <A, B, C> Result<C> map2(Result<A> a, Result<B> b, Function<A, Function<B, C>> f) {
		return lift2(f).apply(a).apply(b);
	}
}