package chapter08;

import java.util.function.Supplier;

public interface TailCall<T> {
	boolean isSuspended();

	T eval();

	TailCall<T> resume();

	static <T> Return<T> ret(T s) {
		return new Return<>(s);
	}

	static <T> Suspend<T> sus(Supplier<TailCall<T>> s) {
		return new Suspend<>(s);
	}

	class Return<T> implements TailCall<T> {
		Return(T value) {
		this.value = value;
	}

		public boolean isSuspended() {
		return false;
	}

		public T eval() {
		return value;
	}

		public TailCall<T> resume() {
		throw new IllegalStateException("Return has no resume");
	}

		private T value;
	}

	class Suspend<T> implements TailCall<T> {
		public Suspend(Supplier<TailCall<T>> supplier) {
		this.supplier = supplier;
	}

		public boolean isSuspended() {
		return true;
	}

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
}