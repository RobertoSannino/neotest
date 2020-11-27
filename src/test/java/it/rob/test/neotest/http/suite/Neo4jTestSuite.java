package it.rob.test.neotest.http.suite;

import it.rob.test.neotest.http.elastic.UfficiTest;
import it.rob.test.neotest.http.neo4j.PfTest;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;


@RunWith(JUnitPlatform.class)
@SuiteDisplayName("JUnit Platform Suite Demo")
@SelectPackages("it.rob.test.neotest.http.neo4j")
public class Neo4jTestSuite {
}
