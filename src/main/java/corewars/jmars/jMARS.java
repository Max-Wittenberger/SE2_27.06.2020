/*-
 * Copyright (c) 1998 Brian Haskin jr.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */
package corewars.jmars;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.util.List;

import corewars.jmars.marsVM.*;
import corewars.jmars.frontend.*;
import corewars.jmars.assembler.*;

/**
 * jMARS is a corewars interpreter in which programs (warriors) battle in the
 * memory of a virtual machine (the MARS) and try to disable the other program.
 */
public class jMARS implements Runnable {

	// constants
	private static final int numDefinedColors = 4;
	private static final Color wColors[][] = { { Color.green, Color.yellow }, { Color.red, Color.magenta },
			{ Color.cyan, Color.blue }, { Color.gray, Color.darkGray } };

	// Application specific variables
	private Frame myFrame;
	private jMarsPanel panel;

	// Common variables
	private boolean useGui = false;
	private int maxProc = 8000;
	private int pSpaceSize;
	private int coreSize = 8000;
	private int cycles = 80000;
	private int rounds = 10;
	private int maxWarriorLength = 100;
	private int minWarriorDistance = 100;
	private int numWarriors;
	private int minWarriors;

	private WarriorObj allWarriors[];
	private WarriorObj warriors[];
	private VM MARS;

	private int runWarriors;
	
	private LogWriter logWriter;

	public jMARS(boolean gui, List<String> warriors) throws jMarsException {
		this(gui, 0, 0, 0, 0, 0, 0, 0, warriors);
	}

	public jMARS(boolean gui, int rounds, int coreSize, int cycles, int maxProc, int maxWarriorLength,
			int minWarriorDistance, int pSpaceSize, List<String> warriors) throws jMarsException {
		useGui = gui;

		if (warriors == null || warriors.size() == 0) {
			throw new jMarsException("ERROR: no warrior files specified");
		}

		numWarriors = warriors.size();

		if (rounds != 0) {
			this.rounds = rounds;
		}
		if (coreSize != 0) {
			this.coreSize = coreSize;
		}
		if (cycles != 0) {
			this.cycles = cycles;
		}
		if (maxProc != 0) {
			this.maxProc = maxProc;
		}
		if (maxWarriorLength != 0) {
			this.maxWarriorLength = maxWarriorLength;
		}
		if (minWarriorDistance != 0) {
			this.minWarriorDistance = minWarriorDistance;
		}
		if (pSpaceSize != 0) {
			this.pSpaceSize = pSpaceSize;
		} else {
			this.pSpaceSize = this.coreSize / 16;
		}

		applicationInit(warriors);
		logWriter = new LogWriter();
	}

	/**
	 * Initialization function for the application.
	 * 
	 * @param warriors2
	 * @throws jMarsException 
	 */
	void applicationInit(List<String> warriorsP) throws jMarsException {

		createWarriors(warriorsP);
		if (useGui) {
			myFrame = new Frame("jMARS");
			myFrame.setSize(new Dimension(1200, 900));

			panel = new jMarsPanel();
			myFrame.add(panel);
			myFrame.addWindowListener(panel);
			myFrame.setVisible(true);

			panel.addCoreDisplay(coreSize);
			panel.addRoundCycleCounter();
		}
	}

	private void createWarriors(List<String> warriorsP) throws jMarsException {
		Assembler parser = new corewars.jmars.assembler.icws94p.ICWS94p();
		parser.addConstant("coresize", Integer.toString(coreSize));
		parser.addConstant("maxprocesses", Integer.toString(maxProc));
		parser.addConstant("maxcycles", Integer.toString(cycles));
		parser.addConstant("maxlength", Integer.toString(maxWarriorLength));
		parser.addConstant("mindistance", Integer.toString(minWarriorDistance));
		parser.addConstant("rounds", Integer.toString(rounds));
		parser.addConstant("pspacesize", Integer.toString(pSpaceSize));
		parser.addConstant("warriors", Integer.toString(numWarriors));
		allWarriors = new WarriorObj[numWarriors];

		for (int i = 0; i < warriorsP.size(); i++) {
			try {
				FileInputStream wFile = new FileInputStream(new File(warriorsP.get(i)));
				try {
					parser.parseWarrior(wFile);
					if (parser.length() > maxWarriorLength) {
						throw new jMarsException("Error: warrior " + warriorsP.get(i) + " to large");
					}
					allWarriors[i] = new WarriorObj(parser.getWarrior(), parser.getStart(),
							wColors[i % numDefinedColors][0], wColors[i % numDefinedColors][1]);
					allWarriors[i].setName(parser.getName());
					allWarriors[i].setAuthor(parser.getAuthor());
					allWarriors[i].Alive = true;
					allWarriors[i].initPSpace(pSpaceSize);
					allWarriors[i].setPCell(0, -1);
				} catch (AssemblerException ae) {
					throw new jMarsException("Error parsing warrior file " + warriorsP.get(i), ae);
				} catch (IOException ioe) {
					throw new jMarsException("IO error while parsing warrior file " + warriorsP.get(i), ioe);
				}
			} catch (FileNotFoundException e) {
				throw new jMarsException("Could not find warrior file " + warriorsP.get(i), e);
			}
		}
	}

