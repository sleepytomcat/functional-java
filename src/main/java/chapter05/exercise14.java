package chapter05;

import java.util.function.Function;

public class exercise14 {
	public static void main(String... args) {
		List<Double> list = List.list(1D, 2D, 3D, 4D, 5D, 7D);
		System.out.println(foldRight(list, "0.0", x -> y -> "(" + x.toString() + " + " + y.toString() + ")"));
	}

	static abstract class List<T> {
		public abstract T head();
		public abstract List<T> tail();
		public abstract boolean isEmpty();

		public static final List NIL = new Nil();
		private List() {}
		private static class Nil<T> extends List<T> {
			public T head() {
				throw new IllegalStateException("head() called on empty list");
			}

			public List<T> tail() {
				throw new IllegalStateException("tail() called on empty list");
			}

			public boolean isEmpty() {
				return true;
			}

			@Override
			public String toString() {
				return "[NIL]";
			}
		}

		private static class Cons<T> extends List<T> {
			private Cons(T head, List<T> tail) {
				this.head = head;
				this.tail = tail;
			}

			public T head() {
				return head;
			}

			public List<T> tail() {
				return tail;
			}

			public boolean isEmpty() {
				return false;
			}

			private T head;
			private List<T> tail;

			@Override
			public String toString() {
				return toString("", this).eval();
			}

			private static <T> TailCall<String> toString(String accumulator, List<T> list) {
				if (list.isEmpty())
					return TailCall.ret("[" + accumulator + " NIL]");
				else
					return TailCall.sus(() -> toString(accumulator + list.head() + ", ", list.tail()));
			}
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
}