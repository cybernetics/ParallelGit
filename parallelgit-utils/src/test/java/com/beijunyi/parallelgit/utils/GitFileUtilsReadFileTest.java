package com.beijunyi.parallelgit.utils;

import java.io.IOException;
import java.nio.file.NoSuchFileException;

import com.beijunyi.parallelgit.AbstractParallelGitTest;
import org.eclipse.jgit.lib.AnyObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GitFileUtilsReadFileTest extends AbstractParallelGitTest {

  @Before
  public void setupRepository() throws IOException {
    initRepository();
  }

  @Test
  public void readFile_theResultShouldEqualToTheFileContent() throws IOException {
    byte[] expected = "test data".getBytes();
    writeToCache("/test_file.txt", expected);
    AnyObjectId commit = commitToMaster();
    byte[] actual = GitFileUtils.readFile("/test_file.txt", commit.getName(), repo);
    Assert.assertArrayEquals(expected, actual);
  }

  @Test(expected = NoSuchFileException.class)
  public void readFileWhenFileDoesNotExist_shouldThrowNoSuchFileException() throws IOException {
    writeSomeFileToCache();
    AnyObjectId commit = commitToMaster();
    GitFileUtils.readFile("/non_existent_file.txt", commit.getName(), repo);
  }

}
