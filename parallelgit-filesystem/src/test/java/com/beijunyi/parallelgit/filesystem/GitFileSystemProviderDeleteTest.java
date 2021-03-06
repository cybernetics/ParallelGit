package com.beijunyi.parallelgit.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

import org.junit.Test;

import static org.junit.Assert.*;

public class GitFileSystemProviderDeleteTest extends AbstractGitFileSystemTest {

  @Test
  public void deleteFile_fileShouldNotExistAfterDeletion() throws IOException {
    initRepository();
    writeToCache("/test_file.txt");
    commitToMaster();
    initGitFileSystem();

    GitPath path = gfs.getPath("/test_file.txt");
    provider.delete(path);
    assertFalse(Files.exists(path));
  }

  @Test
  public void deleteFile_theFileSystemShouldBecomeDirty() throws IOException {
    initRepository();
    writeToCache("/test_file.txt");
    commitToMaster();
    initGitFileSystem();

    GitPath path = gfs.getPath("/test_file.txt");
    provider.delete(path);
    assertTrue(gfs.isDirty());
  }

  @Test
  public void deleteEmptyDirectory_directoryShouldNotExistAfterDeletion() throws IOException {
    initRepository();
    writeToCache("/dir/some_file.txt");
    commitToMaster();
    initGitFileSystem();

    GitPath file = gfs.getPath("/dir/some_file.txt");
    provider.delete(file);
    GitPath dir = gfs.getPath("/dir");
    provider.delete(dir);
    assertFalse(Files.exists(dir));
  }

  @Test
  public void createAndDeleteEmptyDirectory_theFileSystemShouldStayClean() throws IOException {
    initGitFileSystem();
    GitPath dir = gfs.getPath("/empty_dir");
    provider.createDirectory(dir);
    provider.delete(dir);
    assertFalse(gfs.isDirty());
  }

  @Test
  public void deleteNonEmptyDirectory_directoryShouldNotExistAfterDeletion() throws IOException {
    initRepository();
    writeToCache("/dir/some_file.txt");
    commitToMaster();
    initGitFileSystem();

    GitPath dir = gfs.getPath("/dir");
    provider.delete(dir);
    assertFalse(Files.exists(dir));
  }

  @Test(expected = NoSuchFileException.class)
  public void deleteNonExistentFile_shouldThrowException() throws IOException {
    initGitFileSystem();
    provider.delete(gfs.getPath("/non_existent_file.txt"));
  }

}
