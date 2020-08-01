package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.comm.FramedPacketProcessor;

/**
 * Dispatches commands based on the info supplied in a framed packet. You can
 * register the command to schedule with the
 * {@link #registerFramedCommandFactory(String, FramedCommandFactory)} method.
 * 
 * @author Max Apodaca
 *
 */
public class FramedCommandDispatcher implements FramedPacketProcessor {

  /**
   * Creates a framed SCM packet based on the string.
   * 
   * @author Max Apodaca
   *
   */
  public interface FramedCommandFactory {

    /**
     * Get the command based on the info string
     * 
     * @param info the command info string
     * @return the new command
     */
    public Command getCommand(String info);
  }

  /**
   * Creates a dispatcher which will schedule commands to the given command scheduler.
   * @param scheduler The scheduler to dispatch to
   */
  public FramedCommandDispatcher(CommandScheduler scheduler) {
    
  }

  @Override
  public void processFramedPacket(String framedPacket) {

  }

  /**
   * Get the Factory to produce the command for the given command
   * @param command the command string to parse
   * @return the factory to create the command
   */
  protected FramedCommandFactory getFactoryForCommand(String command) {
    return null;
  }

  /**
   * Register a new command factory for a given string, only one may be registered at a time
   * @param command the command to register for
   * @param factory the factory to register
   */
  public void registerFramedCommandFactory(String command, FramedCommandFactory factory) {

  }

}
