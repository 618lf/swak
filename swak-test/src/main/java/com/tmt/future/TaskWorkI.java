package com.tmt.future;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class TaskWorkI {

	/**
	 * 长时间的任务
	 * 
	 * @param i
	 * @return
	 */
	public static Optional<List<Integer>> longTask(Integer i) {
		if (i > 0) {
			List<Integer> list = new ArrayList<>();
			for (int pc = 0; pc < i; pc++)
				list.add(pc);
			return Optional.of(list);
		} else
			return Optional.empty();
	}

	public static CompletableFuture<Long> getResultFuture(Optional<List<Integer>> op) {
		return CompletableFuture.supplyAsync(() -> {
			if (op.isPresent())
				return op.get().stream() // Stream<Integer>
						.map(Integer::toUnsignedLong) // Stream<Long>
						.reduce(0L, (x, y) -> x + y); //
			else
				return -1L;
		});
	}
}