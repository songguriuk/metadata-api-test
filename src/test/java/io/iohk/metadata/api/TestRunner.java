package io.iohk.metadata.api;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        dryRun = false,
        plugin = {
                "html:build/test-results/cucumber-reports/index.html"
        },
        glue = "io.iohk.metadata.api",
        features = "src/test/resources/Scenarios.feature"
)
public class TestRunner {
}
