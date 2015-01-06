package com.codeaffine.extras.jdt.test;

import org.junit.runner.RunWith;

import com.codeaffine.osgi.testuite.BundleTestSuite;
import com.codeaffine.osgi.testuite.BundleTestSuite.TestBundles;

@RunWith(BundleTestSuite.class)
@TestBundles( {
  "com.codeaffine.extras.jdt",
} )
public class AllTests {
}
