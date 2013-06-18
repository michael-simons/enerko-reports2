# ENERKOs Report Engine

## Abstract

ENERKOs Report Engine is a [Apache POI-HSSF][1] based reporting engine that runs inside the JVM of an Oracle Datebase and creates Excel reports through an PL/SQL interface.

It is designed to provide an unified reporting engine for

* Oracle Forms 6i based applications
* Java SE clients as well as
* Webapplications with different architectures.

It fits into 2-tier applications as well as into 3-tier applications as reports are stored at a central point (the database) as 

* PL/SQL functions
* Queries or
* views.

It consists of 2 parts:

* The implementation in Java
* An PL/SQL api to access the Java Stored Procedures

ENERKOs Report Engine can also run client side but this is not recommended as installation base will be duplicated.

## Installation

The following describes the installation inside a database with the fictive user 'hre'.

The report engine should be installed into every scheme where it will be used.

The user needs the following privileges

```
	GRANT CREATE ANY PROCEDURE TO "HRE"
	GRANT CREATE ANY TABLE TO "HRE"
	GRANT CREATE PROCEDURE TO "HRE"
	GRANT CREATE TABLE TO "HRE"


Quotas must be adapted or unlimited tablespace be granted:

```
	GRANT UNLIMITED TABLESPACE TO "HRE"

To actually load the java source files the following permission is needed as well:

```
	call dbms_java.grant_permission('HRE', 'SYS:oracle.aurora.security.JServerPermission', 'loadLibraryInClass.*', null);
	
I use [loadjava.bat][3] to load the java source files which is part of the Oracle Client package (Oracle InstantClient won't be enough). Alternatively [dbms_java.loadjava][4] can be used.

First, load the required dependencies:

```
	loadjava.bat -user hre/hre@database -resolve lib/commons-codec-1.5.jar
	loadjava.bat -user hre/hre@database -resolve lib/poi-3.9.jar
	
then load ENERKOs Report Engine:

```
	loadjava.bat -user hre/hre@database -resolve target/enerko-hre-0.0.1-SNAPSHOT-sources.jar 
	
I prefer (for whatever reason) the sources.

## Usage


[1]: http://poi.apache.org/spreadsheet/
[2]: http://docs.oracle.com/cd/E11882_01/java.112/e10588/toc.htm
[3]: http://docs.oracle.com/cd/E11882_01/java.112/e10588/cheleven.htm#JJDEV10060
[4]: http://docs.oracle.com/cd/E11882_01/java.112/e10588/appendixa.htm#JJDEV13000