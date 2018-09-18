package chapter04;

import java.util.function.Supplier;

public class exercise02 {
	public static void main(String... args) {
		TailCall<Integer> tc = fibonacci(30, 1, 1);
		while (tc.isSuspended()) {
			tc = tc.resume();
		}
		System.out.println(tc.eval());
	}

	public static TailCall<Integer> fibonacci(int number, int accumulatorA, int accumulatorB) {
		if (number == 0)
			return TailCall.ret(accumulatorB);
		else if (number == 1)
			return TailCall.ret(accumulatorA);
		else
			return TailCall.sus(() -> fibonacci(number - 1, accumulatorA + accumulatorB, accumulatorA));
	}
}

interface TailCall<T> {
	boolean isSuspended();
	T eval();
	TailCall<T> resume();

	static <T> Return<T> ret(T s) { return new Return<>(s);}
	static <T> Suspend<T> sus(Supplier<TailCall<T>> s) { return new Suspend<>(s);}
}

class Return<T> implements TailCall<T> {
	Return(T value) {this.value = value;}
	public boolean isSuspended() {return false;}
	public T eval() {return value;}
	public TailCall<T> resume() {throw new IllegalStateException("Return has no resume");}

	private T value;
}

class Suspend<T> implements TailCall<T> {
	public Suspend(Supplier<TailCall<T>> supplier) {this.supplier = supplier;}
	public boolean isSuspended() {return true;}
	public T eval() {
		TailCall<T> tc = supplier.get();
		while (tc.isSuspended()) {
			tc = tc.resume();
		}
		return tc.eval();
	}

	public TailCall<T> resume() {
		return supplier.get();
	}

	private Supplier<TailCall<T>> supplier;
}