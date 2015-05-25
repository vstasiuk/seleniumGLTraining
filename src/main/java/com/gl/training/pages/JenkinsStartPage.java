package com.gl.training.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import static junit.framework.Assert.assertTrue;

public class JenkinsStartPage extends BasePage {

//    private final static String TARGET_URL = getPageURL();

    public JenkinsStartPage(WebDriver wd) {
        super(wd);
        load();
    }

    @FindBy(id = "jenkins-home-link")
    private WebElement jenkinsLogo;

    @FindBy(css = ".tabBar")
    private WebElement tabBar;

    @FindBy(id = "username")
    private WebElement username;

    @FindBy(name = "password1")
    private WebElement password;

    @FindBy(name = "password2")
    private WebElement passwordVerify;

    @FindBy(name = "fullname")
    private WebElement fullname;

    @FindBy(name = "email")
    private WebElement email;

    @FindBy(css = "a[href='/signup']")
    private WebElement buttonSignUp;


    public JenkinsStartPage verifySignUpFormLayout(){
        assertTrue(isElementPresent(username));
        assertTrue(isElementPresent(password));
        assertTrue(isElementPresent(passwordVerify));
        assertTrue(isElementPresent(fullname));
        assertTrue(isElementPresent(email));
        return this;
    }

    public JenkinsStartPage openSignUpForm(){
        click(buttonSignUp);
        waitForVisibility(username);
        return this;
    }

    public JenkinsStartPage navigateToStartPage(){
        click(jenkinsLogo);
        waitForVisibility(tabBar);
        return this;
    }
}
