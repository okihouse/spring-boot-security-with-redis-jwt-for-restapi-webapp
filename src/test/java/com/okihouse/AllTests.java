package com.okihouse;

import com.okihouse.api.ApiWebAppSecurityTests;
import com.okihouse.web.WebFormSecurityTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    ApiWebAppSecurityTests.class,
    WebFormSecurityTests.class
})
public class AllTests {

}
