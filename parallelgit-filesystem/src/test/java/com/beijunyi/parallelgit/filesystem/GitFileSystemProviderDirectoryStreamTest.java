package com.beijunyi.parallelgit.filesystem;

import java.io.IOException;
import java.nio.file.NotDirectoryException;

import org.junit.Assert;
import org.junit.Test;

public class GitFileSystemProviderDirectoryStreamTest extends AbstractGitFileSystemTest {

 @Test
 public void openDirectory_shouldReturnDirectoryStream() throws IOException {
   initRepository();
   writeFile("/dir/file.txt");
   commitToMaster();
   initGitFileSystem();
   Assert.assertNotNull(provider.newDirectoryStream(gfs.getPath("/dir"), null));
 }

  @Test(expected = NotDirectoryException.class)
  public void openRegularFile_shouldThrowException() throws IOException {
    initRepository();
    writeFile("/file.txt");
    commitToMaster();
    initGitFileSystem();
    provider.newDirectoryStream(gfs.getPath("/file.txt"), null);
  }

  @Test(expected = NotDirectoryException.class)
  public void openNonExistentDirectory_shouldThrowException() throws IOException {
    initGitFileSystem();
    provider.newDirectoryStream(gfs.getPath("/non_existent_directory"), null);
  }

}
