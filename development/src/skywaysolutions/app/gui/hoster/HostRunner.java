package skywaysolutions.app.gui.hoster;

import skywaysolutions.app.gui.control.StatusBar;
import skywaysolutions.app.utils.CheckedException;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This class provides a way to do background threading while running against swing.
 *
 * @author Alfred Manville
 */
public final class HostRunner implements Runnable {

    private boolean executing = false;
    private boolean executed = false;
    private final Thread runner;
    private IHostInvokable invoker;
    private StatusBar statusBar;
    private final Queue<EventStruct> eventQueue = new LinkedList<>();
    private final Object slock = new Object();

    /**
     * Constructs a new instance of HostRunner with the specified invoker and status bar.
     *
     * @param invoker The invoker to use.
     * @param statusBar The status bar to use (Can be null).
     * @throws IllegalArgumentException invoker is null.
     */
    public HostRunner(IHostInvokable invoker, StatusBar statusBar) {
        if (invoker == null) throw new IllegalArgumentException("invoker is null");
        this.invoker = invoker;
        this.statusBar = statusBar;
        runner = new Thread(this);
        runner.setDaemon(true);
    }

    /**
     * Starts the runner.
     */
    public void start() {
        if (executing) return;
        synchronized (slock) {
            if (executed) return;
            executing = true;
            runner.start();
        }
    }

    /**
     * Stops the runner.
     */
    public void stop() {
        synchronized (slock) {
            if (executed) return;
            executing = false;
            slock.notifyAll();
        }
    }

    /**
     * Enqueues an event.
     *
     * @param id The ID of the event.
     * @param args The arguments of the event.
     */
    public void addEvent(String id, Object[] args) {
        if (id == null) id = "";
        if (args == null) args = new Object[0];
        synchronized (slock) {
            if (executing) eventQueue.add(new EventStruct(id, args));
            slock.notifyAll();
        }
    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        while (executing) {
            if (statusBar != null) {
                try {
                    SwingUtilities.invokeAndWait(() -> statusBar.setStatus("Please Wait...", "", 0));
                } catch (InterruptedException | InvocationTargetException e) {
                }
            }
            while (!eventQueue.isEmpty()) {
                try {
                    EventStruct es = eventQueue.poll();
                    if(es != null) invoker.invoke(es.id, es.args);
                } catch (CheckedException e) {
                    if (statusBar != null) SwingUtilities.invokeLater(() -> statusBar.setStatus(e, 2500));
                }
            }
            if (statusBar != null) {
                try {
                    SwingUtilities.invokeAndWait(() -> statusBar.clearStatus("Please Wait..."));
                } catch (InterruptedException | InvocationTargetException e) {
                }
            }
            synchronized (slock) {
                if (executing) {
                    try {
                        slock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        executed = true;
    }

    /**
     * This class provides the internal event struct for storing in a queue.
     *
     * @author Alfred Manville
     */
    private static class EventStruct {
        public String id;
        public Object[] args;

        /**
         * Constructs a new event struct instance with the specified id and arguments.
         *
         * @param id The id of the command.
         * @param args The arguments of the command.
         */
        public EventStruct(String id, Object[] args) {
            this.id = id;
            this.args = args;
        }
    }
}
