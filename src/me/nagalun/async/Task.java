package me.nagalun.async;

/**
 * @author nagalun
 * @date 09-08-2017
 */
public abstract class Task implements Comparable<Task> {
	private final int id;
	private final Runnable task;
	
	public Task(final int id, final Runnable task) {
		this.id = id;
		this.task = task;
	}
	
	public void run(final long time) {
		task.run();
	}
	
	public final int getId() {
		return id;
	}
	
	@Override
	public int compareTo(Task o) {
		long diff = getExecutionTime() - o.getExecutionTime();
		return diff < 0 ? -1 : diff == 0 ? 0 : 1;
	}

	public abstract long getExecutionTime();
	
	public abstract boolean shouldCancel();
}
