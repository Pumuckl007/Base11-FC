package org.rocketproplab.marginalstability.flightcomputer.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.rocketproplab.marginalstability.flightcomputer.comm.PacketDirection;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacket;
import org.rocketproplab.marginalstability.flightcomputer.comm.SCMPacketType;
import org.rocketproplab.marginalstability.flightcomputer.subsystems.Subsystem;

public class FramedCommandDispatcherTest {

  private class StringCommand implements Command {
    public String paramString;

    public StringCommand(String string) {
      this.paramString = string;
    }

    @Override
    public boolean isDone() {
      return true;
    }

    @Override
    public void execute() {

    }

    @Override
    public void start() {

    }

    @Override
    public void end() {

    }

    @Override
    public Subsystem[] getDependencies() {
      return new Subsystem[0];
    }

  }

  private class FakeCommandScheduler extends CommandScheduler {
    private Command lastCommand;

    @Override
    public void scheduleCommand(Command command) {
      this.lastCommand = command;
    }

  }

  @Test
  public void regsiteredFactoryIsNullWhenNotRegistered() {
    CommandDispatcher framedCommandDispatcher = new CommandDispatcher(null);
    assertNull(framedCommandDispatcher.getFactoryForCommand("BC"));
  }

  @Test
  public void regsiteredFactoryIsRetruned() {
    CommandDispatcher                framedCommandDispatcher = new CommandDispatcher(null);
    String                           commandString           = "CD";
    StringCommand                    testCommand             = new StringCommand("Test");
    CommandDispatcher.CommandFactory factory                 = StringCommand::new;

    framedCommandDispatcher.registerFramedCommandFactory(commandString, factory);

    CommandDispatcher.CommandFactory actualFactory = framedCommandDispatcher.getFactoryForCommand(commandString);
    assertEquals(factory, actualFactory);
  }

  @Test
  public void newFactoryOverridesOldOne() {
    CommandDispatcher                framedCommandDispatcher = new CommandDispatcher(null);
    String                           commandString           = "CD";
    StringCommand                    testCommand             = new StringCommand("Test");
    CommandDispatcher.CommandFactory factory                 = info -> testCommand;

    framedCommandDispatcher.registerFramedCommandFactory(commandString, factory);
    framedCommandDispatcher.registerFramedCommandFactory(commandString, StringCommand::new);

    CommandDispatcher.CommandFactory actualFactory = framedCommandDispatcher.getFactoryForCommand(commandString);
    StringCommand                    actualCommand = (StringCommand) actualFactory.getCommand("Test");
    assertEquals("test", actualCommand.paramString);
  }

  @Test
  public void processFramedPacketSchedulesPacket() {
    FakeCommandScheduler fakeCommandScheduler    = new FakeCommandScheduler();
    CommandDispatcher    framedCommandDispatcher = new CommandDispatcher(fakeCommandScheduler);
    String               commandString           = "EF";
    framedCommandDispatcher.registerFramedCommandFactory(commandString, StringCommand::new);
    framedCommandDispatcher.processFramedPacket("EF|Test");
    StringCommand newCommand = (StringCommand) fakeCommandScheduler.lastCommand;
    assertEquals("Test", newCommand.paramString);
  }

  @Test
  public void threeCharacterCommandWorks() {
    FakeCommandScheduler fakeCommandScheduler    = new FakeCommandScheduler();
    CommandDispatcher    framedCommandDispatcher = new CommandDispatcher(fakeCommandScheduler);
    String               commandString           = "ABC";
    framedCommandDispatcher.registerFramedCommandFactory(commandString, StringCommand::new);
    framedCommandDispatcher.processFramedPacket("ABC|Hello World");
    StringCommand newCommand = (StringCommand) fakeCommandScheduler.lastCommand;
    assertEquals("Hello World", newCommand.paramString);
  }

  @Test
  public void processFramedPacketDoesNothingWhenNotRegistered() {
    FakeCommandScheduler fakeCommandScheduler    = new FakeCommandScheduler();
    CommandDispatcher    framedCommandDispatcher = new CommandDispatcher(fakeCommandScheduler);
    framedCommandDispatcher.processFramedPacket("HI|Test");
  }

