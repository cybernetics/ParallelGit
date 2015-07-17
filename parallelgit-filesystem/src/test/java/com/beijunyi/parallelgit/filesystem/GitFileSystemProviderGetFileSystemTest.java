package com.beijunyi.parallelgit.filesystem;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;

import com.beijunyi.parallelgit.filesystem.utils.GitUriUtils;
import org.junit.Assert;
import org.junit.Test;

public class GitFileSystemProviderGetFileSystemTest extends AbstractGitFileSystemTest {

  @Test
  public void getFileSystemForUriTest() throws IOException {
    initGitFileSystem();
    URI uri = root.toUri();
    GitFileSystem result = (GitFileSystem) FileSystems.getFileSystem(uri);
    Assert.assertEquals(gfs, result);
  }

  @Test
  public void getFileSystemForUriWithNonExistentSessionIdTest() {
//    URI uri = GitUriUtils.createUri("/repo", "", "non_existent", false, false, null, null, null);
//    GitFileSystem result = (GitFileSystem) FileSystems.getFileSystem(uri);
//    Assert.assertNull(result);
  }

  @Test
  public void getFileSystemForUriWithNoSessionIdTest() {
//    URI uri = GitUriUtils.createUri("/repo", "", null, false, false, null, null, null);
//    GitFileSystem result = (GitFileSystem) FileSystems.getFileSystem(uri);
//    Assert.assertNull(result);
  }
}