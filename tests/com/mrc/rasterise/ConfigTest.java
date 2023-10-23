package com.mrc.rasterise;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConfigTest {

  @Test
  void testLoadConfig() {
    Assertions.assertThrows(FileNotFoundException.class, () -> {
      new Config("file_doesnt.exist");  
    });
  }
  
  private void writeFakeToml(String f, String s) throws IOException {
    PrintWriter PW = new PrintWriter(new FileWriter(f));
    PW.println(s);
    PW.close();
  }
  
  @Test
  void testMissingPopulationToml() throws Exception {
    writeFakeToml("test.toml", "");
    Assertions.assertThrows(Exception.class, () -> {
      new Config("test.toml");  
    });
  }
  
  @Test
  void testMissingPopulationFile() throws Exception {
    writeFakeToml("test.toml", "population = 'nonexist.bil'\n");
    Exception e = Assertions.assertThrows(Exception.class, () -> {
      new Config("test.toml");  
    });
    Assert.assertEquals("Population file not found : nonexist.bil", e.getMessage());
  }
  
  @Test
  void testInvalidToml() throws Exception {
    writeFakeToml("test.toml", "population = 'pop.bil'\npopulation = 'pop2.bil'\n");
    Assertions.assertThrows(Exception.class, () -> {
      new Config("test.toml");  
    });
  }
  
  private void writeFakePop(String f) {
    
  }
  
  @Test
  void testTomlOK() throws Exception {
    writeFakeToml("test.toml", "population = 'pop.bil'\n");
    Config c = new Config("test.toml");
    Assertions.assertNotNull(c);
    System.out.println(c.toml.get("population"));
  }
  

}
