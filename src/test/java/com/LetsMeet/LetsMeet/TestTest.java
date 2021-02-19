package com.LetsMeet.LetsMeet;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class TestTest {

    private WebDriver browser;

    @Before
    public void setup() {
        browser = new FirefoxDriver();
    }

    @Test
    public void startTest() {
        browser.get("http://localhost:8080/Home");
        assertEquals("John", browser.findElement(By.id("firstName")).getAttribute("value"));
    }

    @After
    public void tearDown() {
        browser.close();
    }
}