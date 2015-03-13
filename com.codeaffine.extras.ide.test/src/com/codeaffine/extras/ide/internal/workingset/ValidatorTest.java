package com.codeaffine.extras.ide.internal.workingset;

import static com.codeaffine.extras.ide.internal.workingset.RegexPatterns.ANYTHING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IProject;
import org.junit.Before;
import org.junit.Test;

import com.codeaffine.extras.ide.internal.workingset.ValidationStatus.Severity;

public class ValidatorTest {

  private TestProjectsProvider projectsProvider;
  private JdtFeature jdtFeature;
  private Validator validator;

  @Before
  public void setUp() {
    projectsProvider = new TestProjectsProvider();
    jdtFeature = mock( JdtFeature.class );
    validator = new Validator( projectsProvider, jdtFeature );
  }

  @Test
  public void testValidateEmptyName() {
    ValidationStatus validationStatus = validator.validate( "", "pattern" );

    assertMessageIsError( validationStatus, Validator.MSG_NAME_EMPTY );
  }

  @Test
  public void testValidateEmptyPattern() {
    ValidationStatus validationStatus = validator.validate( "name", "" );

    assertMessageIsError( validationStatus, Validator.MSG_PATTERN_EMPTY );
  }

  @Test
  public void testValidateInvalidPattern() {
    ValidationStatus validationStatus = validator.validate( "name", "*" );

    assertMessageIsError( validationStatus, Validator.MSG_PATTERN_INVALID );
  }

  @Test
  public void testValidateWithNameAndPattern() {
    ValidationStatus validationStatus = validator.validate( "name", "pattern" );

    assertStatusIsOk( validationStatus );
  }

  @Test
  public void testValidateNonMatchingPatternWhenJdtInstalled() {
    installJdt();
    createProject( "name" );
    ValidationStatus validationStatus = validator.validate( "name", "matches-nothing" );

    assertStatusIsWarning( validationStatus, Validator.MSG_JDT_RESTRICTION );
  }

  @Test
  public void testValidateMatchingPatternWhenJdtInstalled() {
    installJdt();
    createProject( "name" );
    ValidationStatus validationStatus = validator.validate( "name", ANYTHING );

    assertStatusIsOk( validationStatus );
  }

  @Test
  public void testValidateNonMatchingPatternWhenJdtNotInstalled() {
    createProject( "name" );
    ValidationStatus validationStatus = validator.validate( "name", "matches-nothing" );

    assertStatusIsOk( validationStatus );
  }

  private static void assertStatusIsOk( ValidationStatus validationStatus ) {
    assertThat( validationStatus.getSeverity() ).isEqualTo( Severity.NONE );
    assertThat( validationStatus.getMessage() ).isEmpty();
  }

  private static void assertStatusIsWarning( ValidationStatus validationStatus, String message ) {
    assertThat( validationStatus.getSeverity() ).isEqualTo( Severity.WARNING );
    assertThat( validationStatus.getMessage() ).isEqualTo( message );
  }

  private static void assertMessageIsError( ValidationStatus validationStatus, String message ) {
    assertThat( validationStatus.getSeverity() ).isEqualTo( Severity.ERROR );
    assertThat( validationStatus.getMessage() ).isEqualTo( message );
  }

  private void createProject( String name ) {
    IProject result = mock( IProject.class );
    when( result.getName() ).thenReturn( name );
    projectsProvider.addProject( result );
  }

  private void installJdt() {
    when( jdtFeature.isInstalled() ).thenReturn( true );
  }

}
