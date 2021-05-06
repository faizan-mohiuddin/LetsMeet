from selenium import webdriver
from selenium.webdriver.chrome.options import Options
import time


class Runner():
    def __init__(self):
        chrome_options = Options()
        chrome_options.add_argument("--window-size=1920,1080")
        self.driver = webdriver.Chrome(chrome_options=chrome_options)
        self.address = "http://localhost:8080/"
        self.driver.get(self.address)

    def close(self):
        self.driver.quit()


################
tester = Runner()
tester.close()
