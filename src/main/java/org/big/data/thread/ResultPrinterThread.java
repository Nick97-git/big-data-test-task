package org.big.data.thread;

import static org.big.data.service.SearchService.END_COMMAND;

import java.io.IOException;
import java.io.OutputStream;
import org.big.data.repository.FileStorage;

public class ResultPrinterThread extends Thread {
  private final FileStorage fileStorage;
  private final String clientId;
  private final OutputStream clientOutputStream;

  public ResultPrinterThread(FileStorage fileStorage, String clientId, OutputStream clientOutputStream) {
    this.fileStorage = fileStorage;
    this.clientId = clientId;
    this.clientOutputStream = clientOutputStream;
  }

  @Override
  public void run() {
    while (true) {
      String file = fileStorage.getFirstForClientWithId(clientId);
      if (file.equals(END_COMMAND)) {
        break;
      }

      try {
        clientOutputStream.write("Found element: %s\n".formatted(file).getBytes());
      } catch (IOException e) {
        throw new RuntimeException("Can't write to output stream");
      }
    }
  }
}
