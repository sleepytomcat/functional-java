package chapter05;

public class exercise02 {
	public static void main(String... args) {
		List<Integer> list = List.list(1, 2);
		List<Integer> replaced = List.setHead(123, list);
	}

	static abstract class List<T> {
		public abstract T head();
		public abstract List<T> tail();
		public abstract boolean isEmpty();
		public abstract List<T> setHead(T newHead);
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

		static <T> List<T> cons(T element, List<T> list) {
			return new Cons<>(element, list);
		}
		static <T> List<T> setHead(T newHead, List<T> list) {
			return list.setHead(newHead);
		}
	}
}
