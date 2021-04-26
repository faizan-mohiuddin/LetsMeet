import unittest
from Runner import *
from TestingMethods import *


class FrontEndTests(unittest.TestCase):
    def setUp(self):
        self.runner = Runner()

    def testHomePage(self):

        # ---- FIRST CARD ON HOME PAGE: START ----

        firstHeader = self.runner.driver.find_element_by_id("firstHeader").get_attribute("innerHTML")
        #                (Expected result, actual result, Message)
        self.assertEqual("An easier way to <b>Meet.</b>", firstHeader, "TEST: First header contents.")

        firstSubheader = self.runner.driver.find_element_by_id("firstSubheader").get_attribute("innerHTML")

        self.assertEqual("Accommodate <i>everyone</i>", firstSubheader, "TEST: First subheader contents.")

        firstSubheaderParagraph = self.runner.driver.find_element_by_id("firstParagraph").get_attribute("innerHTML")

        self.assertEqual("With LetsMeet, participants in your Meet can seamlessly say what times work best for them and can specify other conditions they would like met in the Meet.", firstSubheaderParagraph, "TEST: First paragraph contents.")

        secondSubheader = self.runner.driver.find_element_by_id("secondSubheader").get_attribute("innerHTML")

        self.assertEqual("Remain <i>flexible</i>", secondSubheader, "TEST: Second subheader contents.")

        secondSubheaderParagraph = self.runner.driver.find_element_by_id("secondParagraph").get_attribute("innerHTML")

        self.assertEqual("We take care of the hard work. Our scheduling algorithm will determine the optimum times for your Meet to take place within a time range.", secondSubheaderParagraph, "TEST: Second subheader paragraph contents.")

        # ---- FIRST CARD ON HOME PAGE: END ----
        # ---- SECOND CARD ON HOME PAGE: START ----

        secondHeader = self.runner.driver.find_element_by_id("secondHeader").get_attribute("innerHTML")

        self.assertEqual("Built for <b>Business.</b>", secondHeader, "TEST: Second header contents.")

        thirdSubheader = self.runner.driver.find_element_by_id("thirdSubheader").get_attribute("innerHTML")

        self.assertEqual("Be <i>visible</i>", thirdSubheader, "TEST: Third subheader contents.")

        thirdParagraph = self.runner.driver.find_element_by_id("thirdParagraph").get_attribute("innerHTML")

        self.assertEqual("Register your business with LetsMeet for more exposure and recognisability for your brand.", thirdParagraph, "TEST: Third paragraph contents.")

        fourthSubheader = self.runner.driver.find_element_by_id("fourthSubheader").get_attribute("innerHTML")

        self.assertEqual("Designed for <i>integration</i>", fourthSubheader, "TEST: Fourth subheader contents.")

        fourthParagraph = self.runner.driver.find_element_by_id("fourthParagraph").get_attribute("innerHTML")

        self.assertEqual("Power user? You can utilise LetsMeet's API capabilities to incorporate in your own application.", fourthParagraph, "TEST: Fourth subheader contents.")

        # ---- SECOND CARD ON HOME PAGE: END ----

    def testLoginAndLogout(self):

        self.runner.driver.get("http://localhost:8080/login")

        username = self.runner.driver.find_elements_by_xpath('//*[@id="content"]/div[3]/div/div/form/div[1]/input')[0]
        username.send_keys("123@123.com")

        password = self.runner.driver.find_elements_by_xpath('//*[@id="content"]/div[3]/div/div/form/div[2]/input')[0]
        password.send_keys("123")

        loginButton = self.runner.driver.find_elements_by_xpath('//*[@id="content"]/div[3]/div/div/form/button')[0]
        loginButton.click()

        successLoginMessage = self.runner.driver.find_elements_by_xpath('//*[@id="content"]/div[3]/div[1]/div/div')[0].get_attribute("innerHTML")
        self.assertEqual('<i class="bi bi-check-circle-fill"></i><a> You have logged in as Faizan Mohiuddin</a> ', successLoginMessage, "TEST: Login message after logging in.")

        # LOGGING OUT START

        userButton = self.runner.driver.find_elements_by_xpath('//*[@id="dropdownMenuButton2"]')[0]
        userButton.click()

        logoutButton = self.runner.driver.find_elements_by_xpath('//*[@id="navbarSupportedContent"]/ul/li[3]/div/ul/li[4]/a')[0]
        logoutButton.click()

        logoutMessage = self.runner.driver.find_elements_by_xpath('//*[@id="content"]/div[3]/div[1]/div/div')[0].get_attribute("innerHTML")
        self.assertEqual('<i class="bi bi-check-circle-fill"></i><a> You have successfully logged out.</a> ', logoutMessage, "TEST: Logout message after logging out.")

    def failedLoginAttempt(self):

        self.runner.driver.get("http://localhost:8080/login")

        username = self.runner.driver.find_elements_by_xpath('//*[@id="content"]/div[3]/div/div/form/div[1]/input')[0]
        username.send_keys("123@123.commm")

        password = self.runner.driver.find_elements_by_xpath('//*[@id="content"]/div[3]/div/div/form/div[2]/input')[0]
        password.send_keys("123")

        loginButton = self.runner.driver.find_elements_by_xpath('//*[@id="content"]/div[3]/div/div/form/button')[0]
        loginButton.click()

        failedLoginMessage = self.runner.driver.find_elements_by_xpath('//*[@id="content"]/div[3]/div[1]/div/div')[0].get_attribute("innerHTML")
        self.assertEqual('<test ', failedLoginMessage, "TEST: Login message after failed logging in.")


    def tearDown(self):
        self.runner.close()


if __name__ == '__main__':
    unittest.main()