	public void runApp() throws jMarsException {
		if (useGui) {
			panel.validate();
			panel.repaint();
			panel.update(panel.getGraphics());
		}
		MARS = new MarsVM(coreSize, maxProc);
		loadWarriors();
		minWarriors = (numWarriors == 1) ? 0 : 1;
		Thread myThread = new Thread(this);
		myThread.setPriority(Thread.NORM_PRIORITY - 1);
		myThread.start();
	}

	/**
	 * main function and loop for jMARS. Runs the battles and handles display.
	 */
	public void run() {
		HashMap<String, Integer> statistic = new HashMap<>();
		Date startTime;
		Date endTime;
		double roundTime;
		Date tStartTime;
		Date tEndTime;
		double totalTime;
		int totalCycles = 0;
		tStartTime = new Date();
		startTime = new Date();
		clearCoreDisplay();
		for (int roundNum = 0; roundNum < rounds; roundNum++) {
			int cycleNum = 0;
			clearCoreDisplay();
			for (; cycleNum < cycles; cycleNum++) {
				for (int warRun = 0; warRun < runWarriors; warRun++) {

					StepReport stats = MARS.step();
					stats.warrior.numProc = stats.numProc;
					if (stats.wDeath) {
						stats.warrior.Alive = false;
						runWarriors--;
						ArrayList<WarriorObj> tmp = new ArrayList<>();
						for (int warIdx = 0; warIdx < warriors.length; warIdx++) {
							if (warIdx != warRun) {
								tmp.add(warriors[warIdx]);
							}
						}
						warriors = tmp.toArray(new WarriorObj[] {});
						break;
					}
					notifyStepListener(stats);
				}
				notifyCycleListeners(cycleNum);
				repaint();

				if (runWarriors <= minWarriors) {
					break;
				}
			}
			for (int warIdx = 0; warIdx < warriors.length; warIdx++) {
				String name = warriors[warIdx].getName();
				Integer count = statistic.getOrDefault(name, Integer.valueOf(0));
				count++;
				statistic.put(name, count);
			}
			notifyRoundListener(roundNum);
			endTime = new Date();
			roundTime = ((double) endTime.getTime() - (double) startTime.getTime()) / 1000;
			logWriter.logRound(roundNum, roundTime, cycleNum);
			startTime = new Date();
			totalCycles += cycleNum;
			MARS.reset();
			try {
				loadWarriors();
			} catch (jMarsException e) {
				e.printMessage();
				break;
			}
		}
		tEndTime = new Date();
		totalTime = ((double) tEndTime.getTime() - (double) tStartTime.getTime()) / 1000;
		logWriter.logStat(totalTime, totalCycles, statistic);
	}

	private void notifyRoundListener(int roundNum) {
		if (useGui) {
			panel.notifyRoundListeners(roundNum);
		}
	}

	private void repaint() {
		if (useGui) {
			panel.repaint();
		}
	}

	private void notifyStepListener(StepReport stats) {
		if (useGui) {
			panel.notifyStepListeners(stats);
		}
	}

	private void clearCoreDisplay() {
		if (useGui) {
			panel.getCoreDisplay().clear();
		}
	}

	private void notifyCycleListeners(int cycleNum) {
		if (useGui) {
			panel.notifyCycleListeners(cycleNum);
		}
	}

	/**
	 * Load warriors into core
	 * @throws jMarsException 
	 */
	void loadWarriors() throws jMarsException {
		warriors = new WarriorObj[allWarriors.length];
		System.arraycopy(allWarriors, 0, warriors, 0, allWarriors.length);
		runWarriors = numWarriors;
		int[] location = new int[warriors.length];

		if (!MARS.loadWarrior(warriors[0], 0)) {
			throw new jMarsException("ERROR: could not load warrior 1.");
		}

		for (int i = 1, r = 0; i < numWarriors; i++) {
			boolean validSpot;
			do {
				validSpot = true;
				r = (int) (Math.random() * coreSize);

				if (r < minWarriorDistance || r > (coreSize - minWarriorDistance)) {
					validSpot = false;
				}

				for (int j = 0; j < location.length; j++) {
					if (r < (minWarriorDistance + location[j]) && r > (minWarriorDistance + location[j])) {
						validSpot = false;
					}
				}
			} while (!validSpot);

			if (!MARS.loadWarrior(warriors[i], r)) {
				throw new jMarsException("ERROR: could not load warrior " + (i + 1) + ".");
			}
		}
	}
}
