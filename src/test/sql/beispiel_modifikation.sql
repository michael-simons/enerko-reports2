DECLARE
    -- Eingabedatei
    v_excel_in BLOB;
    -- Eingabedaten der Exceldatei: Jede einzelne Zelle
    v_excel_data_in    table_of_er_cell_definitions;
    -- Das werden die Ausgabedaten des neuen Reports
    v_excel_data_out   table_of_er_cell_definitions;
    v_cell             t_er_cell_definition;
    -- Ausgabeblob
    v_excel_out BLOB;
    i         NUMBER;
BEGIN    
    -- Eingabedatei lesen
    -- Hier kommen Deine Eingabedaten als BLOB rein, wie ist egal.
    -- Das Beispiel nutzt eine Excel Datei, die im DB Directory "enerko_reports" liegt
    -- Wird in Variable gespeichert um sie am Ende als Template ein zweites Mal zu nutzen
    v_excel_in := pck_enerko_reports2.f_file_to_blob('enerko_reports', 'ensupply_input.xls');
    

    -- Eingabedatei einlesen
    pck_enerko_reports2.p_evaluate_workbook(v_excel_in, v_excel_data_in);
    
    -- Ausgabedaten vorbereiten
    v_excel_data_out := table_of_er_cell_definitions();
    
    -- Eingabedaten prüfen, bearbeiten, whatever...
    i := v_excel_data_in.FIRST;
    WHILE i IS NOT NULL LOOP             
        v_cell := v_excel_data_in(i);
        
        IF v_cell.cell_value = '1+1=3' THEN
            v_cell.cell_comment := t_er_comment_definition('Das kann ja so nicht stimmen');
            v_cell.cell_comment.comment_visible := 'true';
            v_cell.cell_comment.comment_width := 4;
            v_cell.cell_comment.comment_height := 2;
        END IF;
        
        -- In Ausgabedaten kopieren
        v_excel_data_out.extend();
        v_excel_data_out(v_excel_data_out.count) := v_cell;
        
        i := v_excel_data_in.NEXT(i);
    END LOOP;
    
    -- Mit den geprüften und bearbeiteten Daten neuen Report erzeugen
    -- Die Eingabedatei wird als Template benutzt
    v_excel_out := pck_enerko_reports2.f_create_report_from_dataset(v_excel_data_out, v_excel_in);
    
    -- Tesweise einmal ausgeben...
    DECLARE
        v_comment_text VARCHAR2(32767) := null;
    BEGIN
       FOR cell in (
        	SELECT *
        	FROM table(
        		pck_enerko_reports2.f_evaluate_workbook(v_excel_out)
        	) src
        ) LOOP
            IF cell.cell_comment IS NOT NULL THEN
                v_comment_text := cell.cell_comment.comment_text;	
            ELSE 
                v_comment_text := 'Kein Kommentar';
        	END IF;
            dbms_output.put_line(cell.cell_name || ' = ' || cell.cell_value || ' (' || v_comment_text || ')');
        END LOOP;
    END;
    
    -- Irgendwo speichern...
    -- Beispiel benutzt server side file, Du wuerdest wahrscheinlich den Blob ueber Forms exportieren
    pck_enerko_reports2.p_blob_to_file(v_excel_out, 'enerko_reports', 'gepruefte_excel_datei.xls');    
END;
/