package org.rocketproplab.marginalstability.flightcomputer.commands;

import org.rocketproplab.marginalstability.flightcomputer.comm.FramedPacketProcessor;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.events.PacketListener;

/**
 * Dispatches commands based on the info supplied in a framed packet. You can
 * register the command to schedule with the
 * {@link #registerFramedCommandFactory(String, CommandFactory)} method.
 * 
 * @author Max Apodaca
 *
 */
public class CommandDispatcher implements FramedPacketProcessor, PacketListener<SCMPacket> {

  /**
   * Creates a command from a string. The string can come from either a Framed SCM
   * packet or a regular SCM Packet
   * 
   * @author Max Apodaca
   *
   */
  public interface CommandFactory {

    /**
     * Get the command based on the info string
     * 
     * @param info the command info string
     * @return the new command
     */
    public Command getCommand(String info);
  }

  /**
   * Creates a dispatcher which will schedule commands to the given command
   * scheduler.
   * 
   * @param scheduler The scheduler to dispatch to
   */
  public CommandDispatcher(CommandScheduler scheduler) {

  }

  @Override
  public void processFramedPacket(String framedPacket) {

  }

  /**
   * Get the Factory to produce the command for the given command
   * 
   * @param command the command string to parse
   * @return the factory to create the command
   */
  protected CommandFactory getFactoryForCommand(String command) {
    return null;
  }

  /**
   * Register a new command factory for a given string, only one may be registered
   * at a time. Note this may conflict if the same string is used as the
   * {@link SCMPacketType#toString()} returns.
   * 
   * @param command the command to register for
   * @param factory the factory to register
   */
  public void registerFramedCommandFactory(String command, CommandFactory factory) {

  }

  /**
   * Register a new command factory for a given packet type, only one may be
   * registered at a time
   * 
   * @param command the command to register for
   * @param factory the factory to register
   */
  public void registerFramedCommandFactory(SCMPacketType command, CommandFactory factory) {

  }

  @Override
  public void onPacket(PacketDirection direction, SCMPacket packet) {

  }

}
