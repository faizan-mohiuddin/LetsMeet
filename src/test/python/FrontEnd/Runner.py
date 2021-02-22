from selenium import webdriver
import time


class Runner():
    def __init__(self):
        self.driver = webdriver.Firefox()
        self.address = "http://localhost:8080/"
        self.driver.get(self.address)

    def close(self):
        self.driver.quit()


################
tester = Runner()
time.sleep(5)
tester.close()
