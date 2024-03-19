package org.big.data.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.big.data.model.SearchTask;
import org.big.data.service.SearchService;

public class SearchThread extends Thread {
  private final BlockingQueue<SearchTask> searchTasksQueue;
  private final SearchService searchService;
  private final ExecutorService executorService;

  public SearchThread(SearchService searchService) {
    this.searchService = searchService;
    executorService = Executors.newCachedThreadPool();
    searchTasksQueue = new LinkedBlockingQueue<>();
  }

  @Override
  public void run() {
    while (true) {
      try {
        SearchTask searchTask = searchTasksQueue.take();
        executorService.execute(() -> searchService.search(searchTask));
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void addTask(SearchTask searchTask) {
    searchTasksQueue.add(searchTask);
  }
}
