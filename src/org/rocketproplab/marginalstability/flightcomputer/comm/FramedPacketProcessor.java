package org.rocketproplab.marginalstability.flightcomputer.comm;

public interface FramedPacketProcessor {
  
  /**
   * Processes the given data in a framed SCM Packet
   * @param framedPacket the data from the framed packet.
   */
  public void processFramedPacket(String framedPacket);
}
