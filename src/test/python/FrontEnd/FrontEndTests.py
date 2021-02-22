import unittest
from Runner import *
from TestingMethods import *


class FrontEndTests(unittest.TestCase):
    def setUp(self):
        self.runner = Runner()

    def testHomePage(self):
        heading = self.runner.driver.find_element_by_tag_name('h1').get_attribute("innerHTML")
        #                (Expected result, actual result, Message)
        self.assertEqual("Welcome to LetsMeet", heading, "Testing if title is on page")

    def tearDown(self):
        self.runner.close()


if __name__ == '__main__':
    unittest.main()
