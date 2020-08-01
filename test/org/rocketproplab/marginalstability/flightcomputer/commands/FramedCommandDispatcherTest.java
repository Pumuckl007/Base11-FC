package org.rocketproplab.marginalstability.flightcomputer.commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
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
    FramedCommandDispatcher framedCommandDispatcher = new FramedCommandDispatcher(null);
    assertNull(framedCommandDispatcher.getFactoryForCommand("BC"));
  }

  @Test
  public void regsiteredFactoryIsRetruned() {
    FramedCommandDispatcher                      framedCommandDispatcher = new FramedCommandDispatcher(null);
    String                                       commandString           = "CD";
    StringCommand                                testCommand             = new StringCommand("Test");
    FramedCommandDispatcher.FramedCommandFactory factory                 = info -> testCommand;

    framedCommandDispatcher.registerFramedCommandFactory(commandString, factory);

    FramedCommandDispatcher.FramedCommandFactory actualFactory = framedCommandDispatcher
        .getFactoryForCommand(commandString);
    assertEquals(factory, actualFactory);
  }

  @Test
  public void newFactoryOverridesOldOne() {
    FramedCommandDispatcher                      framedCommandDispatcher = new FramedCommandDispatcher(null);
    String                                       commandString           = "CD";
    StringCommand                                testCommand             = new StringCommand("Test");
    FramedCommandDispatcher.FramedCommandFactory factory                 = info -> testCommand;

    framedCommandDispatcher.registerFramedCommandFactory(commandString, factory);
    framedCommandDispatcher.registerFramedCommandFactory(commandString, info -> new StringCommand(info));

    FramedCommandDispatcher.FramedCommandFactory actualFactory = framedCommandDispatcher
        .getFactoryForCommand(commandString);
    StringCommand                                actualCommand = (StringCommand) actualFactory.getCommand("Test");
    assertEquals("test", actualCommand.paramString);
  }

  @Test
  public void processFramedPacketSchedulesPacket() {
    FakeCommandScheduler    fakeCommandScheduler    = new FakeCommandScheduler();
    FramedCommandDispatcher framedCommandDispatcher = new FramedCommandDispatcher(fakeCommandScheduler);
    String                  commandString           = "EF";
    framedCommandDispatcher.registerFramedCommandFactory(commandString, info -> new StringCommand(info));
    framedCommandDispatcher.processFramedPacket("EF|Test");
    StringCommand newCommand = (StringCommand) fakeCommandScheduler.lastCommand;
    assertEquals("Test", newCommand.paramString);
  }
  
  @Test
  public void threeCharacterCommandWorks() {
    FakeCommandScheduler    fakeCommandScheduler    = new FakeCommandScheduler();
    FramedCommandDispatcher framedCommandDispatcher = new FramedCommandDispatcher(fakeCommandScheduler);
    String                  commandString           = "ABC";
    framedCommandDispatcher.registerFramedCommandFactory(commandString, info -> new StringCommand(info));
    framedCommandDispatcher.processFramedPacket("ABC|Hello World");
    StringCommand newCommand = (StringCommand) fakeCommandScheduler.lastCommand;
    assertEquals("Hello World", newCommand.paramString);
  }
  
  @Test
  public void processFramedPacketDoesNothingWhenNotRegistered() {
    FakeCommandScheduler    fakeCommandScheduler    = new FakeCommandScheduler();
    FramedCommandDispatcher framedCommandDispatcher = new FramedCommandDispatcher(fakeCommandScheduler);
    framedCommandDispatcher.processFramedPacket("HI|Test");
  }

}
