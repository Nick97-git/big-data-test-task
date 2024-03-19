package org.big.data.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;
import org.big.data.repository.FileStorage;
import org.big.data.thread.SearchThread;

public class ServerService {
  private static ServerService serverService;
  private final ClientService clientService;
  private final FileStorage fileStorage;

  private ServerService(ClientService clientService, FileStorage fileStorage) {
    this.clientService = clientService;
    this.fileStorage = fileStorage;
  }

  public static ServerService getInstance(ClientService clientService, FileStorage fileStorage) {
    return Optional.ofNullable(serverService)
        .orElseGet(
            () -> {
              serverService = new ServerService(clientService, fileStorage);
              return serverService;
            }
        );
  }

  public void run() {
    SearchThread searchThread = startSearchThread(fileStorage);
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter port: ");
    int port = Integer.parseInt(scanner.nextLine());
    System.out.println("Enter root path: ");
    String rootPath = scanner.nextLine();

    while (true) {
      try (ServerSocket serverSocket = new ServerSocket(port)) {
        Socket clientSocket = serverSocket.accept();
        Thread clientThread = new Thread(() -> clientService.handleClient(clientSocket, rootPath, searchThread));
        clientThread.start();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static SearchThread startSearchThread(FileStorage fileStorage) {
    SearchThread searchThread = new SearchThread(new SearchService(fileStorage));
    searchThread.setDaemon(true);
    searchThread.start();
    return searchThread;
  }
}
