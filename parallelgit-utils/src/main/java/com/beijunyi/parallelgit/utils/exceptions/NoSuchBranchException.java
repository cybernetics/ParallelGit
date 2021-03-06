package com.beijunyi.parallelgit.utils.exceptions;

import javax.annotation.Nonnull;

public class NoSuchBranchException extends RuntimeException {

  public NoSuchBranchException(@Nonnull String refName) {
    super("Branch " + refName + " does not exist");
  }

}
