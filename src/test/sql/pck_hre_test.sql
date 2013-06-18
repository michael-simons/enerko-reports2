CREATE OR REPLACE PACKAGE pck_hre_test AS
	FUNCTION f_no_args RETURN NUMBER;
	
	PROCEDURE p_no_args;
	
	FUNCTION f_some_args(p_number IN NUMBER, p_date IN DATE, p_datetime IN TIMESTAMP, p_string IN  VARCHAR2) RETURN NUMBER;
	
	PROCEDURE p_some_args (p_number IN NUMBER, p_date IN DATE, p_datetime IN TIMESTAMP, p_string IN  VARCHAR2);
END;
/

sho err

CREATE OR REPLACE PACKAGE BODY pck_hre_test AS
	FUNCTION f_no_args RETURN NUMBER IS
	BEGIN
		return -1;
	END f_no_args;
	
	PROCEDURE p_no_args IS
	BEGIN
		null;
	END;
	
	FUNCTION f_some_args(p_number IN NUMBER, p_date IN DATE, p_datetime IN TIMESTAMP, p_string IN  VARCHAR2) RETURN NUMBER IS
	BEGIN
		return -1;
	END f_some_args;
	
	PROCEDURE p_some_args(p_number IN NUMBER, p_date IN DATE, p_datetime IN TIMESTAMP, p_string IN  VARCHAR2) IS
	BEGIN
		null;
	END p_some_args;
END;
/

sho err

CREATE OR REPLACE FUNCTION f_arg_resolver_test_no_args RETURN NUMBER AS 
BEGIN
	return -1;
END;
/

CREATE OR REPLACE PROCEDURE p_arg_resolver_test_no_args AS 
BEGIN
	NULL;
END;
/

CREATE OR REPLACE FUNCTION f_arg_resolver_test_some_args(
	p_number IN NUMBER,
	p_date IN DATE,
	p_datetime IN TIMESTAMP,
	p_string IN  VARCHAR2
) RETURN NUMBER AS 
BEGIN
	return -1;
END;
/

CREATE OR REPLACE PROCEDURE p_arg_resolver_test_some_args(
	p_number IN NUMBER,
	p_date IN DATE,
	p_datetime IN TIMESTAMP,
	p_string IN  VARCHAR2
) AS 
BEGIN
	NULL;
END;
/