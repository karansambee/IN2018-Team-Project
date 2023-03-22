CREATE TABLE Blank (
  BlankNumber      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
  StaffID          bigint(19), 
  TypeNumber       integer(3) NOT NULL, 
  BlankDescription varchar(255) NOT NULL, 
  Blacklisted      boolean NOT NULL, 
  Void             boolean NOT NULL, 
  ReceivedDate     date NOT NULL, 
  AssignedDate     date, 
  ReturnedDate     date, 
  FOREIGN KEY(StaffID) REFERENCES Staff(StaffID), 
  FOREIGN KEY(TypeNumber) REFERENCES BlankType(TypeNumber));
CREATE TABLE Sale (
  BlankNumber     bigint(19) NOT NULL, 
  CustomerID      bigint(19) NOT NULL, 
  CurrencyName    char(4) NOT NULL, 
  SaleType        integer(1) NOT NULL, 
  CommissonRate   numeric(4, 2) NOT NULL, 
  SaleDate        date NOT NULL, 
  DueDate         date NOT NULL, 
  Cost            numeric(8, 2) NOT NULL, 
  Tax             numeric(8, 2) NOT NULL, 
  AdditionalTax   numeric(8, 2), 
  PreDiscountCost numeric(8, 2), 
  PRIMARY KEY (BlankNumber), 
  FOREIGN KEY(CustomerID) REFERENCES Customer(CustomerID), 
  FOREIGN KEY(BlankNumber) REFERENCES Blank(BlankNumber), 
  FOREIGN KEY(CurrencyName) REFERENCES ExchangeRate(CurrencyName));
CREATE TABLE Staff (
  StaffID        INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
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
  HashedPassword blob NOT NULL, 
  PasswordSalt   blob NOT NULL, 
  FOREIGN KEY(CurrencyName) REFERENCES ExchangeRate(CurrencyName));
CREATE TABLE Refund (
  RefundID                  INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
  TranscationID             bigint(19) NOT NULL, 
  RefudDate                 date NOT NULL, 
  LocalCurrencyRefundAmount numeric(11, 2) NOT NULL, 
  FOREIGN KEY(TranscationID) REFERENCES Transcation(TranscationID));
CREATE TABLE Customer (
  CustomerID             INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
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
  FOREIGN KEY(DiscountPlanID) REFERENCES DiscountPlan(DiscountPlanID), 
  FOREIGN KEY(CurrencyName) REFERENCES ExchangeRate(CurrencyName));
CREATE TABLE Transcation (
  TranscationID   INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
  BlankNumber     bigint(19) NOT NULL, 
  CurrencyName    char(4) NOT NULL, 
  AmountPaid      numeric(10, 2) NOT NULL, 
  AmountPaidInUSD numeric(10, 2), 
  TransactionDate date NOT NULL, 
  PaymentType     integer(1) NOT NULL, 
  CardNumber      bigint(19), 
  ChequeNumber    bigint(19), 
  FOREIGN KEY(BlankNumber) REFERENCES Sale(BlankNumber), 
  FOREIGN KEY(CurrencyName) REFERENCES ExchangeRate(CurrencyName));
CREATE TABLE ExchangeRate (
  CurrencyName      char(4) NOT NULL, 
  USDConversionRate numeric(8, 6) NOT NULL, 
  CurrencySymbol    char(2) NOT NULL, 
  PRIMARY KEY (CurrencyName));
CREATE TABLE DiscountPlan (
  DiscountPlanID     INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
  DiscountType       integer(1) NOT NULL, 
  DiscountPercentage numeric(4, 2));
CREATE TABLE BlankType (
  TypeNumber      INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
  TypeDescription varchar(255) NOT NULL);
CREATE TABLE FlexibleDiscountEntries (
  EntryID            INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, 
  DiscountPlanID     bigint(19) NOT NULL, 
  AmountLowerBound   numeric(12, 2) NOT NULL, 
  AmountUpperBound   numeric(12, 2) NOT NULL, 
  DiscountPercentage numeric(4, 2) NOT NULL, 
  FOREIGN KEY(DiscountPlanID) REFERENCES DiscountPlan(DiscountPlanID));
CREATE UNIQUE INDEX Staff_EmailAddress 
  ON Staff (EmailAddress);
