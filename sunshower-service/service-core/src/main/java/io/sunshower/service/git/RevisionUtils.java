package io.sunshower.service.git;

import io.sunshower.service.revision.model.Revision;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;

/** Created by haswell on 5/22/17. */
public class RevisionUtils {

  public static Revision fromCommit(RevCommit call) {
    final Revision result = new Revision();
    String s = ObjectId.toString(call.toObjectId());
    result.setRevision(s);
    return result;
  }
}
