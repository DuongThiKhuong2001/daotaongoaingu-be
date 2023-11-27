package com.quanly.trungtamngoaingu.repository;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FilesStorageService {
  public void init();

  public void save(MultipartFile file);

  public void saveRandom(MultipartFile file, String filename);
  public Resource load(String filename);
  
  public boolean delete(String filename);

  public void deleteAll();

  public Stream<Path> loadAll();
}
