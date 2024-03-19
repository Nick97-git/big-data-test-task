package org.big.data.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FileStorage {
  private static FileStorage fileStorage;
  private final Map<String, BlockingQueue<String>> filesMap;

  public static FileStorage getInstance() {
    return Optional.ofNullable(fileStorage)
        .orElseGet(
            () -> {
              fileStorage = new FileStorage();
              return fileStorage;
            }
        );
  }

  private FileStorage() {
    filesMap = new HashMap<>();
  }

  public void addFilesForClientWithId(List<String> files, String clientId) {
    this.filesMap.computeIfPresent(
        clientId,
        (key, value) -> {
          value.addAll(files);
          return value;
        }
    );
  }

  public void addEndCommandForClientWithId(String endCommand, String clientId) {
    filesMap.get(clientId).add(endCommand);
  }

  public String getFirstForClientWithId(String clientId) {
    try {
      return filesMap.get(clientId).take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public void initForClientWithId(String clientId) {
    filesMap.put(clientId, new LinkedBlockingQueue<>());
  }
}
