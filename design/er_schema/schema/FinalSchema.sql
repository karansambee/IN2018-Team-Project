/* Complete set of DDL statements (CREATE TABLE statements) */
CREATE TABLE ExchangeRate (
  CurrencyName      char(4) NOT NULL, 
  USDConversionRate numeric(8, 6) NOT NULL, 
  CurrencySymbol    char(2) NOT NULL, 
  PRIMARY KEY (CurrencyName));

CREATE TABLE Staff (
  StaffID        bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, 
  CurrencyName   char(4), 
  StaffRole      integer(1) NOT NULL, 
  ComissionRate  numeric(4, 2), 
  Firstname      varchar(15) NOT NULL, 
  Surname        varchar(15) NOT NULL, 
  PhoneNumber    varchar(15) NOT NULL, 
  EmailAddress   varchar(25) NOT NULL, 
  DateOfBirth    date NOT NULL, 
  Postcode       varchar(7) NOT NULL, 
  HouseNumber    varchar(4) NOT NULL, 
  StreetName     varchar(20) NOT NULL, 
  HashedPassword binary(32) NOT NULL, 
  PasswordSalt   binary(32) NOT NULL, 
  FOREIGN KEY (CurrencyName) REFERENCES ExchangeRate(CurrencyName));

CREATE UNIQUE INDEX Staff_EmailAddress 
  ON Staff (EmailAddress);

CREATE TABLE BlankType (
  TypeNumber      INTEGER NOT NULL PRIMARY KEY AUTO_INCREMENT, 
  TypeDescription varchar(255) NOT NULL);

CREATE TABLE Blank (
  BlankNumber      bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, 
  StaffID          bigint(19) NOT NULL, 
  TypeNumber       integer(3) NOT NULL, 
  BlankDescription varchar(255) NOT NULL, 
  Blacklisted      boolean NOT NULL, 
  Void             boolean NOT NULL, 
  ReceivedDate     date NOT NULL, 
  AssignedDate     date, 
  ReturnedDate     date, 
  FOREIGN KEY (StaffID) REFERENCES Staff(StaffID), 
  FOREIGN KEY (TypeNumber) REFERENCES BlankType(TypeNumber));

CREATE TABLE DiscountPlan (
  DiscountPlanID     bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, 
  DiscountType       integer(1) NOT NULL, 
  DiscountPercentage numeric(4, 2));

CREATE TABLE FlexibleDiscountEntries (
  EntryID            bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, 
  DiscountPlanID     bigint(19) NOT NULL, 
  AmountLowerBound   numeric(12, 2) NOT NULL, 
  AmountUpperBound   numeric(12, 2) NOT NULL, 
  DiscountPercentage numeric(4, 2) NOT NULL, 
  FOREIGN KEY (DiscountPlanID) REFERENCES DiscountPlan(DiscountPlanID));

CREATE TABLE Customer (
  CustomerID             bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, 
  DiscountPlanID         bigint(19), 
  CurrencyName           char(4) NOT NULL, 
  Firstname              varchar(15) NOT NULL, 
  Surname                varchar(15) NOT NULL, 
  PhoneNumber            varchar(15) NOT NULL, 
  EmailAddress           varchar(25), 
  DateOfBirth            date NOT NULL, 
  Postcode               varchar(7) NOT NULL, 
  HouseNumber            varchar(4) NOT NULL, 
  StreetName             varchar(20) NOT NULL, 
  AccountDiscountCredit  numeric(12, 2), 
  PurchaseAccumulation   numeric(10, 2) NOT NULL, 
  PurchaseMonthBeginning date NOT NULL, 
  Alias                  varchar(32) NOT NULL UNIQUE, 
  FOREIGN KEY (DiscountPlanID) REFERENCES DiscountPlan(DiscountPlanID), 
  FOREIGN KEY (CurrencyName) REFERENCES ExchangeRate(CurrencyName));

CREATE TABLE Sale (
  BlankNumber   bigint(19) NOT NULL, 
  CustomerID    bigint(19) NOT NULL, 
  CurrencyName  char(4) NOT NULL, 
  SaleType      integer(1) NOT NULL, 
  CommissonRate numeric(4, 2) NOT NULL, 
  SaleDate      date NOT NULL, 
  DueDate       date NOT NULL, 
  Cost          numeric(8, 2) NOT NULL, 
  Tax           numeric(8, 2) NOT NULL, 
  AdditionalTax numeric(8, 2), 
  PRIMARY KEY (BlankNumber), 
  FOREIGN KEY (CustomerID) REFERENCES Customer(CustomerID), 
  FOREIGN KEY (BlankNumber) REFERENCES Blank(BlankNumber))
  FOREIGN KEY (CurrencyName) REFERENCES ExchangeRate(CurrencyName));

