package info.smart_tools.smartactors.scheduler.actor.impl.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.scheduler.actor.impl.EntryStorage#getLocalEntry(String)} when required entry was
 * cancelled during last 2 refresh iterations.
 */
public class CancelledLocalEntryRequestException extends Exception {
}
