CREATE DATABASE EzBuild 
	COLLATE Croatian_CI_AS_KS_WS
GO

USE EzBuild
GO

CREATE TABLE Firm 
(
	IDFirm INT PRIMARY KEY IDENTITY,
	FirmName NVARCHAR(50) not null,
	WarehouseID INT null,
)
GO

CREATE TABLE EmployeeType
(
	IDEmployeeType INT PRIMARY KEY IDENTITY,
	EmployeeType NVARCHAR(50) not null
)
GO

CREATE TABLE Employee 
(
	IDEmployee INT PRIMARY KEY IDENTITY,
	FirebaseID NVARCHAR(255) null,
	FullName NVARCHAR(100) not null,
	Email NVARCHAR(100) null,
	Phone NVARCHAR(20) null,
	EmployeeTypeID INT not null,
	FirmID INT not null,
	ConstructionSiteID INT null,

	CONSTRAINT FK_Employee_EmployeeType FOREIGN KEY (EmployeeTypeID)
		REFERENCES EmployeeType(IDEmployeeType),

	CONSTRAINT FK_Employee_Firm FOREIGN KEY (FirmID)
		REFERENCES Firm(IDFirm),
)
GO

CREATE TABLE Meeting
(
	IDMeeting INT PRIMARY KEY IDENTITY,
	Title NVARCHAR(100) not null,
	MeetingDate DATE not null,
	MeetingStartTime TIME not null,
	MeetingDuration NVARCHAR(20) not null,
	MeetingDescription NVARCHAR(250) not null,
	EmployeeID INT not null,

	CONSTRAINT FK_Meeting_Employee FOREIGN KEY (EmployeeID) 
		REFERENCES Employee(IDEmployee)
)
GO

CREATE TABLE EmployeeHours
(
	IDEmployeeHours INT PRIMARY KEY IDENTITY,
	HoursWorked SMALLINT not null,
	DateWorkDone DATE not null,
	EmployeeID INT not null,

	CONSTRAINT FK_EmployeeHours_Employee FOREIGN KEY (EmployeeID) 
		REFERENCES Employee(IDEmployee)
)
GO

CREATE TABLE Warehouse
(
	IDWarehouse INT PRIMARY KEY IDENTITY,
	FullAddress NVARCHAR(200) not null,
	WarehouseManagerID INT not null,

	CONSTRAINT FK_Warehouse_Employee FOREIGN KEY (WarehouseManagerID)
		REFERENCES Employee(IDEmployee)
)
GO

CREATE TABLE Equipment 
(
	IDEquipment INT PRIMARY KEY IDENTITY,
	Base64Image VARCHAR(max) not null,
	EquipmentName NVARCHAR(200) not null,
	Quantity INT not null,
	EquipmentDescription NVARCHAR(max) not null,
	WarehouseID INT not null,

	CONSTRAINT FK_Equipment_Warehouse FOREIGN KEY (WarehouseID)
		REFERENCES Warehouse(IDWarehouse)
)
GO

CREATE TABLE EquipmentHistory
(
	IDEquipmentHistory INT PRIMARY KEY IDENTITY,
	EmployeeID INT not null,
	DateEquipmentTaken SMALLDATETIME not null,
	QuantityTaken INT not null,
	EquipmentID INT not null,
	WarehouseID INT not null,

	CONSTRAINT FK_EquipmentHistory_Employee FOREIGN KEY (EmployeeID)
		REFERENCES Employee(IDEmployee),

	CONSTRAINT FK_EquipmentHistory_Equipment FOREIGN KEY (EquipmentID)
		REFERENCES Equipment(IDEquipment),

	CONSTRAINT FK_EquipmentHistory_Warehouse FOREIGN KEY (WarehouseID)
		REFERENCES Warehouse(IDWarehouse)
)
GO

CREATE TABLE ConstructionSite 
(
	IDConstructionSite INT PRIMARY KEY IDENTITY,
	Base64Image VARCHAR(max) null,
	FullAddress NVARCHAR(200) not null,
	Latitude DECIMAL(8, 6) not null,
	Longitude DECIMAL(9, 6) not null,
	IsActive BIT not null,
	FirmID int not null,
	ConstructionSiteManagerID INT null,

	CONSTRAINT FK_ConstructionSite_Firm FOREIGN KEY (FirmID)
		REFERENCES Firm(IDFirm),

	CONSTRAINT FK_ConstructionSite_Employee FOREIGN KEY (ConstructionSiteManagerID)
		REFERENCES Employee(IDEmployee)
)
GO

