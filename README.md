# Brokerage Firm Assessment

## How to Build

Assuming you have maven installed, you can run application as follows:

```shell
mvn spring:boot run
```

Application will start running on port 8080.

## General Security Information

All endpoints are secured with [Basic Authentication](https://en.wikipedia.org/wiki/Basic_access_authentication). 

Each customer has username/password pair to use in basic
authentication. There are 2 authorities granted by authentication: USER, ADMIN

You can call all endpoints with ADMIN authority. following endpoints are accessible with ADMIN **only**:

* POST/assets/create
* POST /customers/create
* GET /customers/list
* POST /orders/{id}/match


Predefined admin credentials are:

**Username**: admin

**Password**: admin

## How to Use

First you need to create customer using admin credentials.  Sample request is as follows:

```shell
curl 'http://localhost:8080/customers/create' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
--data '{
    "name":"A company makes everything",
    "username": "acme",
    "password":"password"
}'
```

After customer creation, you'd better create some assets for customer, primarily "TRY" asset:

```shell
curl 'http://localhost:8080/assets/create' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWRtaW46YWRtaW4=' \
--data '{
    "customerId": 1,
    "assetName": "TRY",
    "assetSize": 100
}'
```

Now you can submit orders for this customer:

```shell
curl 'http://localhost:8080/orders/create' \
--header 'Content-Type: application/json' \
--header 'Authorization: Basic YWNtZTpwYXNzd29yZA==' \
--data '{
    "customerId": 1,
    "price": 5,
    "orderSide": "BUY",
    "size": 20,
    "assetName": "XAU"
}'
```
---