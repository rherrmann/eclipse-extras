package com.codeaffine.extras.test.all;

import org.junit.runner.RunWith;

import com.codeaffine.osgi.testuite.BundleTestSuite;
import com.codeaffine.osgi.testuite.BundleTestSuite.TestBundles;

@RunWith(BundleTestSuite.class)
@TestBundles( {
  "com.codeaffine.extras.platform",
  "com.codeaffine.extras.jdt",
} )
public class AllTests {
}
