package corewars.jmars;

import java.awt.Graphics;
import java.awt.Panel;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Enumeration;
import java.util.Vector;

import corewars.jmars.frontend.CoreDisplay;
import corewars.jmars.frontend.CycleListener;
import corewars.jmars.frontend.FrontEndManager;
import corewars.jmars.frontend.RoundCycleCounter;
import corewars.jmars.frontend.RoundListener;
import corewars.jmars.frontend.StepListener;
import corewars.jmars.frontend.StepReport;

public class jMarsPanel extends Panel implements WindowListener, FrontEndManager {

	private Vector<StepListener> stepListeners;
	private Vector<CycleListener> cycleListeners;
	private Vector<RoundListener> roundListeners;
	private CoreDisplay coreDisplay;

	private static final long serialVersionUID = 2108930704649381781L;
	
	public jMarsPanel() {
		stepListeners = new Vector<StepListener>();
		cycleListeners = new Vector<CycleListener>();
		roundListeners = new Vector<RoundListener>();
	}
	
	public void addRoundCycleCounter() {
		new RoundCycleCounter(this);
	}
	
	public void addCoreDisplay(int coreSize) {
		setCoreDisplay(new CoreDisplay(this, coreSize, 100));
	}

	public CoreDisplay getCoreDisplay() {
		return coreDisplay;
	}

	public void setCoreDisplay(CoreDisplay coreDisplay) {
		this.coreDisplay = coreDisplay;
	}

	/**
	 * update the display
	 *
	 * @param java.awt.Graphics g - Graphics context
	 */
	public void update(Graphics g) {
		paintComponents(g);
		return;
	}

	/**
	 * register an object to receive step results.
	 *
	 * @param StepListener - object to register
	 */
	public void registerStepListener(StepListener l) {
		stepListeners.addElement(l);
	}

	/**
	 * register an object to receive cycle results.
	 *
	 * @param CycleListener - object to register
	 */
	public void registerCycleListener(CycleListener c) {
		cycleListeners.addElement(c);
	}

	/**
	 * register an object to receive round results.
	 *
	 * @param RoundListener - object to register
	 */
	public void registerRoundListener(RoundListener r) {
		roundListeners.addElement(r);
	}

	protected void notifyStepListeners(StepReport step) {
		for (Enumeration e = stepListeners.elements(); e.hasMoreElements();) {
			StepListener j = (StepListener) e.nextElement();
			j.stepProcess(step);
		}
	}

	protected void notifyCycleListeners(int cycle) {
		for (Enumeration e = cycleListeners.elements(); e.hasMoreElements();) {
			CycleListener j = (CycleListener) e.nextElement();
			j.cycleFinished(cycle);
		}
	}

	protected void notifyRoundListeners(int round) {
		for (Enumeration e = roundListeners.elements(); e.hasMoreElements();) {
			RoundListener j = (RoundListener) e.nextElement();
			j.roundResults(round);
		}
	}

	/**
	 * Invoked when a window is in the process of being closed. The close operation
	 * can be overridden at this point.
	 */
	public void windowClosing(WindowEvent e) {
		System.exit(0);
	}

	/**
	 * Invoked when a window has been opened.
	 */
	public void windowOpened(WindowEvent e) {

	}

	/**
	 * Invoked when a window has been closed.
	 */
	public void windowClosed(WindowEvent e) {

	}

	/**
	 * Invoked when a window is iconified.
	 */
	public void windowIconified(WindowEvent e) {

	}

	/**
	 * Invoked when a window is de-iconified.
	 */
	public void windowDeiconified(WindowEvent e) {

	}

	/**
	 * Invoked when a window is activated.
	 */
	public void windowActivated(WindowEvent e) {

	}

	/**
	 * Invoked when a window is de-activated.
	 */
	public void windowDeactivated(WindowEvent e) {

	}
}
