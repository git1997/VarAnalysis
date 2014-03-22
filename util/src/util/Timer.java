package util;

/**
 * 
 * @author HUNG
 *
 */
public class Timer {
	
	public static Timer inst = new Timer();
	
	private long startTime = System.currentTimeMillis();
	
	public void reset() {
		startTime = System.currentTimeMillis();
	}
	
	public float getCurrentSeconds() {
		return (float) System.currentTimeMillis() / 1000;
	}
	
	public String getCurrentSecondsInText() {
		return String.format("%.3f second(s)", getCurrentSeconds());
	}
	
	public float getElapsedSeconds() {
		return (float) (System.currentTimeMillis() - startTime) / 1000;
	}
	
	public String getElapsedSecondsInText() {
		return String.format("%.3f second(s)", getElapsedSeconds());
	}
	
}
