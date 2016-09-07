package org.codelogger.web.bean;

public class MemoryFile {

  private String name;

  private byte[] bytes;

  public MemoryFile(final String name, final byte[] bytes) {

    super();
    this.name = name;
    this.bytes = bytes;
  }

  public String getName() {

    return name;
  }

  public byte[] getBytes() {

    return bytes;
  }

}
