package chapter05;

import java.util.function.Function;

public class exercise05 {
	public static void main(String... args) {
		List<Integer> list = List.list(1, 2, 3, 5);

		System.out.println(list.dropWhile(x -> x < 5));
	}

	public static <T> List<T> drop(int toDrop, List<T> list) {
		return drop_(toDrop, list).eval();
	}

	private static <T> TailCall<List<T>> drop_(int toDrop, List<T> list) {
		if (toDrop == 0)
			return TailCall.ret(list);
		else
			return TailCall.sus(() -> drop_(toDrop -1, list.tail()));
	}


	static abstract class List<T> {
		public abstract T head();
		public abstract List<T> tail();
		public abstract boolean isEmpty();
		public abstract List<T> setHead(T newHead);
		public abstract List<T> dropWhile(Function<T, Boolean> predicate);
		public static final List NIL = new Nil();
		private List() {}
		private static class Nil<T> extends List<T> {
			public T head() {
				throw new IllegalStateException("head called on empty list");
			}

			public List<T> tail() {
				throw new IllegalStateException("tail called on empty list");
			}

			public boolean isEmpty() {
				return true;
			}

			public List<T> setHead(T newHead) {
				throw new IllegalStateException("setHead called on empty list");
			}

			@Override
			public String toString() {
				return "[NIL]";
			}

			public List<T> dropWhile(Function<T, Boolean> predicate) {
				return this;
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

			public List<T> setHead(T newHead) {
				return new Cons<>(newHead, tail());
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

			public List<T> dropWhile(Function<T, Boolean> predicate) {
				return dropWhile(predicate, this).eval();
			}

			private static <T> TailCall<List<T>> dropWhile(Function<T, Boolean> predicate, List<T> list) {
				if (list.isEmpty())
					return TailCall.ret(list);
				else if (predicate.apply(list.head()))
					return TailCall.sus(() -> dropWhile(predicate, list.tail()));
				else
					return TailCall.ret(list);
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
}
