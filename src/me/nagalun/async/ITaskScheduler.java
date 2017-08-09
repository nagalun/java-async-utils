package me.nagalun.async;

/**
 * @author nagalun
 * @date 09-08-2017
 */
public interface ITaskScheduler {
	public int setTimeout(final Runnable task, final int delay);
	public int setInterval(final Runnable task, final int delay);
	public boolean clear(final int id);
}
