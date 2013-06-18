CREATE OR REPLACE PACKAGE pck_enerko_reports2_test AS
	FUNCTION f_no_args RETURN NUMBER;
	
	PROCEDURE p_no_args;
	
	FUNCTION f_some_args(p_number IN NUMBER, p_date IN DATE, p_datetime IN TIMESTAMP, p_string IN  VARCHAR2) RETURN NUMBER;
	
	PROCEDURE p_some_args (p_number IN NUMBER, p_date IN DATE, p_datetime IN TIMESTAMP, p_string IN  VARCHAR2);
	
	FUNCTION f_fb_report_source_test(num_rows NUMBER, p_test_date IN DATE, p_test_string IN VARCHAR2) RETURN table_of_er_cell_definitions pipelined;
	
	FUNCTION f_all_features RETURN table_of_er_cell_definitions pipelined;
END;
/

sho err

CREATE OR REPLACE PACKAGE BODY pck_enerko_reports2_test AS
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
	
	FUNCTION f_fb_report_source_test(num_rows NUMBER, p_test_date IN DATE, p_test_string IN VARCHAR2) RETURN table_of_er_cell_definitions pipelined IS
	BEGIN
		FOR i IN 0 .. (num_rows - 1) LOOP
    		pipe row(
      			t_er_cell_definition(
	        		'f_fb_report_source_test',
	        		0,
	        		i,
	        		'string',
	        		'Row number ' || i || ' ' || p_test_string
      			)
      		);
      	END LOOP;
      	RETURN; 
	END f_fb_report_source_test;
	
	FUNCTION f_all_features RETURN table_of_er_cell_definitions pipelined IS
		r INTEGER;
	BEGIN
		r:=0;		
		-- Datentypen
		pipe row(t_er_cell_definition(
			'datatypes', 0, r, 'string', 'Bezeichnung'
		));
		pipe row(t_er_cell_definition(
			'datatypes', 1, r, 'string', 'Wert'
		));
		
		r:=r+1;
		pipe row(t_er_cell_definition(
			'datatypes', 0, r, 'string', 'string'
		));
		pipe row(t_er_cell_definition(
			'datatypes', 1, r, 'string', 'beliebiger string wert'
		));
		
		r:=r+1;
		pipe row(t_er_cell_definition(
			'datatypes', 0, r, 'string', 'number'
		));
		pipe row(t_er_cell_definition(
			'datatypes', 1, r, 'number', '42.23'
		));
		pipe row(t_er_cell_definition(
			'datatypes', 2, r, 'string', 'Mit Formatierung'
		));
		pipe row(t_er_cell_definition(
			'datatypes', 3, r, 'number', '42.23@@#0.000'
		));
				
		r:=r+1;
		pipe row(t_er_cell_definition(
			'datatypes', 0, r, 'string', 'date'
		));
		pipe row(t_er_cell_definition(
			'datatypes', 1, r, 'date', '06.05.2013'
		));
		
		r:=r+1;
		pipe row(t_er_cell_definition(
			'datatypes', 0, r, 'string', 'date'
		));
		pipe row(t_er_cell_definition(
			'datatypes', 1, r, 'datetime', '06.05.2013 03:41'
		));
		
		r:=r+1;
		pipe row(t_er_cell_definition(
			'datatypes', 0, r, 'string', 'formula'
		));
		pipe row(t_er_cell_definition(
			'datatypes', 1, r, 'formula', 'SUM(1,2,3,4,5,6,7,8,9,10)'
		));
		
		-- Formatvorlagen
		r:=1;
		pipe row(t_er_cell_definition(
			'Nutzung von Formatvorlagen', 0, r, 'string', 'A1'
		));
		pipe row(t_er_cell_definition(
			'Nutzung von Formatvorlagen', 1, r, 'string; "Formatvorlagen" A1', 'beliebiger string wert'
		));
		
		r:=r+1;
		pipe row(t_er_cell_definition(
			'Nutzung von Formatvorlagen', 0, r, 'string', 'A2'
		));
		pipe row(t_er_cell_definition(
			'Nutzung von Formatvorlagen', 1, r, 'datetime; "Formatvorlagen" A2', '06.05.2013 03:41'
		));		
		
		r:=r+1;
		pipe row(t_er_cell_definition(
			'Nutzung von Formatvorlagen', 0, r, 'string', 'A3'
		));
		pipe row(t_er_cell_definition(
			'Nutzung von Formatvorlagen', 1, r, 'number; "Formatvorlagen" A3', '-4223.123'
		));
		
		FOR i IN 0 .. 19 LOOP
		pipe row(t_er_cell_definition(
			'diagramme', 0, i, 'formula', 'EXP(' || i || ')'
		));
		END LOOP;
		
		RETURN;
	END f_all_features;		
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