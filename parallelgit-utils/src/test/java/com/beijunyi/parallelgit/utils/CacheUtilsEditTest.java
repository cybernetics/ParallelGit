package com.beijunyi.parallelgit.utils;

import java.io.IOException;
import javax.annotation.Nonnull;

import com.beijunyi.parallelgit.AbstractParallelGitTest;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.dircache.DirCacheBuilder;
import org.eclipse.jgit.dircache.DirCacheEditor;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CacheUtilsEditTest extends AbstractParallelGitTest {

  @Nonnull
  private static DirCache setupCache(@Nonnull String... files) {
    DirCache cache = DirCache.newInCore();
    DirCacheBuilder builder = cache.builder();
    for(String file : files)
      CacheUtils.addFile(file, FileMode.REGULAR_FILE, ObjectId.zeroId(), builder);
    builder.finish();
    return cache;
  }

  @Before
  public void setUp() throws IOException {
    initRepository();
  }

  @Test
  public void keepEverything_ExistingEntriesShouldBeKept() throws IOException {
    writeFilesToCache("/file1.txt");
    writeFilesToCache("/file2.txt");
    DirCacheBuilder builder = CacheUtils.keepEverything(cache);
    builder.finish();
    Assert.assertNotNull(CacheUtils.getEntry("/file1.txt", cache));
    Assert.assertNotNull(CacheUtils.getEntry("/file2.txt", cache));
  }

  @Test
  public void addFile_theFileShouldExistInTheCacheAfterTheOperation() {
    CacheUtils.addFile("/test_file.txt", someObjectId(), cache);
    Assert.assertNotNull(CacheUtils.getEntry("/test_file.txt", cache));
  }

  @Test
  public void addFile_theAddedFileShouldHaveTheSpecifiedObjectId() {
    AnyObjectId expected = someObjectId();
    CacheUtils.addFile("/test_file.txt", expected, cache);
    Assert.assertEquals(expected, CacheUtils.getBlob("/test_file.txt", cache));
  }

  @Test
  public void addFile_theAddedFileShouldHaveTheSpecifiedFileMode() {
    FileMode expected = FileMode.EXECUTABLE_FILE;
    CacheUtils.addFile("/test_file.txt", expected, someObjectId(), cache);
    Assert.assertEquals(expected, CacheUtils.getFileMode("/test_file.txt", cache));
  }

  @Test
  public void addFilesWithDirCacheBuilderTest() {
    DirCache cache = DirCache.newInCore();

    DirCacheBuilder builder = cache.builder();
    String[] files = new String[] {"a/b/c1.txt",
                                    "a/c2.txt",
                                    "a/c3.txt",
                                    "a/b/c4.txt"};
    for(String file : files)
      CacheUtils.addFile(file, FileMode.REGULAR_FILE, ObjectId.zeroId(), builder);builder.finish();

    int entryCount = cache.getEntryCount();
    Assert.assertEquals(4, entryCount);
  }

  @Test
  public void deleteFileTest() {
    DirCache cache = setupCache("a/b/c1.txt",
                                 "a/c2.txt",
                                 "a/c3.txt");

    CacheUtils.deleteFile("non_existent_file", cache);
    Assert.assertEquals(3, cache.getEntryCount());

    CacheUtils.deleteFile("a/b/c1.txt", cache);
    Assert.assertEquals(2, cache.getEntryCount());

    CacheUtils.deleteFile("a/c2.txt", cache);
    Assert.assertEquals(1, cache.getEntryCount());
  }

  @Test
  public void deleteFilesWithDirCacheEditorTest() {
    DirCache cache = setupCache("a/b/c1.txt",
                                 "a/b/c2.txt",
                                 "a/c3.txt",
                                 "a/c4.txt",
                                 "a/c5.txt",
                                 "a/c6.txt");

    DirCacheEditor editor = cache.editor();
    CacheUtils.deleteFile("a/b/c1.txt", editor);
    CacheUtils.deleteFile("a/c3.txt", editor);
    CacheUtils.deleteFile("a/c4.txt", editor);
    CacheUtils.deleteFile("a/c6.txt", editor);
    editor.finish();

    Assert.assertEquals(2, cache.getEntryCount());
    Assert.assertNull(cache.getEntry("a/b/c1.txt"));
    Assert.assertNotNull(cache.getEntry("a/b/c2.txt"));
    Assert.assertNotNull(cache.getEntry("a/c5.txt"));
  }

  @Test
  public void deleteTreeTest() {
    DirCache cache = setupCache("a/b/c1.txt",
                                 "a/b/c2.txt",
                                 "a/c3.txt",
                                 "a/c4.txt",
                                 "a/c5.txt",
                                 "a/c6.txt");

    CacheUtils.deleteDirectory("a/b", cache);

    Assert.assertEquals(4, cache.getEntryCount());
    Assert.assertNull(cache.getEntry("a/b/c1.txt"));
    Assert.assertNull(cache.getEntry("a/b/c2.txt"));
    Assert.assertNotNull(cache.getEntry("a/c3.txt"));
    Assert.assertNotNull(cache.getEntry("a/c4.txt"));
    Assert.assertNotNull(cache.getEntry("a/c5.txt"));
    Assert.assertNotNull(cache.getEntry("a/c6.txt"));
  }

  @Test
  public void deleteMultipleTreesTest() {
    DirCache cache = setupCache("a/b/c1.txt",
                                 "a/b/c2.txt",
                                 "a/d/c3.txt",
                                 "a/d/c4.txt",
                                 "a/c5.txt",
                                 "a/c6.txt");

    DirCacheEditor editor = cache.editor();
    CacheUtils.deleteDirectory("a/b", editor);
    CacheUtils.deleteDirectory("a/d", editor);
    editor.finish();

    Assert.assertEquals(2, cache.getEntryCount());
    Assert.assertNotNull(cache.getEntry("a/c5.txt"));
    Assert.assertNotNull(cache.getEntry("a/c6.txt"));
  }

  @Test
  public void updateFileBlob_theEntryBlobShouldEqualToTheInputBlobAfterTheOperation() throws IOException {
    writeToCache("/test_file.txt");

    AnyObjectId expected = someObjectId();
    CacheUtils.updateFileBlob("/test_file.txt", expected, cache);
    Assert.assertEquals(expected, CacheUtils.getBlob("/test_file.txt", cache));
  }

  @Test
  public void updateFileMode_theEntryFileModeShouldEqualToTheInputFileModeAfterTheOperation() throws IOException {
    writeToCache("/test_file.txt");

    CacheUtils.updateFileMode("/test_file.txt", FileMode.EXECUTABLE_FILE, cache);
    Assert.assertEquals(FileMode.EXECUTABLE_FILE, CacheUtils.getFileMode("/test_file.txt", cache));
  }

}
