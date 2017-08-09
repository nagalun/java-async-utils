package me.nagalun.async;

/**
 * @author nagalun
 * @date 09-08-2017
 */
public class TimedTask extends Task {
	private long lastRan = System.currentTimeMillis();
	private int delay;
	
	public TimedTask(final Runnable task, final int delay, final int id) {
		super(id, task);
		this.delay = delay;
	}
	
	@Override
	public void run(final long time) {
		super.run(time);
		lastRan = time;
	}

	@Override
	public long getExecutionTime() {
		return lastRan + delay;
	}

	@Override
	public boolean shouldCancel() {
		return false;
	}
}
