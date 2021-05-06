# Let's Meet

[![Let's Meet: demo video](http://img.youtube.com/vi/dVIA8e39oGY/0.jpg)](http://www.youtube.com/watch?v=dVIA8e39oGY "Let's Meet")

## Architecture

Let's Meet is divided into two main packages, `User` and `Event`. 

Each package has 4 core sub-packages organized as layers:

- `Model` Defines the data structures used.
- `Controller` receives API requests and maps them to internal `services` function calls. Where necessary it should perform the translation of request parameters into `Model` objects. There is a separate controller for API and Web methods.
- `Service` implements the business logic. Called from the above `Controller` layer, this layer should work exclusively with `Model` classes.
- `DAO` performs CRUD (Create, Read, Update, Delete) operations on the persistent storage. The Service layer calls this to access data from the database.

### User

The `User` package implements the user management system. It has the following key components:

- `User` A user object
- `UserSanitized` As above, but with unnecessary data stripped. This is ideal for returning to the front-end.
- `Token` A user's token

### Event
 The `Event` package implements the event management system. It has the following key components:

 - `Event` An event. Contained all event specific metadata.
 - `Permission` Links a user to an event with a set of permissions. Currently this simply tracks whether they are the owner or not.
 - `Response` Links a User to an Event through a response. This contains a `ConditionSet` and a set of 'PollResponse' objects - however these are currently not implemented

**ConditionSet**
 - `ConditionSet` Has a set of `Variable`s and `Constraint`s. 
 - `Variable` Defines the range of values around the named domain.
 - `Constraints` Defines a relationship between two `variable`s of the same domain data type.

 **Poll**
 - `Poll` defines the options in a poll
 - `PollResponse` The users response to each option in a poll
 

 Note that the `Event` itself is completely decoupled from any `User`. This is intentional and should remain that way.

---

 ## Programming best practices
 When contributing to the project, please bear in mind:
 
 - Each tier should only communicate with that directly above and below it. From top to bottom that is Controller --> Service --> DAO
 - The DAO (Data Access Object) should only implement CRUD operations. If you find yourself needing to write a non-crud op in the DAO then take a step back and rethink your approach. Generally the DAO should instantiate a `Model` object to be manipulated in the Service layer. While it is okay to provide additional DAO methods than those in the DAO interface consider splitting into multiple DAO objects if you are writing many of them.


 ### Last updated: 12 Feb (added details for Event package)