CREATE TABLE Transcation (
  TranscationID   bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, 
  BlankNumber     bigint(19) NOT NULL, 
  CurrencyName    char(4) NOT NULL, 
  AmountPaid      numeric(10, 2) NOT NULL, 
  AmountPaidInUSD numeric(10, 2), 
  TransactionDate date NOT NULL, 
  PaymentType     integer(1) NOT NULL, 
  CardNumber      bigint(19), 
  ChequeNumber    bigint(19), 
  FOREIGN KEY (BlankNumber) REFERENCES Sale(BlankNumber), 
  FOREIGN KEY (CurrencyName) REFERENCES ExchangeRate(CurrencyName));

CREATE TABLE Refund (
  RefundID                  bigint(19) NOT NULL PRIMARY KEY AUTO_INCREMENT, 
  TranscationID             bigint(19) NOT NULL, 
  RefudDate                 date NOT NULL, 
  LocalCurrencyRefundAmount numeric(11, 2) NOT NULL, 
  FOREIGN KEY (TranscationID) REFERENCES Transcation(TranscationID));

/* 2 INSERT Statements */

INSERT INTO DiscountPlan VALUES (
    DEFAULT,
    0,
    4.5
);

INSERT INTO ExchangeRate VALUES (
    'GBP',
	1.22,
	'£'
);

INSERT INTO Customer VALUES (
    DEFAULT,
    NULL,
    'GBP',
    'John',
    'Doe',
    '03648318215',
    'JohnDoe@gmail.com',
    '1965-05-12',
    'RG3 2IK',
    '233',
    'Albert Road',
	NULL,
	0,
	'2023-03-01',
	'JohnD'
);


/* 2 UPDATE STATEMENTS */

UPDATE Customer 
SET DiscountPlanID = 1
WHERE CustomerID = 1;

UPDATE Customer 
SET Postcode = 'PG4 4JK', HouseNumber = '5A', StreetName = 'St. Johns Street'
WHERE CustomerID = 1;


/* 2 SELECT STATEMENTS */

SELECT Postcode, HouseNumber, StreetName
FROM Customer
WHERE DateOfBirth BETWEEN '1960-01-01' AND '1975-01-01';

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
    DEFAULT,
	'GBP',
    0,
    3.50,
    'Percy',
    'Benedict',
    '0421135672',
    'Percy.Benedict@ATS.com',
    '1997-03-21',
    'AA1 1BV',
    '120A',
    'St. Peter Street',
    0x00,
    0x00
);

INSERT INTO Customer VALUES (
    1,
    NULL,
    'GBP',
    'John',
    'Doe',
    '03648318215',
    'JohnDoe@gmail.com',
    '1965-05-12',
    'RG3 2IK',
    '233',
    'Albert Road',
	NULL,
	0,
	'2023-03-01',
	'JohnD'
);

INSERT INTO BlankType VALUES
    (444, 'Two leg interline journey.'),
	(101, 'Single leg domestic journey.');

INSERT INTO Blank VALUES 
    (4441023489, 1, 444, 'London to Seoul, Seoul to Manila', 0, 0, '2023-03-03', '2023-03-04', NULL),
    (4444343235, 1, 444, 'London to Tokoyo, Tokoyo to Dublin', 0, 0, '2023-03-03', '2023-03-04', NULL),
    (4441344491, 1, 444, 'London to Pairs, Paris to Lyon', 0, 0, '2023-03-03', '2023-03-04', NULL),
    (1015003455, 1, 101, 'Liverpool to London', 0, 0, '2023-03-04', '2023-03-04', NULL),
    (1014044431, 1, 101, 'Manchester to Luton', 0, 0, '2023-03-04', '2023-03-04', NULL);

INSERT INTO Sale VALUES 
    (4441023489, 1, 'GBP', 1, 3.50, '2023-02-01', '2023-03-02', 1000.50, 200.00, 30.00),
    (4444343235, 1, 'GBP', 1, 3.50, '2023-02-04', '2023-03-05', 500.00, 100.00, 56.00),
    (4441344491, 1, 'GBP', 1, 3.50, '2023-02-05', '2023-03-06', 350.50, 50.00, 20.00),
    (1015003455, 1, 'GBP', 0, 3.50, '2023-02-25', '2023-03-26', 187.40, 65.00, NULL),
    (1014044431, 1, 'GBP', 0, 3.50, '2023-02-25', '2023-03-26', 187.00, 75.00, NULL);

INSERT INTO Transcation VALUES 
    (1, 4441023489, 'GBP', 1230.50, 1501.21, '2023-02-04', 0, NULL, NULL),
    (2, 4444343235, 'GBP', 656.00, 800.32, '2023-02-06', 1, 353435218, NULL),
    (3, 4441344491, 'GBP', 420.00, 420.00, '2023-02-08', 0, NULL, NULL),
    (4, 1015003455, 'GBP', 252.40, 512.40, '2023-02-25', 0, NULL, NULL),
    (5, 1014044431, 'GBP', 262.00, 314.04, '2023-02-27', 3, NULL, 978424910);
