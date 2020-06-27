package corewars.jmars;

import java.util.HashMap;

public class LogWriter {

	public void logRound(int roundNum, double roundTime, int cycleNum) {
		System.out.println(roundNum + 1 + ". Round time=" + roundTime + " Cycles=" + cycleNum + " avg. time/cycle="
				+ (roundTime / cycleNum));
	}

	public void logStat(double totalTime, int totalCycles, HashMap<String, Integer> statistic) {
		System.out.println("Total time=" + totalTime + " Total Cycles=" + totalCycles + " avg. time/cycle="
				+ (totalTime / totalCycles));
		System.out.println("Survivor in how many rounds:");
		for (String name : statistic.keySet()) {
			System.out.println("  " + name + ": " + statistic.get(name));
		}
	}

}
