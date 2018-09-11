package chapter02;

import java.util.function.Function;

public class exercise10 {
	public static void main(String[] args) {
		Function<Tuple<Integer,Character>, String> f = tuple -> tuple.a.toString() + tuple.b.toString();
		Function<Integer, Function<Character, String>> curried = curry(f);
		System.out.println(curried.apply(1).apply('b')); // expected: "1b"
	}

	static <A, B, C> Function<A, Function<B,C>> curry(Function<Tuple<A,B>, C> f) {
		return a -> b -> f.apply(new Tuple<A,B>(a, b));
	}
}

class Tuple<U, V> {
	Tuple(U a, V b) {this.a = a; this.b = b;}
	public U a;
	public V b;
}