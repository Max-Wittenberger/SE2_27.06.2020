package corewars.jmars;

public class jMarsStarter {

	public void runJMarsGui(String red1, String red2) {
		jMARS jm = new jMARS(true, new String[] { red1, red2 });
		jm.runApp();
	}

	public void runJMarsConsole(String red1, String red2) {
		jMARS jm = new jMARS(false, new String[] { red1, red2 });
		jm.runApp();
	}
}
