package corewars.jmars;

public class jMarsMain {

	public static void main(String[] args) {
		String red1 = "war\\dwarf2.red";
		String red2 = "war\\imp.red";
		
		jMarsStarter jms = new jMarsStarter();
		jms.runJMarsConsole(red1, red2);
//		jms.runJMarsGui(red1, red2);
	}

}
