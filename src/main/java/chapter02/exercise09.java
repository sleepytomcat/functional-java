package chapter02;

import java.util.function.Function;

public class exercise09 {
	public static void main(String[] args) {
		Function<String, Function<String, Function<String, Function<String, String>>>>
				func = a -> b -> c -> d -> String.format("%s, %s, %s, %s", a, b, c, d);

		System.out.println(method("a", "b", "c", "d"));
		System.out.println(func.apply("a").apply("b").apply("c").apply("d"));
	}

	static <A, B, C, D> String method(A a, B b, C c, D d) {
		return String.format("%s, %s, %s, %s", a, b, c, d);
	}
}


