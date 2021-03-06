package com.beijunyi.parallelgit.utils;

import java.io.IOException;

import com.beijunyi.parallelgit.AbstractParallelGitTest;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ReflogEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class BranchUtilsCreateBranchRefLogTest extends AbstractParallelGitTest {

  @Before
  public void setUp() throws IOException {
    initFileRepository(false);
  }

  @Test
  public void createBranchFromCommitName_theRefLogShouldStartWithBranchCreatedFromCommit() throws IOException {
    writeSomeFileToCache();
    AnyObjectId commit = commitToMaster();
    BranchUtils.createBranch("test_branch", commit.getName(), repo);
    ReflogEntry lastRefLog = RefUtils.getLastRefLog("test_branch", repo);
    assert lastRefLog != null;
    Assert.assertTrue(lastRefLog.getComment().startsWith("branch: Created from commit"));
  }

  @Test
  public void createBranchFromCommitId_theRefLogShouldStartWithBranchCreatedFromCommit() throws IOException {
    writeSomeFileToCache();
    AnyObjectId commit = commitToMaster();
    BranchUtils.createBranch("test_branch", commit, repo);
    ReflogEntry lastRefLog = RefUtils.getLastRefLog("test_branch", repo);
    assert lastRefLog != null;
    Assert.assertTrue(lastRefLog.getComment().startsWith("branch: Created from commit"));
  }

  @Test
  public void createBranchFromCommit_theRefLogShouldStartWithBranchCreatedFromCommit() throws IOException {
    writeSomeFileToCache();
    RevCommit commit = commitToMaster();
    BranchUtils.createBranch("test_branch", commit, repo);
    ReflogEntry lastRefLog = RefUtils.getLastRefLog("test_branch", repo);
    assert lastRefLog != null;
    Assert.assertTrue(lastRefLog.getComment().startsWith("branch: Created from commit"));
  }

  @Test
  public void createBranchFromBranchRef_theRefLogShouldStartWithBranchCreatedFromBranch() throws IOException {
    writeSomeFileToCache();
    commitToBranch("source_branch");
    BranchUtils.createBranch("test_branch", repo.getRef("source_branch"), repo);
    ReflogEntry lastRefLog = RefUtils.getLastRefLog("test_branch", repo);
    assert lastRefLog != null;
    Assert.assertTrue(lastRefLog.getComment().startsWith("branch: Created from branch"));
  }

  @Test
  public void createBranchFromBranchName_theRefLogShouldStartWithBranchCreatedFromBranch() throws IOException {
    writeSomeFileToCache();
    commitToBranch("source_branch");
    BranchUtils.createBranch("test_branch", "source_branch", repo);
    ReflogEntry lastRefLog = RefUtils.getLastRefLog("test_branch", repo);
    assert lastRefLog != null;
    Assert.assertTrue(lastRefLog.getComment().startsWith("branch: Created from branch"));
  }

  @Test
  public void createBranchFromTagRef_theHeadOfTheNewBranchShouldEqualToTheTaggedTag() throws IOException {
    writeSomeFileToCache();
    TagUtils.tagCommit("source_tag", commitToMaster(), repo);
    BranchUtils.createBranch("test_branch", repo.getRef("source_tag"), repo);
    ReflogEntry lastRefLog = RefUtils.getLastRefLog("test_branch", repo);
    assert lastRefLog != null;
    Assert.assertTrue(lastRefLog.getComment().startsWith("branch: Created from tag"));
  }

  @Test
  public void createBranchFromTagName_theHeadOfTheNewBranchShouldEqualToTheTaggedTag() throws IOException {
    writeSomeFileToCache();
    TagUtils.tagCommit("source_tag", commitToMaster(), repo);
    BranchUtils.createBranch("test_branch", "source_tag", repo);
    ReflogEntry lastRefLog = RefUtils.getLastRefLog("test_branch", repo);
    assert lastRefLog != null;
    Assert.assertTrue(lastRefLog.getComment().startsWith("branch: Created from tag"));
  }

  @Test
  public void createBranchFromTag_theHeadOfTheNewBranchShouldEqualToTheTaggedTag() throws IOException {
    writeSomeFileToCache();
    TagUtils.tagCommit("source_tag", commitToMaster(), repo);
    BranchUtils.createBranch("test_branch", repo.resolve("source_tag"), repo);
    ReflogEntry lastRefLog = RefUtils.getLastRefLog("test_branch", repo);
    assert lastRefLog != null;
    Assert.assertTrue(lastRefLog.getComment().startsWith("branch: Created from tag"));
  }

}
