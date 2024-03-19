package org.big.data.service;

import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;
import org.big.data.model.SearchTask;
import org.big.data.repository.FileStorage;
import org.big.data.thread.ResultPrinterThread;
import org.big.data.thread.SearchThread;

public class ClientService {
  private static ClientService clientService;
  private final FileStorage fileStorage;

  private ClientService(FileStorage fileStorage) {
    this.fileStorage = fileStorage;
  }

  public static ClientService getInstance(FileStorage fileStorage) {
    return Optional.ofNullable(clientService)
        .orElseGet(
            () -> {
              clientService = new ClientService(fileStorage);
              return clientService;
            }
        );
  }

  public void handleClient(
      Socket clientSocket,
      String rootPath,
      SearchThread searchThread
  ) {
    try (
        Scanner inputStreamScanner = new Scanner(clientSocket.getInputStream());
        clientSocket
    ) {
      String clientId = UUID.randomUUID().toString();
      fileStorage.initForClientWithId(clientId);
      while (true) {
        clientSocket.getOutputStream().write("Enter depth: ".getBytes());
        int depth = Integer.parseInt(inputStreamScanner.nextLine());
        if (depth <= 0) {
          throw new RuntimeException("depth should be greater than 0");
        }
        clientSocket.getOutputStream().write("Enter mask: ".getBytes());
        String mask = inputStreamScanner.nextLine();

        searchThread.addTask(new SearchTask(rootPath, depth, mask, clientId));

        ResultPrinterThread listenerThread = new ResultPrinterThread(
            fileStorage,
            clientId,
            clientSocket.getOutputStream()
        );
        listenerThread.start();
        listenerThread.join();

        clientSocket.getOutputStream().write("Want to finish? Answer yes or no: ".getBytes());
        String answer = inputStreamScanner.nextLine();
        if (answer.equals("yes")) {
          break;
        }
      }
    } catch (Exception exception) {
      System.err.printf("Something happened. Try again. Error: %s%n", exception.getMessage());
    }
  }
}
