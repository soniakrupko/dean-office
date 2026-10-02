# Лабораторна №6 — тестовий сценарій (Spring Data JPA)

Swagger UI: http://localhost:8080/swagger-ui.html
Таблиці й дані ті самі, що в лабі 5 (sql/setup.sql). Якщо тести лаби 5 змінили дані, виконайте скрипт очищення з інструкції і setup.sql заново.

SQL-запити Hibernate видно в консолі Eclipse (spring.jpa.show-sql=true).

## Прості операції
GET /api/students; ?group=ІК-21; ?surname=Шевченко; ?group=ІК-21&surname=Мельник; ?page=0&size=2
POST {"surname":"Іваненко","name":"Петро","groupName":"ІК-21"} -> 201
GET /{id}; PUT; PATCH {"groupName":"ІК-25"}; DELETE -> 204; неіснуючий id -> 404; неіснуюча група -> 400

## Методи пошуку (вимога 5)
- @Query (JPQL):         GET /api/students/search?part=ко ; GET /api/groups/{id}/students
- @NamedQuery:           GET /api/groups/with-free-seats
- Назва методу (Spring Data): GET /api/students?group=ІК-21 ; GET /api/students/first-three ; GET /api/groups/by-name/ІК-21

## Транзакції
1. Відкат: POST /api/groups/2/transfer-students?toGroupId=3 -> 400; GET /api/groups/2/students — студенти на місці
2. Успіх:  POST /api/groups/2/transfer-students?toGroupId=4 -> 200 {"moved":2,"totalInTarget":3}
3. POST /api/groups/with-students?transactional=true
   {"name":"ІК-26","capacity":5,"students":[{"surname":"Сидоренко","name":"Петро"},{"surname":"","name":"Анна"}]}
   -> 400, групи ІК-26 немає
4. Те саме з ?transactional=false -> 400, але ІК-26 і Сидоренко залишились
5. DELETE /api/groups/1 -> 409 (у групі є студенти)
