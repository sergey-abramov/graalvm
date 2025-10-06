# Serverless Authentication App с GraalVM и AWS Lambda

Простое приложение с аутентификацией/авторизацией, развернутое на AWS Lambda с использованием GraalVM native image для быстрых холодных стартов.

## Особенности

- **Serverless архитектура** - работает на AWS Lambda
- **GraalVM Native Image** - быстрые холодные старты (~100ms)
- **Dual Authentication** - Email/Password + Google OAuth2
- **Spring Boot 3.x** - с AOT (Ahead-of-Time) компиляцией
- **Минималистичный UI** - Bootstrap + Thymeleaf
- **CI/CD** - автоматический деплой через GitHub Actions
- **Free Tier** - укладывается в бесплатный тариф AWS

## Требования

- Java 17+
- Maven 3.8+
- **Docker Desktop** (для сборки native image)
- AWS CLI настроенная
- SAM CLI
- Google Cloud Console аккаунт

## Локальная разработка

### Клонирование и настройка

```bash
git clone <your-repo-url>
cd graalvm
```

### Настройка Google OAuth2

1. Перейдите в [Google Cloud Console](https://console.cloud.google.com/)
2. Создайте новый проект или выберите существующий
3. Включите Google+ API
4. Перейдите в "APIs & Services" > "Credentials"
5. Создайте "OAuth 2.0 Client IDs":
   - Application type: Web application
   - Name: Serverless Auth App
   - Authorized redirect URIs:
     - `http://localhost:8080/login/oauth2/code/google` (для локальной разработки)
     - `https://your-api-gateway-url/login/oauth2/code/google` (для продакшна)

6. Скопируйте Client ID и Client Secret
### 3. Настройка OAuth2 в application.properties

Откройте файл `src/main/resources/application.properties` и замените placeholder значения:

```properties
# Замените эти значения на ваши реальные Google OAuth2 credentials
spring.security.oauth2.client.registration.google.client-id=your-actual-google-client-id
spring.security.oauth2.client.registration.google.client-secret=your-actual-google-client-secret
```

Для AWS также обновите `src/main/resources/application-aws.properties`:

```properties
# Замените URL на ваш реальный API Gateway URL
spring.security.oauth2.client.registration.google.redirect-uri=https://your-api-gateway-url.amazonaws.com/Prod/login/oauth2/code/google
```

### 4. Локальный запуск

```bash
# Запустите приложение
./mvnw spring-boot:run
```


Приложение будет доступно по адресу: http://localhost:8080

### 5. Тестирование

Step 1 - Build the native image

Change into the project directory
Run the following to build a Docker container image which will include all the necessary dependencies to build the application
```bash
docker build . -t sam/custom-graal-image
```

Build the application within the previously created build image
```bash
sam build --use-container --build-image sam/custom-graal-image
```
After the build finishes, you need to deploy the function:
```bash
sam deploy --guided
```


#### Сборка и создание native image через Docker
```bash
sam build --use-container
```
#### Локальное тестирование Lambda функции
```bash
sam local start-api
```
#### Локальный тест одной функции
```bash
sam local invoke ServerlessAuthFunction -e events/test-event.json
```

## Деплой на AWS

### Настройка AWS

```bash
# Установите и настройте AWS CLI
#aws configure

# Установите SAM CLI
# macOS: brew install aws-sam-cli
# Windows: choco install aws-sam-cli
# Linux: следуйте официальным инструкциям
```

### Ручной деплой (через Docker)

#### Сборка native image через Docker (SAM автоматически использует GraalVM контейнер)
```bash
sam build --use-container
```
#### Первый деплой (с мастером настройки)
```bash
sam deploy --guided
```
#### Последующие деплои
```bash
sam deploy
```
#### Отследить процесс развертывания
```bash
sam logs -n ServerlessAuthFunction --stack-name your-stack-name --tail
```

###  Преимущества Docker подхода:

-  **Не нужно устанавливать GraalVM локально**
-  **Консистентные сборки** на всех платформах
-  **Автоматическая совместимость** с AWS Lambda runtime
-  **Упрощенный CI/CD** - ничего дополнительно настраивать
-  **Поддержка всех ОС** (Windows, macOS, Linux)

### Автоматический деплой (GitHub Actions)

1. Форкните репозиторий
2. Настройте секреты в GitHub:
   - `AWS_ACCESS_KEY_ID`
   - `AWS_SECRET_ACCESS_KEY`
   - `GOOGLE_CLIENT_ID`
   - `GOOGLE_CLIENT_SECRET`

3. Пушьте в main ветку - деплой запустится автоматически

## Архитектура

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   CloudFront    │    │   API Gateway    │    │  Lambda Function│
│   (Optional)    │───▶│                  │───▶│  (GraalVM Native│
│                 │    │                  │    │     Image)      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                │                        │
                                ▼                        ▼
                       ┌──────────────────┐    ┌─────────────────┐
                       │   CloudWatch     │    │   DynamoDB      │
                       │     Logs         │    │   (Sessions)    │
                       │                  │    │   (Optional)    │
                       └──────────────────┘    └─────────────────┘
```

## Структура проекта

```
├── src/
│   ├── main/
│   │   ├── java/com/example/graalvm/
│   │   │   ├── config/           # Конфигурация (Security, Lambda, etc.)
│   │   │   ├── controller/       # REST контроллеры
│   │   │   ├── entity/           # JPA сущности
│   │   │   ├── repository/       # Spring Data репозитории
│   │   │   └── service/          # Бизнес логика
│   │   ├── resources/
│   │   │   ├── templates/        # Thymeleaf темплейты
│   │   │   ├── application.properties
│   │   │   └── application-aws.properties
│   └── test/                     # Тесты
├── .github/workflows/            # GitHub Actions
├── template.yaml                 # SAM template
├── pom.xml                      # Maven конфигурация
└── README.md
```

## Безопасность

- **OAuth2** интеграция с Google
- **CSRF** защита включена
- **Secure headers** настроены
- **Session management** через Spring Security
- **Environment variables** для секретов

## Стоимость (Free Tier)

- **Lambda**: 1M запросов/месяц + 400,000 GB-секунд бесплатно
- **API Gateway**: 1M API вызовов/месяц бесплатно
- **DynamoDB**: 25 GB хранения + 25 units read/write бесплатно
- **CloudWatch**: 5 GB логов бесплатно

**Ожидаемая стоимость**: $0-5/месяц при нормальном использовании

## Отладка

### Локальные логи
```bash
# Просмотр логов приложения
tail -f logs/application.log

# H2 консоль (только для разработки)
http://localhost:8080/h2-console
```

### AWS логи
#### Просмотр Lambda логов
```bash
sam logs -n ServerlessAuthFunction --stack-name serverless-auth-app --tail
```
#### AWS CLI
```bash
aws logs tail /aws/lambda/serverless-auth-app-auth-function --follow
```

> **Примечание:** После первого деплоя с `sam deploy --guided`, все параметры сохранятся в `samconfig.toml`
Все переменные настраиваются в `template.yaml` и передаются через parameters.

## API Endpoints

- `GET /` - Главная страница (redirect на /dashboard)
- `GET /login` - Страница входа
- `GET /dashboard` - Панель управления (требует авторизации)
- `GET /api/user/info` - Информация о пользователе (JSON)
- `POST /logout` - Выход из системы


