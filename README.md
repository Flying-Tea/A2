# A2

FlyRank Part 2 assignment that naturally continues from the first one. Instead of using an in-memory array, you will replace it with a real database while keeping exactly the same API. This reinforces the idea that persistence is an implementation detail behind the API, not a change to the API itself.

![Screenshot of SQLite Database](/a2/images/Screenshot_35.png)

## TechStack

- Java Maven
- SpringBoot
- SQLite
- JDBC (API)

## Features

- GET / - API information
- GET /health - Health checks

- Task Database features
  - GET /tasks         | lists all tasks
  - /tasks/{id}        | for individual tasks
  - POST /tasks        | creates a task
  - DELETE /tasks/{id} | Removes a task
  - PUT /tasks/{id}    | Updates a task
