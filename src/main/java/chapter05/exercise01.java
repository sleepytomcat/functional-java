package chapter05;

public class exercise01 {
	public static void main(String... args) {
		List<Integer> ex1 = List.list();
		List<Integer> ex2 = List.list(1);
		List<Integer> ex3 = List.list(1, 2);
		List<Integer> prepended = List.cons(123, ex3);
	}

	static abstract class List<T> {
		public abstract T head();
		public abstract List<T> tail();
		public abstract boolean isEmpty();
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
	}
}