  @Test
  public void registeringToAPacketTypeWorksAsWell() {
    FakeCommandScheduler fakeCommandScheduler    = new FakeCommandScheduler();
    CommandDispatcher    framedCommandDispatcher = new CommandDispatcher(fakeCommandScheduler);
    SCMPacketType        type                    = SCMPacketType.DD;
    SCMPacket            packet                  = new SCMPacket(type, "Hello");

    framedCommandDispatcher.registerFramedCommandFactory(type, StringCommand::new);

    framedCommandDispatcher.onPacket(PacketDirection.RECIVE, packet);
    StringCommand newCommand = (StringCommand) fakeCommandScheduler.lastCommand;
    assertEquals("Hello", newCommand.paramString);
  }

  @Test
  public void wrongDirectionDoesNotScheduleCommand() {
    FakeCommandScheduler fakeCommandScheduler    = new FakeCommandScheduler();
    CommandDispatcher    framedCommandDispatcher = new CommandDispatcher(fakeCommandScheduler);
    SCMPacketType        type                    = SCMPacketType.DD;
    SCMPacket            packet                  = new SCMPacket(type, "Hello");

    framedCommandDispatcher.registerFramedCommandFactory(type, info -> {
      throw new RuntimeException("Not implement yet");
    });

    framedCommandDispatcher.onPacket(PacketDirection.SEND, packet);
    assertNull(fakeCommandScheduler.lastCommand);
  }

  @Test
  public void differentCommandTypeDoesNotSchedule() {
    FakeCommandScheduler fakeCommandScheduler    = new FakeCommandScheduler();
    CommandDispatcher    framedCommandDispatcher = new CommandDispatcher(fakeCommandScheduler);
    SCMPacketType        type                    = SCMPacketType.DD;
    SCMPacket            packet                  = new SCMPacket(SCMPacketType.ER, "Hello");

    framedCommandDispatcher.registerFramedCommandFactory(type, info -> {
      throw new RuntimeException("Not implement yet");
    });

    framedCommandDispatcher.onPacket(PacketDirection.RECIVE, packet);
    assertNull(fakeCommandScheduler.lastCommand);
  }

  @Test
  public void registeringTwoOfBothTypesWorks() {
    FakeCommandScheduler fakeCommandScheduler    = new FakeCommandScheduler();
    CommandDispatcher    framedCommandDispatcher = new CommandDispatcher(fakeCommandScheduler);
    SCMPacketType        type                    = SCMPacketType.DD;
    SCMPacketType        type2                   = SCMPacketType.GX;
    SCMPacket            packet                  = new SCMPacket(type, "Hello");
    SCMPacket            packet2                 = new SCMPacket(type2, "World");
    String               framedSCM               = "F1|Quantum";
    String               framedSCM2              = "F2|Encabulator";

    framedCommandDispatcher.registerFramedCommandFactory(type, StringCommand::new);
    framedCommandDispatcher.registerFramedCommandFactory(type2, info -> new StringCommand(info + "2"));
    framedCommandDispatcher.registerFramedCommandFactory("F1", info -> new StringCommand(info + "3"));
    framedCommandDispatcher.registerFramedCommandFactory("F2", info -> new StringCommand(info + "4"));

    framedCommandDispatcher.onPacket(PacketDirection.RECIVE, packet);
    StringCommand newCommand = (StringCommand) fakeCommandScheduler.lastCommand;
    assertEquals("Hello", newCommand.paramString);

    framedCommandDispatcher.processFramedPacket(framedSCM);
    newCommand = (StringCommand) fakeCommandScheduler.lastCommand;
    assertEquals("Quantum3", newCommand.paramString);

    framedCommandDispatcher.onPacket(PacketDirection.RECIVE, packet2);
    newCommand = (StringCommand) fakeCommandScheduler.lastCommand;
    assertEquals("World2", newCommand.paramString);

    framedCommandDispatcher.processFramedPacket(framedSCM2);
    newCommand = (StringCommand) fakeCommandScheduler.lastCommand;
    assertEquals("Encabulator4", newCommand.paramString);
  }

}
