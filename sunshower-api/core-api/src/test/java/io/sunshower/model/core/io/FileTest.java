package io.sunshower.model.core.io;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import io.sunshower.test.common.SerializationAware;
import io.sunshower.test.common.SerializationTestCase;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

/** Created by haswell on 10/14/17. */
@RunWith(JUnitPlatform.class)
public class FileTest extends SerializationTestCase {

  public FileTest() {
    super(SerializationAware.Format.JSON, new Class<?>[] {File.class});
  }

  @Test
  public void ensureSerializingFileWithPathWorks() {
    final File file = new File("parent");
    final File child = new File("child");
    final File gchild = new File("gchild");
    child.addChild(gchild);
    file.addChild(child);
    assertThat(copy(file).getPath(), is("parent"));
    write(file, System.out);
  }

  @Test
  public void ensureSplitProducesCorrectValue() {
    String value =
        "arn:aws:cloudformation:us-east-2:706501674089:stack/jh4/a757ba30-b8e7-11e7-a866-500ab3b9e8f2";
    System.out.println(value.split("/")[0].split(":")[4]);
  }

  @Test
  public void ensureSettingNameSetsExtension() {

    final File file = new File();
    file.setPath("frapper.dap");
    assertThat(file.getExtension(), is("dap"));
  }

  @Test
  public void ensureSettingPathDoesNotSetInvalidExtension() {
    final File file = new File();
    file.setPath(
        "io/sunshower-sdk/sdk-core/build/0854211a-356e-4c04-a71d-3e0ad4ba9802/XRKiB8ecSAJcVpz5ZD3XL7/local");
    assertThat(file.getExtension(), is(nullValue()));
  }
}
