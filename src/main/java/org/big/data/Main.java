package org.big.data;

import org.big.data.repository.FileStorage;
import org.big.data.service.ClientService;
import org.big.data.service.ServerService;

public class Main {
  public static void main(String[] args) {
    FileStorage fileStorage = FileStorage.getInstance();
    ClientService clientService = ClientService.getInstance(fileStorage);
    ServerService serverService = ServerService.getInstance(clientService, fileStorage);
    serverService.run();
  }
}
