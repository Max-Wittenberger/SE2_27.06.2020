package corewars.jmars;

import java.util.ArrayList;

public class jMarsStarter {

	public static void main(String[] args) {
		boolean useGui = false;
		int rounds = 0, coreSize = 0, cycles = 0, maxProc = 0, maxWarriorLength = 0, minWarriorDistance = 0,
				pSpaceSize = 0;
		ArrayList<String> warriors = new ArrayList<String>();
		jMARS jm = null;

		if (args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].charAt(0) == '-') {
					if (args[i].equals("-g")) {
						useGui = true;
					} else if (args[i].equals("-r")) {
						rounds = Integer.parseInt(args[++i]);
					} else if (args[i].equals("-s")) {
						coreSize = Integer.parseInt(args[++i]);
					} else if (args[i].equals("-c")) {
						cycles = Integer.parseInt(args[++i]);
					} else if (args[i].equals("-p")) {
						maxProc = Integer.parseInt(args[++i]);
					} else if (args[i].equals("-l")) {
						maxWarriorLength = Integer.parseInt(args[++i]);
					} else if (args[i].equals("-d")) {
						minWarriorDistance = Integer.parseInt(args[++i]);
					} else if (args[i].equals("-S")) {
						pSpaceSize = Integer.parseInt(args[++i]);
					}
				} else {
					warriors.add(args[i]);
				}
			}
			jm = new jMARS(useGui, rounds, coreSize, cycles, maxProc, maxWarriorLength, minWarriorDistance, pSpaceSize,
					warriors);
		} else {
			warriors.add("war\\dwarf2.red");
			warriors.add("war\\imp.red");

			jm = new jMARS(true, warriors);
		}

		jm.runApp();
	}

	public void runJMarsGui(String red1, String red2) {
		ArrayList<String> warriors = new ArrayList<String>();
		warriors.add(red1);
		warriors.add(red2);
		jMARS jm = new jMARS(true, warriors);
		jm.runApp();
	}

	public void runJMarsConsole(String red1, String red2) {
		ArrayList<String> warriors = new ArrayList<String>();
		warriors.add(red1);
		warriors.add(red2);
		jMARS jm = new jMARS(false, warriors);
		jm.runApp();
	}
}
