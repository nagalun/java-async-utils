package me.nagalun.async;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author nagalun
 * @date 09-08-2017
 */
public class MultiSelector implements ITaskScheduler {
	private final Selector selector;
	private final PriorityBlockingQueue<Task> tasks = new PriorityBlockingQueue<>();
	private final AtomicInteger nextId = new AtomicInteger(0);

	public MultiSelector() throws IOException {
		this.selector = Selector.open();
	}

	private long executeTasks(final long time) {
		Task t = tasks.peek();
		long nextTaskDelay = t.getExecutionTime() - time;
		if (nextTaskDelay <= 0) {
			do {
				if (nextTaskDelay <= 0) {
					t.run(time);
				}
				if (t.shouldCancel()) {
					tasks.remove();
				} else {
					/* re-sort the item (maybe not very efficient...) */
					tasks.remove();
					tasks.offer(t);
				}
				t = tasks.peek();
				nextTaskDelay = t.getExecutionTime() - time;
			} while (t != null && nextTaskDelay <= 0);
		}
		return nextTaskDelay;
	}

	private long getNextTaskDelay(final long time) {
		/* I'm gonna be mad if this returns null */
		return tasks.isEmpty() ? 0 : Math.max(tasks.peek().getExecutionTime() - time, 1);
	}

	public int select() throws IOException {
		long time = System.currentTimeMillis();
		long delay = getNextTaskDelay(time);
		int selected;
		do {
			/*
			 * NOTE: .select(delay) can wait less than the specified delay before returning
			 * (even without any selector events)
			 */
			selected = selector.select(delay);
			time = System.currentTimeMillis();
			if (!tasks.isEmpty()) {
				delay = executeTasks(time);
			} else {
				delay = 0; /* .select() will not time out */
			}
		} while (selected == 0);
		return selected;
	}

	public void wakeup() {
		selector.wakeup();
	}

	public int setTimeout(final Runnable task, final int delay) {
		final int id = nextId.incrementAndGet();
		tasks.offer(new DelayedTask(task, delay, id));
		selector.wakeup();
		return id;
	}

	public int setInterval(final Runnable task, final int delay) {
		final int id = nextId.incrementAndGet();
		tasks.offer(new TimedTask(task, delay, id));
		selector.wakeup();
		return id;
	}

	public boolean clear(final int id) {
		/* NOTE: does not break on first match */
		return tasks.removeIf(obj -> obj.getId() == id);
	}
	
	public void close() throws IOException {
		tasks.clear();
		selector.close();
	}

	public Selector getSelector() {
		return selector;
	}
}
