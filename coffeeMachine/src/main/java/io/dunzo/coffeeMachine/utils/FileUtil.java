package io.dunzo.coffeeMachine.utils;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;

/**
 * @author swatigojra
 */
public class FileUtil
{
  public static  <T> T readFile(String filePath,Class<T> classType)
      throws FileNotFoundException, MalformedURLException {
    FileSystems.getDefault().getPath(filePath).toUri().toURL().toString();
    BufferedReader br = new BufferedReader(new FileReader(filePath));
    return new Gson().fromJson(br, classType);
  }
}
