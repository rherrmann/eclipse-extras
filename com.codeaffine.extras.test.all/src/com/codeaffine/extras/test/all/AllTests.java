package com.codeaffine.extras.test.all;

import org.junit.runner.RunWith;

import com.codeaffine.osgi.testuite.BundleTestSuite;
import com.codeaffine.osgi.testuite.BundleTestSuite.TestBundles;

@RunWith(BundleTestSuite.class)
@TestBundles({
  "com.codeaffine.extras.ide",
  "com.codeaffine.extras.imageviewer",
  "com.codeaffine.extras.jdt",
  "com.codeaffine.extras.launch"
})
public class AllTests {
}
