# Order & Inventory Management Platform

A Java 17 / Spring Boot REST API project for managing products, stock, and customer orders. It is designed as a learning portfolio project for Java backend roles.

## Features

- Product CRUD: create, list, get, update, and delete products.
- Product validation and unique SKU checks.
- Customer orders with multiple product line items.
- Stock is checked and deducted when an order is placed.
- Cancelling an order restores its stock; an order cannot be cancelled twice.
- PostgreSQL persistence using Spring Data JPA / Hibernate.
- Centralized API error responses and request validation.

## Requirements

- Java 17+
- PostgreSQL
- Maven (or use the included Maven Wrapper)

## 1. Create the database

Open PostgreSQL / pgAdmin and run:

```sql
CREATE DATABASE order_inventory;
```

## 2. Configure the database password

The project intentionally does not store a real password in source control. In PowerShell, set the password for your local PostgreSQL user before starting the app:

```powershell
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_actual_postgres_password"
$env:DB_URL="jdbc:postgresql://localhost:5432/order_inventory"
```

Keep these variables set in the same terminal window from which you start the application. Alternatively, edit `application.properties` locally, but do not commit real credentials to GitHub.

## 3. Run the application

From the project root in PowerShell:

```powershell
mvn spring-boot:run
```

If you prefer the Maven Wrapper, you can run `./mvnw spring-boot:run` on macOS/Linux or `\.\mvnw.cmd spring-boot:run` on Windows; the first run may need internet access to download Maven.

The API will be available at `http://localhost:8080`.

## API endpoints

### Products

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/products` | List products |
| GET | `/api/products/{id}` | Get one product |
| POST | `/api/products` | Create product |
| PUT | `/api/products/{id}` | Update product |
| DELETE | `/api/products/{id}` | Delete product |

Example product JSON:

```json
{
  "name": "Wireless Mouse",
  "sku": "MOUSE-001",
  "category": "Accessories",
  "price": 799.00,
  "stockQuantity": 25
}
```

### Orders

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/orders` | List orders |
| GET | `/api/orders/{id}` | Get one order |
| POST | `/api/orders` | Place an order |
| POST | `/api/orders/{id}/cancel` | Cancel an order and restore stock |

Example order JSON (create the product first and replace `1` with its ID):

```json
{
  "customerName": "Asha Kulkarni",
  "customerEmail": "asha@example.com",
  "items": [
    { "productId": 1, "quantity": 2 }
  ]
}
```

## Test quickly with PowerShell

Create a product:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/products -ContentType 'application/json' -Body '{"name":"Wireless Mouse","sku":"MOUSE-001","category":"Accessories","price":799.00,"stockQuantity":25}'
```

List products:

```powershell
Invoke-RestMethod http://localhost:8080/api/products
```

## Project structure

- `product/`: product entity, request DTO, repository, service, controller.
- `order/`: order entity, order items, request DTOs, repository, service, controller.
- `shared/`: common exception classes and centralized API error handling.

## Important learning note

This is a learning project, not a production-ready inventory system. Before production use, add authentication/authorization, pagination, database migrations, concurrency-safe stock updates, and more automated tests. Review and understand each class before describing the project in an interview.
