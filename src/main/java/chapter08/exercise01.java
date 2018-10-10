package chapter08;

import java.util.function.Function;

public class exercise01 {
	public static void main(String... args) {
		List<Integer> list = List.list(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
		System.out.println(list.lengthMemoized());
	}

	static abstract class List<T> {
		public abstract T head();
		public abstract List<T> tail();
		public abstract boolean isEmpty();
		public abstract long length();
		public abstract long lengthMemoized();

		public static final List NIL = new Nil();
		public List<T> cons(T head) {return new Cons<>(head, this);}
		private List() {}

		private static class Nil<T> extends List<T> {
			@Override public T head() {throw new IllegalStateException("head() called on empty list");}
			@Override public List<T> tail() {throw new IllegalStateException("tail() called on empty list");}
			@Override public boolean isEmpty() {return true;}
			@Override public String toString() {return "[NIL]";}
			@Override public long length() {return 0;}
			@Override public long lengthMemoized() {return 0;}
		}

		private static class Cons<T> extends List<T> {
			private Cons(T head, List<T> tail) {
				this.head = head;
				this.tail = tail;
				lengthMemoized = tail.isEmpty() ? 1: tail.length() + 1;
			}

			@Override public T head() {return head;}
			@Override public List<T> tail() {return tail;}
			@Override public boolean isEmpty() {return false;}
			@Override public String toString() {return toString("", this).eval();}
			@Override public long length() {return foldRight(this, 0, x -> y -> y + 1);}
			@Override public long lengthMemoized() {return lengthMemoized;}

			private static <T> TailCall<String> toString(String accumulator, List<T> list) {
				if (list.isEmpty())
					return TailCall.ret("[" + accumulator + " NIL]");
				else
					return TailCall.sus(() -> toString(accumulator + list.head() + ", ", list.tail()));
			}

			private T head;
			private List<T> tail;
			private long lengthMemoized;
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

	public static <T> List<T> concat(List<T> first, List<T> second) {
		return foldRight(first, second, secondHead -> accumulatedFirst -> new List.Cons<>(secondHead, accumulatedFirst));
	}

	public static <T> List<T> flatten(List<List<T>> listOfLists) {
		return foldLeft(listOfLists, List.list(), x -> y -> concat(y, x));
	}

	public static <U,V> List<V> map(List<U> list, Function<U,V> mapper) {
		return foldRight(list, List.list(), x -> y -> new List.Cons<>(mapper.apply(x), y));
	}

	public static <T> List<T> filter(List<T> list, Function<T,Boolean> predicate) {
		return flatMap(list, x -> predicate.apply(x) ? List.list(x) : List.list());
	}

	public static <U,V> List<V> flatMap(List<U> list, Function<U,List<V>> mapper) {
		return foldRight(list, List.list(), x -> y -> concat(mapper.apply(x), y));
	}
}