package chapter02;

import java.util.function.Function;

public class exercise03 {
	public static void main(String[] args) {
		Function<Integer, Function<Integer, Integer>> sum = x -> y -> x + 4;

		System.out.println(sum.apply(3).apply(4)); // expected: 7
	}
}
