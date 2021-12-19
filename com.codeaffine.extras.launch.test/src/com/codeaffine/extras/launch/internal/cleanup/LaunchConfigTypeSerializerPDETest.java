package com.codeaffine.extras.launch.internal.cleanup;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.codeaffine.extras.launch.test.LaunchConfigRule;

public class LaunchConfigTypeSerializerPDETest {

  @Rule
  public final LaunchConfigRule launchConfigRule = new LaunchConfigRule();

  private LaunchConfigTypeSerializer serializer;

  @Before
  public void setUp() {
    serializer = new LaunchConfigTypeSerializer(DebugPlugin.getDefault().getLaunchManager());
  }

  @Test
  public void testSerialize() {
    ILaunchConfigurationType type = launchConfigRule.getPublicTestLaunchConfigType();

    String serialized = serializer.serialize(type);

    assertThat(serialized).isEqualTo(type.getIdentifier());
  }

  @Test
  public void testSerializeMultipleTypes() {
    ILaunchConfigurationType type = launchConfigRule.getPublicTestLaunchConfigType();

    String serialized = serializer.serialize(type, type);

    assertThat(serialized).isEqualTo(type.getIdentifier() + "," + type.getIdentifier());
  }

  @Test
  public void testDeserializeEmptyString() {
    ILaunchConfigurationType[] types = serializer.deserialize("");

    assertThat(types).isEmpty();
  }

  @Test
  public void testDeserializeNonExistingType() {
    ILaunchConfigurationType[] types = serializer.deserialize("foo");

    assertThat(types).isEmpty();
  }

  @Test
  public void testDeserializeMalformedInput() {
    ILaunchConfigurationType[] types = serializer.deserialize(",,foo, ,bar,");

    assertThat(types).isEmpty();
  }
}
