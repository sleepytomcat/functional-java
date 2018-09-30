package chapter05;

public class exercise06 {
	public static void main(String... args) {
		List<Integer> list = List.list(1, 2, 3);
		System.out.println(list.reverse());
		System.out.println(list.dropLast());
	}

	static abstract class List<T> {
		public abstract T head();
		public abstract List<T> tail();
		public abstract boolean isEmpty();
		public abstract List<T> setHead(T newHead);
		public abstract List<T> dropLast();
		public abstract List<T> reverse();

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

			public List<T> setHead(T newHead) {
				throw new IllegalStateException("setHead() called on empty list");
			}

			@Override
			public String toString() {
				return "[NIL]";
			}

			public List<T> dropLast() {
				throw new IllegalStateException("dropLast() called on empty list");
			}

			public List<T> reverse() {
				return reverse(list(), this).eval();
			}

			private static <T> TailCall<List<T>> reverse(List<T> accumulator, List<T> list) {
				if (list.isEmpty())
					return TailCall.ret(list);
				else
					return TailCall.sus(() -> reverse(new Cons<>(list.head(), accumulator), list.tail()));
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

			public List<T> dropLast() {
				return this.reverse().tail().reverse();
			}

			public List<T> reverse() {
				return reverse(list(), this).eval();
			}

			private static <T> TailCall<List<T>> reverse(List<T> accumulator, List<T> list) {
				if (list.isEmpty())
					return TailCall.ret(accumulator);
				else
					return TailCall.sus(() -> reverse(new Cons<>(list.head(), accumulator), list.tail()));
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