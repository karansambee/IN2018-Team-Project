/* Complete set of DDL statements (CREATE TABLE statements) */

CREATE TABLE Blank (
  BlankNumber      int(10) NOT NULL, 
  StaffID          int(10) NOT NULL, 
  BlankDescription varchar(255) NOT NULL, 
  Returned         int(1) NOT NULL, 
  Blacklisted      int(1) NOT NULL, 
  Void             int(1) NOT NULL, 
  PRIMARY KEY (BlankNumber)
);

CREATE TABLE Customer (
  CustomerID           int(10) NOT NULL AUTO_INCREMENT, 
  DiscountPlanID       int(10), 
  CurrencyName         varchar(10), 
  DiscountAccumulation decimal(10, 2), 
  Firstname            varchar(15) NOT NULL, 
  Surname              varchar(15) NOT NULL, 
  PhoneNumber          varchar(15) NOT NULL, 
  EmailAddress         varchar(25), 
  DateOfBirth          date NOT NULL, 
  Postocde             varchar(7) NOT NULL, 
  HouseNumber          varchar(4) NOT NULL, 
  StreetName           varchar(20) NOT NULL, 
  PRIMARY KEY (CustomerID)
);

CREATE TABLE DiscountPlan (
  DiscountPlanID     int(10) NOT NULL AUTO_INCREMENT, 
  DiscountType       varchar(8) NOT NULL, 
  DiscountPercentage decimal(4, 2) NOT NULL, 
  PRIMARY KEY (DiscountPlanID)
);

CREATE TABLE ExchangeRate (
  CurrencyName      varchar(10) NOT NULL, 
  USDConversionRate decimal(8, 6) NOT NULL, 
  PRIMARY KEY (CurrencyName)
);

CREATE TABLE Refund (
  RefundID                  int(10) NOT NULL AUTO_INCREMENT, 
  TranscationID             int(10) NOT NULL, 
  RefudDate                 date NOT NULL, 
  LocalCurrencyRefundAmount decimal(11, 2) NOT NULL, 
  PRIMARY KEY (RefundID)
);

CREATE TABLE Sale (
  BlankNumber   int(10) NOT NULL, 
  CustomerID    int(10) NOT NULL, 
  SaleType      varchar(10) NOT NULL, 
  CommissonRate decimal(4, 2) NOT NULL, 
  SaleDate      date NOT NULL, 
  DueDate       date NOT NULL, 
  Cost          decimal(7, 2) NOT NULL, 
  Tax           decimal(6, 2) NOT NULL, 
  AdditionalTax decimal(6, 2), 
  PRIMARY KEY (BlankNumber)
);

CREATE TABLE Staff (
  StaffID        int(10) NOT NULL AUTO_INCREMENT, 
  StaffRole      varchar(20) NOT NULL, 
  ComissionRate  decimal(4, 2), 
  CurrencyName   varchar(10), 
  Firstname      varchar(15) NOT NULL, 
  Surname        varchar(15) NOT NULL, 
  PhoneNumber    varchar(15) NOT NULL, 
  EmailAddress   varchar(25) NOT NULL, 
  DateOfBirth    date NOT NULL, 
  Postcode       varchar(7) NOT NULL, 
  HouseNumber    varchar(4) NOT NULL, 
  StreetName     varchar(20) NOT NULL, 
  HashedPassword varchar(255) NOT NULL, 
  PasswordSalt   varchar(255) NOT NULL, 
  PRIMARY KEY (StaffID)
);

CREATE TABLE Transcation (
  TranscationID   int(10) NOT NULL AUTO_INCREMENT, 
  BlankNumber     int(10) NOT NULL, 
  CurrencyName    varchar(10) NOT NULL, 
  AmountPaid      decimal(11, 2) NOT NULL, 
  AmountPaidInUSD decimal(7, 2), 
  TransactionDate date NOT NULL, 
  PaymentType     varchar(4) NOT NULL, 
  CardNumber      int(19), 
  ChequeNumber    int(6), 
  PRIMARY KEY (TranscationID)
);

