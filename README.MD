# Let's Meet

[![Let's Meet: demo video](http://img.youtube.com/vi/dVIA8e39oGY/0.jpg)](http://www.youtube.com/watch?v=dVIA8e39oGY "Let's Meet")
Click to watch a video introduction to Let's Meet on YouTube.

## Installation

### Requirements

LetMeet Server has been tested on RHEL, Ubuntu Server 18.01 LTS and Windows 10.

- Java JDK 11+
- MySQL

### Building and Installation

#### Database Setup

A mySQL Database is required and should be initialised using the included SQL script – &quot;data/database-schema.sql&quot;

LetsMeet requires SELECT, INSERT, UPDATE and DELETE permissions. It is strongly recommended to create a dedicated user for the LetsMeet application and its database with only these privileges granted.

#### Building the LetsMeet Server

LetsMeet can be built from source with ease using the included apache maven wrapper. To ensure version consistency across deployments, we recommend using the included maven wrapper rather than your own local installation although both are valid. The former shall be demonstrated here.

Prerequisites to launching LetsMeet are having Java installed and having Maven installed.

**Running on Linux**

To install Java (Linux):

1. `$ sudo apt update`
2. `$ java -version` (LetsMeet requires Java version 11+)
3. `$ sudo apt install`  **default-jre**
4. After JRE is installed, run `$ sudo apt install default-jdk` (to install JDK)
5. `$ java -version` and `$ javac -version` to check if both were installed

To install Maven (Linux):

 `$ sudo apt install maven`

If the error &quot;waiting for cache lock: Could not get lock \&lt;some file path\&gt;&quot; is encountered at any point, stop the process and then run the command `$ sudo rm \&lt;some file path\&gt;` then continue or follow the instructions given [here](https://unix.stackexchange.com/questions/374748/ubuntu-update-error-waiting-for-unattended-upgr-to-exit).

To build LetsMeet simply run: `$ mvn install all` or `mvnw install all` if you wish to use the included maven wrapper. 

If the above command yields an error, try: `$ mvn clean install` or `$ mvnw clean install`

This will download all required dependencies, run tests, and build LetsMeet as a JAR within the /target folder. It is possible to skip tests by appending **DSkipTests** to the installation command.

In order to successfully start the LetsMeet server you will need to insert an application.properties file to the LetsMeet/src/resourses folder

If the build is successful, you can run LetsMeet:

`$ Java -jar \path-to-your-generated-jar\`

To run the application run the command: `$ mvn spring-boot:run` which should run the website after a small while. Finally, open your browser and navigate to `localhost:8080/Home` to view the website.

Gracefully shutting down the server in the terminal can be done with Ctrl + C.

**Running on Windows**

1. Ensure that Java and its components (JDK, JRE, etc.) are installed and configured properly on your PC.
2. In your terminal, navigate to the LetsMeet directory (/LetsMeet) where the src, libs, etc folders and where the mvnw, pom, etc files are located.
3. Run the command `mvnw clean install`. Once that has completed run the command `mvnw spring-boot:run` to launch the application.
4. After a small while, the application should be running. Then, on your browser, head to `localhost:8080/Home` to view the website.

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
