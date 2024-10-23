# Brokerage API Management

This API provides a set of endpoints for managing transactions, trade orders, user authentication, and assets in a brokerage system.

## Useful
1.  Please find the screenshots located in the 'extras' folder for reference.
2.  Check the data.sql file before starting the application
3.  The "**system**" user has been added for initial asset loading.
4.  Run "Flow" folders on postman in a sequence to check endpoints.
5.  Adjust or play around with Brokerage API Management collection in postman
6.  Check the sample logs file in extras folder with your results

## API Information

- **Version:** 4.0.0
- **License:** [Apache License Version 2.0](https://www.apache.org/licenses/LICENSE-2.0)
- **Contact:** [Brokerage](http://www.brokerage.com/) - info@brokerage.com

## Server

- **Local Development Server:** [http://localhost:8080](http://localhost:8080)

## API Endpoints

### Transaction API
- **GET** `/api/transactions`: List transactions for a specific customer.
    - **Parameters:** `customerId` (required)
- **POST** `/api/transactions`: Deposit or withdraw money.
    - **Request Body:** Transaction object.

### Trade Orders API
- **GET** `/api/tradeOrders`: List trade orders for a customer within a date range.
    - **Parameters:** `customerId`, `startDate`, `endDate`, `page`, `size` (all required except `page` and `size`)
- **POST** `/api/tradeOrders`: Create a new trade order.
    - **Request Body:** CreateOrderRequest object.
- **DELETE** `/api/tradeOrders/{tradeOrderId}`: Delete a trade order by ID.

### Trade Matcher API
- **POST** `/api/tradeMatcher/match`: Match all pending trade orders.

### Auth API
- **POST** `/api/auth/register`: Register a new user.
    - **Request Body:** RegisterRequest object.
- **POST** `/api/auth/authenticate`: Authenticate a user.
    - **Request Body:** AuthenticationRequest object.

### Asset API
- **GET** `/api/assets`: List assets for a specific customer.
    - **Parameters:** `customerId` (required)

## Data Models

### Transaction
- Fields: `id`, `customer`, `type` (DEPOSIT/WITHDRAWAL), `amount`, `iban`, `timestamp`, `processed`.

### TradeOrder
- Fields: `id`, `customer`, `assetCode`, `orderSide` (BUY/SELL), `size`, `price`, `status`, `createDate`.

### Asset
- Fields: `id`, `customer`, `assetCode`, `size`, `usableSize`.

### User
- Fields: `id`, `username`, `roles`, `customer`, `enabled`, `authorities`.

## Usage

To start using the API, you can send requests to the local server endpoints provided above, ensuring to adhere to the required parameters and request body specifications.
## Features

- **Create Order**: Create a new order for a given customer, specifying asset, side (BUY/SELL), size, and price.
- **List Orders**: List orders for a given customer within a specified date range.
- **Delete Order**: Cancel a pending order.
- **Deposit Money**: Deposit TRY for a given customer and amount.
- **Withdraw Money**: Withdraw TRY for a given customer and amount with IBAN.
- **List Assets**: List all assets for a given customer.
- **User Authorization**: All endpoints are secured and require admin/customer user authentication.

 

## Requirements

- Java 17 or higher
- Spring Boot
- H2 Database
- Maven 3.5+

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/brokerageapi.git
   cd brokerage-firm-api
   ```

2. Build the project using Maven:
    ```
    mvn clean install
   ```
3. Run the application:
    ```
    mvn spring-boot:run
   ```
4. Access the API at http://localhost:8080/api.

## Database Initialization
Initial data will be added using the data.sql file located in src/main/resources. This file will be executed on application startup to populate the database with initial data.


## Helper links
### Swagger UI
http://localhost:8080/swagger-ui/index.html#/

### API docs
http://localhost:8080/v3/api-docs

### H2 Console: 
user/password : 
sa - pass
http://localhost:8080/h2-console/



## Queries to check

SELECT * FROM ASSET

SELECT * FROM TRADE_ORDER

SELECT * FROM TRADE 