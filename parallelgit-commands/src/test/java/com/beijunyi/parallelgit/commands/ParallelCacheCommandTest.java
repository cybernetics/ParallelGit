package com.beijunyi.parallelgit.commands;

import java.io.IOException;

import com.beijunyi.parallelgit.AbstractParallelGitTest;
import com.beijunyi.parallelgit.utils.CacheUtils;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.FileMode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParallelCacheCommandTest extends AbstractParallelGitTest {

  @Before
  public void setupRepository() throws IOException {
    initRepository();
  }

  @Test
  public void buildCacheWithoutParameters_theResultCacheShouldBeEmpty() throws IOException {
    DirCache cache = ParallelCacheCommand.prepare().call();
    assertEquals(0, cache.getEntryCount());
  }

  @Test
  public void buildCacheWithBaseCommit_theResultCacheShouldHaveTheSameContentAsTheCommit1() throws IOException {
    AnyObjectId expectedFileBlob = writeToCache("/expected_file.txt");
    AnyObjectId commitId = commitToMaster();
    DirCache cache = ParallelCacheCommand.prepare(repo)
                       .baseCommit(commitId)
                       .call();
    assertEquals(expectedFileBlob, CacheUtils.getBlob("/expected_file.txt", cache));
  }

  @Test
  public void buildCacheWithBaseCommit_theResultCacheShouldHaveTheSameContentAsTheCommit2() throws IOException {
    AnyObjectId expectedFileBlob = writeToCache("/expected_file.txt");
    String commitIdStr = commitToMaster().getName();
    DirCache cache = ParallelCacheCommand.prepare(repo)
                       .baseCommit(commitIdStr)
                       .call();
    assertEquals(expectedFileBlob, CacheUtils.getBlob("/expected_file.txt", cache));
  }

  @Test
  public void buildCacheWithBaseTree_theResultCacheShouldHaveTheSameContentAsTheTree1() throws IOException {
    AnyObjectId expectedFileBlob = writeToCache("/expected_file.txt");
    AnyObjectId treeId = commitToMaster().getTree();
    DirCache cache = ParallelCacheCommand.prepare(repo)
                       .baseTree(treeId)
                       .call();
    assertEquals(expectedFileBlob, CacheUtils.getBlob("/expected_file.txt", cache));
  }

  @Test
  public void buildCacheWithBaseTree_theResultCacheShouldHaveTheSameContentAsTheTree2() throws IOException {
    AnyObjectId expectedFileBlob = writeToCache("/expected_file.txt");
    String treeIdStr = commitToMaster().getTree().getName();
    DirCache cache = ParallelCacheCommand.prepare(repo)
                       .baseTree(treeIdStr)
                       .call();
    assertEquals(expectedFileBlob, CacheUtils.getBlob("/expected_file.txt", cache));
  }

  @Test
  public void addFile_theResultCacheShouldHaveTheAddedFile() throws IOException {
    AnyObjectId blobId = someObjectId();
    DirCache cache = ParallelCacheCommand.prepare()
                       .addFile("/expected_file.txt", blobId)
                       .call();
    assertNotNull(CacheUtils.getEntry("/expected_file.txt", cache));
  }

  @Test
  public void addFile_theResultFileShouldHaveTheSpecifiedBlobId() throws IOException {
    AnyObjectId blobId = someObjectId();
    DirCache cache = ParallelCacheCommand.prepare()
                       .addFile("/expected_file.txt", blobId)
                       .call();
    assertEquals(blobId, CacheUtils.getBlob("/expected_file.txt", cache));
  }

  @Test
  public void addFile_theResultFileShouldBeRegularFileByDefault() throws IOException {
    AnyObjectId blobId = someObjectId();
    DirCache cache = ParallelCacheCommand.prepare()
                       .addFile("/expected_file.txt", blobId)
                       .call();
    assertEquals(FileMode.REGULAR_FILE, CacheUtils.getFileMode("/expected_file.txt", cache));
  }

  @Test
  public void addFileWithFileMode_theResultFileShouldHaveTheSpecifiedFileMode() throws IOException {
    AnyObjectId blobId = someObjectId();
    FileMode mode = FileMode.EXECUTABLE_FILE;
    DirCache cache = ParallelCacheCommand.prepare()
                       .addFile("/expected_file.txt", blobId, mode)
                       .call();
    assertEquals(mode, CacheUtils.getFileMode("/expected_file.txt", cache));
  }

  @Test
  public void addDirectoryFromTree_theChildrenShouldExist() throws IOException {
    writeToCache("/child1.txt");
    writeToCache("/child2.txt");
    AnyObjectId treeWithTwoChildren = commitToMaster().getTree();
    DirCache cache = ParallelCacheCommand.prepare(repo)
                       .addDirectory("/dir", treeWithTwoChildren)
                       .call();
    assertNotNull(CacheUtils.getEntry("/dir/child1.txt", cache));
    assertNotNull(CacheUtils.getEntry("/dir/child2.txt", cache));
  }

  @Test
  public void addDirectoryFromTree_theChildrenShouldHaveTheInputBlobIds() throws IOException {
    AnyObjectId blob1 = writeToCache("/child1.txt");
    AnyObjectId blob2 = writeToCache("/child2.txt");
    AnyObjectId treeWithTwoChildren = commitToMaster().getTree();
    DirCache cache = ParallelCacheCommand.prepare(repo)
                       .addDirectory("/dir", treeWithTwoChildren)
                       .call();
    assertEquals(blob1, CacheUtils.getBlob("/dir/child1.txt", cache));
    assertEquals(blob2, CacheUtils.getBlob("/dir/child2.txt", cache));
  }

  @Test
  public void addDirectoryFromTreeIdString_theChildrenShouldExist() throws IOException {
    writeToCache("/child1.txt");
    writeToCache("/child2.txt");
    AnyObjectId treeWithTwoChildren = commitToMaster().getTree();
    DirCache cache = ParallelCacheCommand.prepare(repo)
                       .addDirectory("/dir", treeWithTwoChildren.getName())
                       .call();
    assertNotNull(CacheUtils.getEntry("/dir/child1.txt", cache));
    assertNotNull(CacheUtils.getEntry("/dir/child2.txt", cache));
  }

  @Test(expected = IllegalArgumentException.class)
  public void addDirectoryWithoutRepository_shouldThrowIllegalArgumentException() throws IOException {
    writeToCache("/child1.txt");
    writeToCache("/child2.txt");
    AnyObjectId treeWithTwoChildren = commitToMaster().getTree();
    ParallelCacheCommand.prepare()
      .addDirectory(treeWithTwoChildren.getName(), "/dir")
      .call();
  }

  @Test
  public void deleteFile_theTargetFileShouldNotExistAfterDeletion() throws IOException {
    writeToCache("/file.txt");
    AnyObjectId commitId = commitToMaster();
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .deleteFile("/file.txt")
                       .call();
    Assert.assertNull(cache.getEntry("/file.txt"));
  }

  @Test
  public void deleteFile_shouldHaveNoEffectOnOtherFile() throws IOException {
    writeToCache("/file.txt");
    writeToCache("/some_other_file.txt");
    AnyObjectId commitId = commitToMaster();
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .deleteFile("/file.txt")
                       .call();
    assertNotNull(CacheUtils.getEntry("/some_other_file.txt", cache));
  }

  @Test
  public void deleteNonExistentFile_shouldHaveNoEffectOnTheCache() throws IOException {
    writeToCache("/some_file.txt");
    AnyObjectId commitId = commitToMaster();
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .deleteFile("/non_existent_file.txt")
                       .call();
    assertEquals(1, cache.getEntryCount());
  }


  @Test
  public void deleteDirectory_childrenInTheTargetDirectoryShouldNotExistAfterDeletion() throws IOException {
    writeToCache("/dir/file1.txt");
    writeToCache("/dir/file2.txt");
    AnyObjectId commitId = commitToMaster();
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .deleteDirectory("/dir")
                       .call();
    Assert.assertNull(CacheUtils.getEntry("/dir/file1.txt", cache));
    Assert.assertNull(CacheUtils.getEntry("/dir/file2.txt", cache));
  }

  @Test
  public void deleteDirectory_shouldHaveNoEffectOnFilesOutsideTheTargetDirectory() throws IOException {
    writeToCache("/a/b/1.txt");
    writeToCache("/a/b/2.txt");
    writeToCache("/a/3.txt");
    writeToCache("/4.txt");
    AnyObjectId commitId = commitToMaster();
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .deleteDirectory("/a/b")
                       .call();
    assertNotNull(CacheUtils.getEntry("/a/3.txt", cache));
    assertNotNull(CacheUtils.getEntry("/4.txt", cache));
  }


  @Test
  public void updateFile_theTargetFileShouldHaveTheNewBlobIdAndFileMode() throws IOException {
    writeToCache("/file.txt", "old content");
    AnyObjectId commitId = commitToMaster();
    AnyObjectId newBlobId = someObjectId();
    FileMode newFileMode = FileMode.EXECUTABLE_FILE;
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .updateFile("/file.txt", newBlobId, newFileMode)
                       .call();
    assertEquals(newBlobId, CacheUtils.getBlob("/file.txt", cache));
    assertEquals(newFileMode, CacheUtils.getFileMode("/file.txt", cache));
  }

  @Test
  public void updateBlobIdOnly_theTargetFileShouldHaveTheNewBlobId() throws IOException {
    writeToCache("/file.txt", "old content");
    AnyObjectId commitId = commitToMaster();
    AnyObjectId newBlobId = someObjectId();
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .updateFile("/file.txt", newBlobId)
                       .call();
    assertEquals(newBlobId, CacheUtils.getBlob("/file.txt", cache));
  }

  @Test
  public void updateBlobIdOnly_theTargetFileShouldHaveTheOldFileMode() throws IOException {
    writeToCache("/file.txt", "some content".getBytes(), FileMode.SYMLINK);
    AnyObjectId commitId = commitToMaster();
    AnyObjectId newBlobId = someObjectId();
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .updateFile("/file.txt", newBlobId)
                       .call();
    assertEquals(FileMode.SYMLINK, CacheUtils.getFileMode("/file.txt", cache));
  }

  @Test
  public void updateFileModeOnly_theTargetFileShouldHaveTheNewFileMode() throws IOException {
    writeToCache("/file.txt", "some content".getBytes());
    AnyObjectId commitId = commitToMaster();
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .updateFile("/file.txt", FileMode.EXECUTABLE_FILE)
                       .call();
    assertEquals(FileMode.EXECUTABLE_FILE, CacheUtils.getFileMode("/file.txt", cache));
  }

  @Test
  public void updateFileModeOnly_theTargetFileShouldHaveTheOldBlobId() throws IOException {
    AnyObjectId blobId = writeToCache("/file.txt", "some content".getBytes());
    AnyObjectId commitId = commitToMaster();
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .updateFile("/file.txt", FileMode.EXECUTABLE_FILE)
                       .call();
    assertEquals(blobId, CacheUtils.getBlob("/file.txt", cache));
  }

  @Test(expected = IllegalArgumentException.class)
  public void updateNonExistentFile_shouldThrowIllegalArgumentException() throws IOException {
    AnyObjectId commitId = commitToMaster();
    AnyObjectId blobId = someObjectId();
    DirCache cache = ParallelCacheCommand
                       .prepare(repo)
                       .baseCommit(commitId)
                       .updateFile("/non_existent_file.txt", blobId)
                       .call();
    assertNotNull(CacheUtils.getEntry("/non_existent_file.txt", cache));
  }

}