CREATE TABLE ConstructionSiteDiaryEntry
(
	IDConstructionSiteDiary INT PRIMARY KEY IDENTITY,
	DiaryEntry NVARCHAR(max) not null,
	DiaryEntryDate SMALLDATETIME not null,
	DiaryEntryEmployeeID INT not null,
	ConstructionSiteID INT not null,

	CONSTRAINT FK_ConstructionSiteDiaryEntry_Employee FOREIGN KEY (DiaryEntryEmployeeID)
		REFERENCES Employee(IDEmployee),

	CONSTRAINT FK_ConstructionSiteDiaryEntry_ConstructionSite FOREIGN KEY (ConstructionSiteID)
		REFERENCES ConstructionSite(IDConstructionSite)
)
GO

ALTER TABLE Firm 
ADD CONSTRAINT FK_Firm_Warehouse 
FOREIGN KEY (WarehouseID) REFERENCES Warehouse(IDWarehouse)
GO


ALTER TABLE Employee
ADD CONSTRAINT FK_Employee_ConstructionSite
FOREIGN KEY (ConstructionSiteID) REFERENCES ConstructionSite(IDConstructionSite)
GO

INSERT INTO EmployeeType (EmployeeType)
VALUES ('Direktor'), ('Inženjer'), ('Voditelj skladišta'), ('Fizièki radnik')
GO

INSERT INTO Firm (FirmName)
VALUES ('Ta-grad d.o.o.')
GO

INSERT INTO Employee (FirebaseID, FullName, Email, Phone, EmployeeTypeID, FirmID, ConstructionSiteID)
VALUES (null, 'Pero Periæ', 'pero@mail.com', '098123456', 1, 1, null),
	   (null, 'Netko Netkiæ', 'netko@mail.com', '098654321', 2, 1, null),
	   (null, 'Antonio Antoniæ', 'antonio@gmail.com', '098654543', 2, 1, null),
	   (null, 'Ante Antiæ', 'ante.antic@gmail.com', '098654322', 2, 1, null),
	   (null, 'Marko Markiæ', 'marko.markic@gmail.com', '098654333', 3, 1, null),
	   (null, 'Mirko Mirkiæ', 'mirko@gmail.com', '098654444', 4, 1, null),
	   (null, 'Petar Horvat', 'petar.horvat@gmail.com', '098654555', 4, 1, null),
	   (null, 'Ivan Horvat', 'ivan.horvat@gmail.com', '098654666', 4, 1, null),
	   (null, 'Slavenko Slaviæ', 'slavenko@gmail.com', '098654777', 4, 1, null),
	   (null, 'Blaženko Blažiæ', 'blaženko@gmail.com', '098654888', 4, 1, null),
	   (null, 'Jasenko Jasniæ', 'jasenko@gmail.com', '098654999', 4, 1, null),
	   (null, 'Matej Matiæ', 'matej@gmail.com', '098654000', 4, 1, null),
	   (null, 'Tin Martin', 'tin@gmail.com', '098654001', 4, 1, null),
	   (null, 'Tomislav Tomièiæ', 'tomislav@gmail.com', '098654002', 4, 1, null)
GO

INSERT INTO Warehouse (FullAddress, WarehouseManagerID)
VALUES ('Mlinovi 110', 5)
GO

INSERT INTO ConstructionSite (Base64Image, FullAddress, Latitude, Longitude, IsActive,  FirmID, ConstructionSiteManagerID)
VALUES (null, 'Testna lokacija 123', 5.12, 6.12, 1, 1, 2)
GO

INSERT INTO ConstructionSiteDiaryEntry (DiaryEntry, DiaryEntryDate, DiaryEntryEmployeeID, ConstructionSiteID)
VALUES ('Rad na fasadi', '2022-04-12', 2, 1)
GO

UPDATE Firm SET WarehouseID = 1 WHERE IDFirm = 1
GO

INSERT INTO Equipment (Base64Image, EquipmentName, Quantity, EquipmentDescription, WarehouseID)
VALUES ('', 'Bušilica', 1, 'Bušilica opis', 1), ('', 'Vreæa cementa', 10, 'Vreæa cementa od 10kg', 1)
GO

INSERT INTO EquipmentHistory (EmployeeID, DateEquipmentTaken, QuantityTaken, EquipmentID, WarehouseID)
VALUES (8, '2022-04-25', 1, 1, 1), (9, '2022-04-20', 2, 2, 1)
GO

INSERT INTO Meeting (Title, MeetingDate, MeetingStartTime, MeetingDuration, MeetingDescription, EmployeeID)
VALUES ('Sastank Title', '2022-04-20', '8:00', '2:30', 'Sastanak Description', 3)
GO

