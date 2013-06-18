CREATE OR REPLACE PACKAGE pck_hre_test AS
	FUNCTION f_no_args RETURN NUMBER;
	
	PROCEDURE p_no_args;
	
	FUNCTION f_some_args(p_number IN NUMBER, p_date IN DATE, p_datetime IN TIMESTAMP, p_string IN  VARCHAR2) RETURN NUMBER;
	
	PROCEDURE p_some_args (p_number IN NUMBER, p_date IN DATE, p_datetime IN TIMESTAMP, p_string IN  VARCHAR2);
	
	FUNCTION f_fb_report_source_test(num_rows NUMBER, p_test_date IN DATE, p_test_string IN VARCHAR2) RETURN table_of_hre_cell_definitions pipelined;
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
	
	FUNCTION f_fb_report_source_test(num_rows NUMBER, p_test_date IN DATE, p_test_string IN VARCHAR2) RETURN table_of_hre_cell_definitions pipelined IS
	BEGIN
		FOR i IN 0 .. (num_rows - 1) LOOP
    		pipe row(
      			t_hre_cell_definition(
	        		'f_fb_report_source_test',
	        		0,
	        		i,
	        		'string',
	        		'Row number ' || i
      			)
      		);
      	END LOOP;
      	RETURN; 
	END f_fb_report_source_test;
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