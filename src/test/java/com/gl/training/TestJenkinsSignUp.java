package com.gl.training;

import com.gl.training.pages.JenkinsStartPage;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestJenkinsSignUp extends BaseTest{

    static JenkinsStartPage startPage;

    @BeforeClass
    public static void beforeTestClass(){
        startPage = new JenkinsStartPage(getWdInstance());
    }

    @Test
    public void testSignUpButton(){
        startPage.openSignUpForm();
        startPage.verifySignUpFormLayout();
        startPage.navigateToStartPage();
    }
}
