package com.codeaffine.extras.launch.internal.cleanup;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;

public class LaunchConfigCleanerState {

  public static final String DEFAULT_FILE_NAME = "cleanup-launch-configurations.txt";

  private final ILaunchManager launchManager;
  private final File file;

  public LaunchConfigCleanerState(ILaunchManager launchManager, File file) {
    this.launchManager = launchManager;
    this.file = file;
  }

  public void save(ILaunchConfiguration... launchConfigs) {
    try {
      Files.write(file.toPath(), getLaunchConfigNames(launchConfigs), UTF_8);
    } catch (IOException ioe) {
      handleSaveException(ioe);
    }
  }

  public ILaunchConfiguration[] restore() {
    ILaunchConfiguration[] result = new ILaunchConfiguration[0];
    try (Stream<String> stream = readLines()) {
      result = stream.map(this::findLaunchConfig).filter(Objects::nonNull).toArray(ILaunchConfiguration[]::new);
    } catch (IOException ioe) {
      handleRestoreExeption(ioe);
    }
    return result;
  }

  private Stream<String> readLines() throws IOException {
    try {
      return Files.lines(file.toPath(), UTF_8);
    } catch (NoSuchFileException ignore) {
      return Stream.empty();
    }
  }

  private ILaunchConfiguration findLaunchConfig(String name) {
    return Stream.of(getLaunchConfigs()).filter(launchConfig -> name.equals(launchConfig.getName())).findAny()
        .orElse(null);
  }

  private ILaunchConfiguration[] getLaunchConfigs() {
    try {
      return launchManager.getLaunchConfigurations();
    } catch (CoreException ce) {
      throw new RuntimeException(ce);
    }
  }

  private static List<String> getLaunchConfigNames(ILaunchConfiguration... launchConfigs) {
    return Stream.of(launchConfigs).map(launchConfig -> launchConfig.getName()).collect(toList());
  }

  private void handleSaveException(IOException ioe) {
    String msg = String.format("Failed to save launch configuration cleanup state to: ", file);
    throw new RuntimeException(msg, ioe);
  }

  private void handleRestoreExeption(IOException ioe) {
    String msg = String.format("Failed to restore launch configuration cleanup state to: ", file);
    throw new RuntimeException(msg, ioe);
  }
}
