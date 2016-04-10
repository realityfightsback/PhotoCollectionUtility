package util;

public class StopWatch {

	private long startTime = -1;
	private long endTime = -1;

	/**
	 * Time starts on instantiation
	 */
	public StopWatch() {
		startTime = System.currentTimeMillis();
	}

	public void reset() {
		startTime = System.currentTimeMillis();
		endTime = -1;

	}

	public void stop() {
		System.currentTimeMillis();
	}

	public long getDuration() {
		if (endTime == -1) {
			return System.currentTimeMillis() - startTime;

		}
		return endTime - startTime;
	}

}
