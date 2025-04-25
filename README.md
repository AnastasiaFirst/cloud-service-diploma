# Дипломная работа «Облачное хранилище»

## Описание проекта

Задача — разработать REST-сервис. Сервис должен предоставить REST-интерфейс для загрузки файлов и вывода списка уже загруженных файлов пользователя. 

Все запросы к сервису должны быть авторизованы. Заранее подготовленное веб-приложение (FRONT) должно подключаться к разработанному сервису без доработок, 
а также использовать функционал FRONT для авторизации, загрузки и вывода списка файлов пользователя.

## Требования к приложению

- Сервис должен предоставлять REST-интерфейс для интеграции с FRONT.
- Сервис должен реализовывать все методы, описанные в [yaml-файле](./CloudServiceSpecification.yaml):
  1. Вывод списка файлов.
  2. Добавление файла.
  3. Удаление файла.
  4. Авторизация.
- Все настройки должны вычитываться из файла настроек (yml).
- Информация о пользователях сервиса (логины для авторизации) и данные должны храниться в базе данных (на выбор студента).

## Требования к реализации

- Приложение разработано с использованием Spring Boot.
- Использован сборщик пакетов gradle/maven.
- Для запуска используется docker, docker-compose.
- Код размещён на Github.
- Код покрыт unit-тестами с использованием mockito.
- Добавлены интеграционные тесты с использованием testcontainers.

## Шаги реализации

- Изучите протокол получения и отправки сообщений между FRONT и BACKEND.
- Нарисуйте схему приложений.
- Опишите архитектуру приложения, где хранятся настройки и большие файлы, структуры таблиц/коллекций базы данных.
- Создайте репозиторий проекта на Github.
- Напишите приложение с использованием Spring Boot.
- Протестируйте приложение с помощью curl/postman.
- Протестируйте приложение с FRONT.
- Напишите README.md к проекту.
- Отправьте на проверку.

## Технологический стек/Условия работы приложения

- Java 17 и выше
- Spring Boot 3 и выше
- Spring Security (JWT-token 0.12)
- Postgres 16 и выше
- Docker & Docker Compose
- Maven
- Node.js(version 19.7.0-21.0) & npm (for frontend)

## REST API
### Сервис авторизации
- **POST /login**
  - **Description:** Вход по логину и паролю.
    - **cURL:**
      ```
      curl -X POST http://localhost:8080/cloud/login \
      -H "Content-Type: application/json" \
      -d '{
              "login": "your_login",
              "password": "your_password_hash"
          }'
      ```
    - **Responses:**
      - HTTP 200 Пользователь <login> успешно авторизован.
        ```
        {
            "auth-token": "string"
        }
        ```
      - HTTP 400 Неправильный пароль
- **POST /logout**
  - **Description:** Выход из учетной записи пользователя, используя токен.
    - **cURL:**
      ```
      curl -X POST http://localhost:8080/cloud/logout \
      -H "Content-Type: application/json" \
      -H "auth-token: your_auth_token"
      ```
    - **Responses:**
      - HTTP 200 OK.
### Сервис управления файлами пользователя
- **POST /file**
  - **Description:** Добавление нового файла
    - **cURL:**
      ```
      curl -X POST http://localhost:8080/cloud/file?filename=your_file_name.txt \
      -H "auth-token: your_auth_token" \
      -F "hash=your_file_hash" \
      -F "file=@/path/to/your/file.txt"
      ```
    - **Responses:**
      - HTTP 200 Файл с именем <fileName> успешно добавлен.
      - HTTP 400 Файл с таким именем: <fileName> уже существует для <userID>
      - HTTP 401 Пользователь <userID> не авторизован.
- **GET /file**
  - **Description:** Загрузка файла с сервера
    - **cURL:**
      ```
      curl -X GET http://localhost:8080/cloud/file?filename=your_filename \
      -H "auth-token: your_auth_token"
      ```
    - **Responses:**
      - HTTP 200 Ok.
      - HTTP 400 Файл с именем: <fileName> не найден
      - HTTP 401 Пользователь <userID> не авторизован.
      - HTTP 500 Произошла ошибка при обработке запроса. Пожалуйста, попробуйте позже.
- **PUT /file**
  - **Description:** Изменение имени файла
    - **cURL:**
      ```
      curl -X PUT http://localhost:8080/cloud/file?filename=your_filename \
      -H "Content-Type: application/json" \
      -H "auth-token: your_auth_token" \
      -d '{
          "name": "new_file_name"
          }'
      ```
    - **Responses:**
      - HTTP 200 У файла с именем <fileName> успешно изменено имя файла на <newFileName>.
      - HTTP 400 Некорректные входные данные: имя файла не может быть пустым
      - HTTP 401 Пользователь <userID> не авторизован.
      - HTTP 500 Файл с таким именем: <fileName> уже существует.
- **DELETE /file**
  - **Description:** Удаление файла
    - **cURL:**
      ```
      curl -X DELETE http://localhost:8080/cloud/file?filename=your_filename \
      -H "auth-token: your_auth_token"
      ```
    - **Responses:**
      - HTTP 200 Файл с именем: <fileName> успешно удален.
      - HTTP 400 Файл с именем: <fileName> не найден
      - HTTP 401 Пользователь <userID> не авторизован.
      - HTTP 500 Не удалось удалить файл с именем: <fileName>
**GET /list**
  - **Description:** Получение списка файлов
    - **cURL:**
      ```
      curl -X GET http://localhost:8080/cloud/list?limit=10 \
      -H "auth-token: your_auth_token"
      ```
    - **Responses:**
      - HTTP 200 Ok.
      - HTTP 400 Лимит должен быть больше нуля.
      - HTTP 401 Пользователь <userID> не авторизован.
      - HTTP 500 Не удалось получить список файлов.
