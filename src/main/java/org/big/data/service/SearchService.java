package org.big.data.service;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import org.big.data.model.SearchTask;
import org.big.data.repository.FileStorage;

public class SearchService {
  public static final String END_COMMAND = "#CODE123CODE#";

  private final FileStorage fileStorage;

  public SearchService(FileStorage fileStorage) {
    this.fileStorage = fileStorage;
  }

  public void search(SearchTask searchTask) {
    LinkedList<File> files = new LinkedList<>();
    Optional.ofNullable(new File(searchTask.rootPath()).listFiles())
            .ifPresent(
                rootChildren -> {
                  Collections.addAll(files, rootChildren);
                  fileStorage.addFilesForClientWithId(
                      getValidFiles(searchTask, rootChildren),
                      searchTask.clientId()
                  );
                }
            );

    int depth = searchTask.depth();
    while (!files.isEmpty()) {
      depth--;
      while (!files.isEmpty()) {
        File current = files.poll();
        if (current.isDirectory() && depth > 0) {
          Optional.ofNullable(current.listFiles())
              .ifPresent(
                  listOfFiles -> fileStorage.addFilesForClientWithId(
                      getValidFiles(searchTask, listOfFiles),
                      searchTask.clientId()
                  )
              );
        }
      }
    }
    fileStorage.addEndCommandForClientWithId(END_COMMAND, searchTask.clientId());
  }

  private List<String> getValidFiles(SearchTask searchTask, File[] rootChildren) {
    return Arrays.stream(rootChildren)
        .filter(file -> file.getName().contains(searchTask.mask()))
        .map(File::getAbsolutePath)
        .toList();
  }
}
