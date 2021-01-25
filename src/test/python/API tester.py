import requests
import json


class LetsMeetAPITesting:
    def __init__(self, email=None, password=None):
        self.address = "http://localhost:8080/api/"
        self.email = email
        self.password = password
        self.token = None

    def get_all_users(self):
        pass

    def get_all_event(self):
        pass

    def getMyEvents(self):
        payload = {"Token": self.token}
        r = requests.get(self.address + "MyEvents", params=payload)
        print(r.text)
        return json.loads(r.text)

    def createEvent(self, name, desc, location):
        payload = {"Name": name, "Desc": desc, "Location": location, "Token": self.token}
        r = requests.post(self.address + "Event", params=payload)
        print(r.text)

    def deleteEvent(self, EventUUID):
        payload = {"Token": self.token}
        r = requests.delete(self.address + "Event/" + str(EventUUID), params=payload)
        print(r.text)

    def login(self):
        payload = {'email': self.email, 'password': self.password}
        r = requests.post(self.address + "login", params=payload)
        self.token = r.text
        print(r.text)

    def setEmailandPassword(self, email, password):
        self.email = email
        self.password = password

    def createAccount(self):
        pass

    def joinEvent(self, uuid):
        payload = {"Token": self.token}
        r = requests.put(self.address + "Event/" + str(uuid), params=payload)
        print(r.text)


########################################################################################################################
NoUser = LetsMeetAPITesting()

UserOne = LetsMeetAPITesting("caelmilne2001@gmail.com", "testing")
UserTwo = LetsMeetAPITesting("caelmilne@gmail.com", "testing 2")

# Test 1 #########################################################################
UserOne.login()
#UserOne.createEvent("Caels API test", "API Testing", "The Broch")
events = UserOne.getMyEvents()
#UserOne.deleteEvent(events[0]["uuid"])
UserTwo.login()
#UserTwo.createEvent("Second Test", "Testing", "The Broch")
UserTwo.joinEvent(events[0]["uuid"])
events2 = UserTwo.getMyEvents()
##################################################################################
# Test 2 #########################################################################

##################################################################################
