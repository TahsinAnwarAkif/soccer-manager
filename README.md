# Soccer Manager Game

## Table of Contents
* [General info](#general-info)
* [Technologies](#technologies)
* [Instructions on Using Application](#instructions-on-using-application)
* [Notable Features](#notable-features)

## General Info
An online **Soccer Manager** game where users can create fantasy teams and will be able to sell/buy players among them.

## Technologies
* Java 1.8
* Spring Boot 2.6.4
* MySQL 5

## Instructions on Using Application
1. Install **Java 1.8** & **MySQL 5**.
2. Create a Database named _soccer_manager_ in **MySQL**. Hence, connection to _localhost:3306/soccer_manager_ should be established.
3. Clone the _feature/soccer-manager-api_ branch. Go to the project directory via terminal and run this command in project directory: **./gradlew clean build -x test bootRun**
4. Go To _localhost:8080/swagger-ui.html_ to find all the APIs. 
5. Use the **signup** API first to create a user, then you can generate a JWT. With that JWT as authentication header, you can perform other authenticated requests. 

## Notable Features
1. **Authentication** using industry-standard **JWT** mechanism is performed.
2. JWT validity is authenticated in each request _except signin & signup_. 
3. JWT validity is set to **15 minutes**. Hence, after this time, user needs to authenticate again.
4. **API versioning** is done throughout the requests.
5. Swagger is used to enlist all the API Documentations in an effective way.
6. **Uniqueness** of each **username** or **email** is ensured.
7. A team, 20 players are automatically generated through a valid user signup request.
8. User can see only his/her **own** team/players. This is done through JWT, also without any path variable in the URL.
9. **DTOs** for all POST/PUT requests is used in order to avoid **invalid/sensitive** inputs _(e.g. id)_.
10. **Field Level Validations** are properly performed (e.g. Field max/min length specified in swagger-doc, proper username/email pattern for respective fields, transfer minimum asking price, 1 etc)
11. **Audit Info** like creation date, update date etc are maintained.
12. **Concurrent Update Prevention** is performed through keeping a **version** field in each of **Team/User/Player/Transfer** 
13. Showing **Team Players/Transfers** API call can be performed using **limit** and/or **offset** request params optionally, which will limit the result rows. For example, If these API calls are performed with **limit = 10** and **offset = 11**, a total of 10 rows will be fetched in the result from the 11th result row. However, user can also get all the rows simply without mentioning these request params.
14. **JPA L2 Caching** is performed for **Team/User/Player/Transfer**, which reduces repetitive SELECT DB queries (for 5 minutes) once such object is fetched.
15. **Spring Caching** is performed for **Transfer List**, since it should be the most frequently accessed method. Invalidating this cache in proper time is also performed. 
16. APIs returning the **Players'/Transfers' Total Count** only is provided.
17. User can update only his/her own team's player(s).
18. Inputting only valid country names in **Team/Player Update** APIs is also ensured.
19. Adding a player in **Transfer List** is possible only when:
    1. The player exists in his/her own team
    2. The player is **not present** in the **Transfer List**
20. Applying a transfer from **Transfer List** is possible only when
    1. The player is present in **Transfer List** and s/he is **not** currently in user's own team 
    2. The player's **asking price** for transfer is **not greater** than the user's **team-budget**
21. After every successful transfer execution, the following things are updated:
    1. Source/Destination Team: Player List, Budget, Value (Sum of Players)
    2. Transferred Player: Value (Increased by 10~100 percent of the asking price)
    3. Transfer List
22. **End-to-End Testing** for each and every handler method is performed with/without authentication.
23. **Around 85%** of **Code Base Coverage** is achieved in the written test codes.