/* 2 INSERT Statements */

INSERT INTO Customer VALUES (
    NULL,
    NULL,
    "USD",
    0,
    "John",
    "Doe",
    "03648318215",
    "JohnDoe@gmail.com",
    "1965-05-12",
    "RG3 2IK",
    "233",
    "Albert Road",  
);

INSERT INTO DiscountPlan VALUES (
    NULL,
    "FIXED",
    4.5
);


/* 2 UPDATE STATEMENTS */

UPDATE Customer 
SET DiscountPlanID = 1
WHERE CustomerID = 1;

UPDATE Customer 
SET PostCode = "PG4 4JK", HouseNumber = "5A", StreetName = "St. Johns Street"
WHERE CustomerID = 1;


/* 2 SELECT STATEMENTS */

SELECT Postcode, HouseNumber, StreetName
FROM Customer
WHERE DateOfBirth BETWEEN "1960-01-01" AND "1975-01-01";

SELECT COUNT(CustomerID) AS CustomersWithoutDiscountPlans
FROM Customer
WHERE DiscountPlanID IS NULL;


/* 2 DELETE STATEMENTS */

DELETE FROM Customer
WHERE CustomerID = 1;

DELETE FROM DiscountPlan
WHERE DiscountPlanID = 1;


/* SQL DML statements which are needed to create 2 non-trivial reports pertaining to the case-study */
/* Report 1 would be an Interline Sales Report an Report 2 would be a Domestic Sales Report */

INSERT INTO Staff VALUES (
    NULL,
    "Travel Agent",
    3.50,
    "GDP",
    "Percy",
    "Benedict",
    "0421135672",
    "Percy.Benedict@ATS.com",
    "1997-03-21",
    "AA1 1BV",
    "120A",
    "St. Peter Street",
    "!'@DHsdHFeS2!",
    "DD$%FdsNC2!Dnd"
);

INSERT INTO Blank VALUES (
    444 10023489, 1, "London to Seoul, Seoul to Manila", 0, 0, 0,
    444 43432435, 1, "London to Tokoyo, Tokoyo to Dublin", 0, 0, 0,
    440 13434491, 1, "London to Pairs, Paris to Lyon", 0, 0, 0,
    101 50023455, 1, "Liverpool to London", 0, 0, 0,
    101 40454431, 1, "Manchester to Luton", 0, 0, 0
);

INSERT INTO Sale VALUES (
    444 10023489, 1, "INTERLINE", 3.50, "2023-02-01", "2023-03-02", 1000.50, 200.00, 30.00,
    444 43432435, 1, "INTERLINE", 3.50, "2023-02-04", "2023-03-05", 500.00, 100.00, 56.00,
    440 13434491, 1, "INTERLINE", 3.50, "2023-02-05", "2023-03-06", 350.50, 50.00, 20.00,
    101 50023455, 1, "DOMESTIC", 3.50, "2023-02-25", "2023-03-26", 187.40, 65.00, NULL,
    101 40454431, 1, "DOMESTIC", 3.50, "2023-02-25", "2023-03-26", 187.00, 75.00, NULL
);

INSERT INTO Transcation VALUES (
    1, 444 10023489, "USD", 1230.50, NULL, "2023-02-04", "CASH", NULL, NULL,
    2, 444 43432435, "USD", 656.00, NULL, "2023-02-06", "CARD", 353435218, NULL,
    3, 440 13434491, "USD", 420.00, NULL, "2023-02-08", "CASH", NULL, NULL,
    4, 101 50023455, "GDP", 252.40, 302.52, "2023-02-25", "CASH", NULL, NULL,
    5, 101 40454431, "GDP", 262.00, 314.04, "2023-02-27", "CHEQUE", NULL, 978424910
);