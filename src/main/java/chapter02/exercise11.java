package chapter02;

import java.util.function.Function;

public class exercise11 {
	public static void main(String[] args) {
		Function<Character, Function<Integer, String>> f = x -> y -> x.toString() + y.toString();
		Function<Integer, Function<Character, String>> reversed_f = reverseCurriedArgs(f);

		System.out.println(f.apply('b').apply(10));
		System.out.println(reversed_f.apply(10).apply('b'));
	}

	static <A,B,C> Function<B, Function<A,C>> reverseCurriedArgs(Function<A, Function<B,C>> f) {
		return a -> b -> f.apply(b).apply(a);
	}
}


