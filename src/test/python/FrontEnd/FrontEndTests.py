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

    def testLogin(self):


        loginButton = self.runner.driver.find_elements_by_xpath('/html/body/div[3]/div[2]/div[1]/div/nav/div/div/ul/li[2]/button')[0]
        loginButton.click()

        username = self.runner.driver.find_elements_by_xpath('//*[@id="myModal"]/div/div/div[2]/div/div/form/div[1]/input')
        username.send_keys("123@123.com")
        password = self.runner.driver.find_elements_by_xpath('//*[@id="myModal"]/div/div/div[2]/div/div/form/div[2]/input')
        password.send_keys("123")

        submitLoginButton = self.runner.driver.find_elements_by_xpath('//*[@id="myModal"]/div/div/div[2]/div/div/form/button')[0]

        submitLoginButton.click()

        loginSuccessMessage = self.runner.driver.find_element_by_class_name('alert alert-success').get_attribute("innerHTML")

        self.assertEqual("something", loginSuccessMessage, "TEST: Login alert success")

    def tearDown(self):
        self.runner.close()


if __name__ == '__main__':
    unittest.main()
