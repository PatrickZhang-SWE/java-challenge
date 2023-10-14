### How to use this spring-boot project

- Install packages with `mvn package`
- Run `mvn spring-boot:run` for starting the application (or use your IDE)

Application (with the embedded H2 database) is ready to be used ! You can access the url below for testing it :

- Swagger UI : http://localhost:8080/swagger-ui.html
- H2 UI : http://localhost:8080/h2-console

> Don't forget to set the `JDBC URL` value as `jdbc:h2:mem:testdb` for H2 UI.


### My experience in Java

- I know Spring Boot very well and have been using it for more than 3 years

### What I have done

- upgrade version of springboot to 2.4.0 since there are some vulnerabilities.
- remove some unnecessary codes.
- fix some bugs.
- add cache logics.
- add authorization and authentication
- add validations
- add test cases.
- add log configuration.

### Would have done

 - add a real cache layer and use AOP to do cache.
 - add a well-defined user manage system.
