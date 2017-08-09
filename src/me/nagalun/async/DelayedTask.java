package me.nagalun.async;

/**
 * @author nagalun
 * @date 09-08-2017
 */
public class DelayedTask extends Task  {
	public final long runsOn;

	public DelayedTask(final Runnable task, final int delay, final int id) {
		super(id, task);
		this.runsOn = System.currentTimeMillis() + delay;
	}
	
	@Override
	public long getExecutionTime() {
		return runsOn;
	}

	@Override
	public boolean shouldCancel() {
		return true;
	}
}
