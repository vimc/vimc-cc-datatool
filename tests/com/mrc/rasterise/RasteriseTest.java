package com.mrc.rasterise;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RasteriseTest {

  @Test
  void testNoArgs() {
    Exception thrown = Assertions.assertThrows(Exception.class,  () -> {
      Rasterise.main(new String[] {});
    });
    Assert.assertEquals("Requires one argument: config.toml", thrown.getMessage());

  }
  
  @Test
  void test2Args() {
    Exception thrown = Assertions.assertThrows(Exception.class,  () -> {
      Rasterise.main(new String[] {"a", "b"});
    });
    
    Assert.assertEquals("Requires one argument: config.toml", thrown.getMessage());
  }
  
  @Test
  void testSingleArg() {
    Exception thrown = Assertions.assertThrows(Exception.class,  () -> {
      Rasterise.main(new String[] {"a"});
    });
    
    Assert.assertEquals("Config file not found: a", thrown.getMessage());
  }

  void testSingleArg2() {
    try {
      Rasterise r = new Rasterise();
      int res = r.check_args(new String[] {"a"});
      Assert.assertEquals(res,  0);
    } catch (Exception e) {
      Assert.fail("No exception expected");
    }
  }
}
