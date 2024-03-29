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

    def createAccount(self, fName, lName, email, password):
        payload = {"fName": fName, "lName": lName, "email": email, "password": password}
        r = requests.post(self.address + "/User", params=payload)
        print(r.text)

    def joinEvent(self, uuid):
        payload = {"Token": self.token}
        r = requests.put(self.address + "Event/" + str(uuid), params=payload)
        print(r.text)

    def deleteAccount(self):
        payload = {"Token": self.token}
        r = requests.delete(self.address + "User", params=payload)
        print(r.text)

    def searchByRadius(self):
        latitude = 57.691
        longitude = -2.010
        payload = {"Longitude": longitude, "Latitude": latitude, "Radius": 1}
        r = requests.get(self.address + "Venue/Search/Radius", params=payload)
        print(r.text)


########################################################################################################################
NoUser = LetsMeetAPITesting()

UserOne = LetsMeetAPITesting("caelmilne2001@gmail.com", "testing")
UserTwo = LetsMeetAPITesting("caelmilne@gmail.com", "testing 2")

# Test 1 #########################################################################
# print("\nTest 1")
# Testing creating, joining, and deleting events
# UserOne.login()
# UserOne.createEvent("Caels API test", "API Testing", "The Broch")
# events = UserOne.getMyEvents()
# UserOne.deleteEvent(events[0]["uuid"])
# UserTwo.login()
# UserTwo.createEvent("Second Test", "Testing", "The Broch")
# UserTwo.joinEvent(events[0]["uuid"])
# events2 = UserTwo.getMyEvents()
##################################################################################
# Test 2 #########################################################################
# Testing creating and deleting users
# print("\nTest 2")
# UserThree = LetsMeetAPITesting()
# UserThree.createAccount("Random", "Test", "random@testing.com", "Testing")
#
# UserThree.setEmailandPassword("random@testing.com", "Testing")
# UserThree.login()
# UserThree.deleteAccount()
##################################################################################
# Test 3 #########################################################################
# Testing token verification
# print("\nTest 3")
# UserThree = LetsMeetAPITesting()
# # UserThree.createAccount("Random", "Test", "random@testing.com", "Testing")
#
# UserThree.setEmailandPassword("random@testing.com", "Testing")
# UserThree.login()
# # UserThree.token += "Wrong"
# UserThree.deleteAccount()
##################################################################################
# Test 4 ########################################################################
# Testing gathering users events
# UserOne.login()
# UserOne.joinEvent("99e3f78d-7246-3010-a808-4ee4543cc928")
# UserOne.getMyEvents()
#################################################################################
# Test 5 ########################################################################
# Searching by radius
NoUser.searchByRadius()