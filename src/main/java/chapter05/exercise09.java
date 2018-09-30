package chapter05;

import java.util.function.Function;

public class exercise09 {
	public static void main(String... args) {
		List<Double> list = List.list(1D, 2D, 3D, 4D);
		System.out.println(foldRight(list, 1D, x -> y -> x * y));
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
	}

	static <U, V> V foldRight(List<U> list, V identity, Function<U, Function<V, V>> folding) {
		return list.isEmpty()
				? identity
				: folding.apply(list.head()).apply(foldRight(list.tail(), identity, folding));
	}
}