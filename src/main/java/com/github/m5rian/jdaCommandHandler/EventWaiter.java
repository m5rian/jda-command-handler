package com.github.m5rian.jdaCommandHandler;

import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * @author Marian
 * <p>
 * With this class you're able to wait for a specific {@link GenericEvent}.
 * Once this event is fired a callback will run too.
 * Important settings can be made in the {@link Settings} class.
 * The {@link EventWaiter} class is only for adding new event waiters.
 */
@SuppressWarnings({"unused"})
public class EventWaiter implements EventListener {
    private final ScheduledExecutorService scheduleService = new ScheduledThreadPoolExecutor(5);
    private final List<Settings> waiters = new ArrayList<>();

    /**
     * Fires on every discord event.
     * This method checks if any waiter needs to be fired.
     *
     * @param action The event, which got fired.
     */
    @Override
    public void onEvent(@NotNull GenericEvent action) {
        final List<Settings> waitersCopy = new ArrayList<>(this.waiters); // Copy of current waiters to not interrupt the original ones
        final List<Settings> triggered = new ArrayList<>(); // Waiters which got triggered and need to be removed

        for (Settings waiter : waitersCopy) {
            if (waiter.type == action.getClass()) { // Waiting event is same as triggered event
                // Condition is met or no condition set
                if (waiter.condition == null || waiter.condition.test(action)) {
                    waiter.action.accept(action); // Run callback

                    // Remove waiter, if action doesn't remain
                    if (!waiter.remainAction) triggered.add(waiter);
                }
            }
        }

        this.waiters.removeAll(triggered);
    }

    /**
     * Create a new waiting event.
     * All important settings will be made in the {@link Settings} class.
     *
     * @param type The {@link Class} of the event to wait for.
     * @param <T>  T
     * @return Returns {@link Settings} to change settings on the waiters behavior.
     */
    public <T extends GenericEvent> Settings<T> waitForEvent(Class<T> type) {
        return new Settings<>(type);
    }

    /**
     * With this class you can set up specific settings for the waiter.
     * To actually register a new event waiter you must call the {@link Settings#load()} method.
     */
    public class Settings<T> {
        private final Class<T> type; // Event class to wait for
        private Predicate<T> condition; // Condition, which needs to be true in order to run the action
        private Consumer<T> action; // Action, which runs once the event fires
        private long timeoutDelay; // Value until timeout
        private TimeUnit timeunit; // Timeunit for timeout
        private Runnable timeoutAction; // Action, which runs on a timeout
        private boolean remainAction = false; // Should the action be repeatable?

        /**
         * Default constructor of event waiter.
         * Sets the event.
         *
         * @param type Event class to wait for.
         */
        public Settings(Class type) {
            this.type = type;
        }

        /**
         * A condition which needs to be true in order to run the event.
         *
         * @param condition A condition lmfao
         * @return Returns this
         */
        public Settings<T> setCondition(Predicate<T> condition) {
            this.condition = condition;
            return this;
        }

        /**
         * Add an action, which fires with the event.
         *
         * @param action A {@link Consumer} with the action you want to run, when the event fires.
         * @return Returns this
         */
        public Settings<T> setAction(Consumer<T> action) {
            this.action = action; // Set action
            return this;
        }

        /**
         * When should the timeout fire?
         * When the timeout runs, the waiter won't listen anymore.
         * <p>
         * If {@link Settings#timeoutAction} is not null,
         * it gets fired after the timeout.
         *
         * @param delay    The amount of {@link Settings#timeunit} to wait until the timeout.
         * @param timeUnit The timeunit for the timeout.
         *                 By default this value would be {@link TimeUnit#MILLISECONDS}
         * @return Returns this.
         */
        public Settings<T> setTimeout(long delay, TimeUnit timeUnit) {
            this.timeoutDelay = delay;
            this.timeunit = timeUnit;
            return this;
        }

        /**
         * Set a action, which runs on a timeout. This value is optional.
         * By default the event waiter gets removed after the timeout. So it won't listen to events anymore.
         *
         * @param timeoutAction A {@link Runnable}, which gets fired on a timeout.
         * @return Returns this.
         */
        public Settings<T> setTimeoutAction(Runnable timeoutAction) {
            this.timeoutAction = timeoutAction;
            return this;
        }

        /**
         * After the event waiter fired, it gets removed.
         * With this option, you can remain the action,
         * so that the event can be fired as long as the timeout doesn't run out.
         *
         * @return Returns this.
         */
        public Settings<T> remainsOnAction() {
            this.remainAction = true;
            return this;
        }

        /**
         * Adds the event waiter to the list of waiting events.
         */
        public void load() {
            waiters.add(this); // Add event waiter

            TimeUnit unit = TimeUnit.MILLISECONDS;
            if (this.timeunit != null) unit = this.timeunit;

            scheduleService.schedule(() -> {
                // Event waiter is still in the map
                if (waiters.contains(this.type)) {
                    waiters.remove(this.type); // Remove event waiter
                    // If timeout action is set, run it
                    if (this.timeoutAction != null) this.timeoutAction.run();
                }
            }, this.timeoutDelay, unit);

        }

    }

}
