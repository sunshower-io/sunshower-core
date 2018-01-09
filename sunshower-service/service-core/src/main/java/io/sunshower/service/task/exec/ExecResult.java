package io.sunshower.service.task.exec;

import io.sunshower.service.task.ExecutionResult;

/** Created by haswell on 2/15/17. */
class ExecResult implements ExecutionResult<Object> {

  private final Object value;

  public ExecResult(Object result) {
    this.value = result;
  }
}
