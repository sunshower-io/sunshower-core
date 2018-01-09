package io.sunshower.model.core.auth;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/** Created by haswell on 5/9/17. */
@Embeddable
public class AuditStatus {

  @Column(name = "audit_success")
  private boolean success;

  @Column(name = "audit_failure")
  private boolean failure;

  public boolean isSuccess() {
    return success;
  }

  public void setSuccess(boolean success) {
    this.success = success;
  }

  public boolean isFailure() {
    return failure;
  }

  public void setFailure(boolean failure) {
    this.failure = failure;
  }
}
