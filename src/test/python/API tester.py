import requests


class LetsMeetAPITesting:
    def __init__(self):
        self.address = "http://localhost:8080/api/"

    def get_all_users(self):
        pass

    def get_all_event(self):
        pass

    def insert_user(self):
        payload = {}
        r = requests.post(self.address + "", params=payload)
        print(r.text)

    def insert_event(self, name, desc, location):
        payload = {'Name': name, 'Desc': desc, 'Location': location}
        r = requests.post(self.address + "Event", params=payload)
        print(r.text)


########################################################################################################################
tester = LetsMeetAPITesting()

tester.insert_event("Caels Test Event", "Testing testing", "The Broch")
