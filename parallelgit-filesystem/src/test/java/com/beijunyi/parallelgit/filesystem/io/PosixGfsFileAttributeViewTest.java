package com.beijunyi.parallelgit.filesystem.io;

import java.io.IOException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import javax.annotation.Nonnull;

import com.beijunyi.parallelgit.filesystem.AbstractGitFileSystemTest;
import org.eclipse.jgit.lib.FileMode;
import org.junit.Test;

import static com.beijunyi.parallelgit.filesystem.io.BasicGfsFileAttributeViewTest.readAttribute;
import static org.junit.Assert.*;

public class PosixGfsFileAttributeViewTest extends AbstractGitFileSystemTest {

  @Test
  public void getName_shouldReturnPosix() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    assertEquals("posix", view.name());
  }

  @Test
  public void readAttributes_shouldReturnNotNull() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    assertNotNull(view.readAttributes());
  }

  @Test
  public void getOwnerOfFile_shouldReturnNull() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    assertNull(view.getOwner());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void setOwnerOfFile_shouldThrowUnsupportedOperationException() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    view.setOwner(new UserPrincipal() {
      @Nonnull
      @Override
      public String getName() {
        return "some_owner";
      }
    });
  }

  @Test(expected = UnsupportedOperationException.class)
  public void setGroupOfFile_shouldThrowUnsupportedOperationException() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    view.setGroup(new GroupPrincipal() {
      @Nonnull
      @Override
      public String getName() {
        return "some_group";
      }
    });
  }


  @Test
  public void getPermissionOfFile_shouldReturnNotNull() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    assertNotNull(readAttribute(view, GfsFileAttributeView.Posix.PERMISSIONS_NAME));
  }

  @Test
  public void getPermissionOfFile_shouldContainOwnerRead() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    Collection permissions = (Collection) readAttribute(view, GfsFileAttributeView.Posix.PERMISSIONS_NAME);
    assertNotNull(permissions);
    assertTrue(permissions.contains(PosixFilePermission.OWNER_READ));
  }

  @Test
  public void getPermissionOfFile_shouldContainOwnerWrite() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    Collection permissions = (Collection) readAttribute(view, GfsFileAttributeView.Posix.PERMISSIONS_NAME);
    assertNotNull(permissions);
    assertTrue(permissions.contains(PosixFilePermission.OWNER_WRITE));
  }

  @Test
  public void getPermissionOfFile_shouldNotContainOwnerExecute() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    Collection permissions = (Collection) readAttribute(view, GfsFileAttributeView.Posix.PERMISSIONS_NAME);
    assertNotNull(permissions);
    assertFalse(permissions.contains(PosixFilePermission.OWNER_EXECUTE));
  }
  
  

  @Test
  public void getPermissionOfExecutableFile_shouldContainOwnerExecute() throws IOException {
    initRepository();
    writeToCache("/file.txt", "some data".getBytes(), FileMode.EXECUTABLE_FILE);
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    Collection permissions = (Collection) readAttribute(view, GfsFileAttributeView.Posix.PERMISSIONS_NAME);
    assertNotNull(permissions);
    assertTrue(permissions.contains(PosixFilePermission.OWNER_EXECUTE));
  }

  @Test
  public void addExecutePermissionToFile_fileShouldBecomeExecutable() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    view.setPermissions(new HashSet<>(Arrays.asList(PosixFilePermission.OWNER_READ,
                                                     PosixFilePermission.OWNER_WRITE,
                                                     PosixFilePermission.OWNER_EXECUTE)));
  }

  @Test
  public void getOwnerAttributeOfFile_shouldReturnNull() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    assertNull(readAttribute(view, GfsFileAttributeView.Posix.OWNER_NAME));
  }

  @Test
  public void getGroupAttributeOfFile_shouldReturnNull() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    assertNull(readAttribute(view, GfsFileAttributeView.Posix.GROUP_NAME));
  }

  @Test(expected = UnsupportedOperationException.class)
  public void getNonPosixAttributeOfFile_shouldThrowUnsupportedOperationException() throws IOException {
    initRepository();
    writeToCache("/file.txt");
    commitToMaster();
    initGitFileSystem();

    GfsFileAttributeView.Posix view = provider.getFileAttributeView(gfs.getPath("/file.txt"), GfsFileAttributeView.Posix.class);
    assertNotNull(view);
    readAttribute(view, "nonPosixAttribute");
  }


}
