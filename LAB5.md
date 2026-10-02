# Лабораторна №5 — тестовий сценарій

Swagger UI: http://localhost:8080/swagger-ui.html
База: Microsoft SQL Server, скрипт sql/setup.sql виконується в SSMS.
Початкові дані: ІК-21 (3 студ., ліміт 25), ІК-22 (2, ліміт 25), ІК-23 (2, ліміт 3), ІК-24 (1, ліміт 30), ІК-25 (0, ліміт 30). id груп 1–5.

Перемикання реалізації DAO: `app.dao.type=client` (JdbcClient) або `template` (JdbcTemplate) у application.properties.

## CRUD студентів
GET /api/students; ?group=ІК-21; ?surname=Шевченко; ?page=0&size=2; POST {"surname":"Іваненко","name":"Петро","groupName":"ІК-21"} -> 201;
PUT /api/students/{id}; PATCH {"groupName":"ІК-25"} -> 200; DELETE -> 204; неіснуючий id -> 404; неіснуюча група -> 400.

## Транзакції
1. Відкат: POST /api/groups/2/transfer-students?toGroupId=3 -> 400 (2+2=4 > 3). GET /api/groups/2/students — студенти досі в ІК-22.
2. Успіх: POST /api/groups/2/transfer-students?toGroupId=4 -> 200 {"moved":2,"totalInTarget":3}.
3. Відкат при створенні (transactional=true), POST /api/groups/with-students:
   {"name":"ІК-26","capacity":5,"students":[{"surname":"Сидоренко","name":"Петро"},{"surname":"","name":"Анна"}]}
   -> 400; GET /api/groups — групи ІК-26 немає.
4. Той самий запит із ?transactional=false -> 400, але ІК-26 і Сидоренко залишились (відкату немає).
   Перед повтором видаліть їх або змініть назву.
5. Успіх: те саме тіло, де в обох студентів є прізвище -> 201.
6. DELETE /api/groups/1 (є студенти) -> 409.
