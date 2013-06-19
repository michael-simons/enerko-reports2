/*
 * Copyright 2013 ENERKO Informatik GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * THIS SOFTWARE IS  PROVIDED BY THE  COPYRIGHT HOLDERS AND  CONTRIBUTORS "AS IS"
 * AND ANY  EXPRESS OR  IMPLIED WARRANTIES,  INCLUDING, BUT  NOT LIMITED  TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL  THE COPYRIGHT HOLDER OR CONTRIBUTORS  BE LIABLE
 * FOR ANY  DIRECT, INDIRECT,  INCIDENTAL, SPECIAL,  EXEMPLARY, OR  CONSEQUENTIAL
 * DAMAGES (INCLUDING,  BUT NOT  LIMITED TO,  PROCUREMENT OF  SUBSTITUTE GOODS OR
 * SERVICES; LOSS  OF USE,  DATA, OR  PROFITS; OR  BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT  LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE  USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
CREATE OR REPLACE PACKAGE BODY pck_enerko_reports2 IS
    FUNCTION f_create_report_from_statement(p_statement IN VARCHAR2) RETURN BLOB IS LANGUAGE JAVA
        NAME 'de.enerko.reports2.PckEnerkoReports2.createReportFromStatement(java.lang.String) return oracle.sql.BLOB';

    FUNCTION f_create_report_from_statement(p_statement IN VARCHAR2, p_template IN BLOB) RETURN BLOB IS LANGUAGE JAVA
        NAME 'de.enerko.reports2.PckEnerkoReports2.createReportFromStatement(java.lang.String, oracle.sql.BLOB) return oracle.sql.BLOB';
    
    FUNCTION f_create_report(p_method_name IN VARCHAR2) RETURN BLOB IS
    BEGIN
        RETURN f_create_report(p_method_name, p_args => null);
    END f_create_report;
    
    FUNCTION f_create_report(p_method_name IN VARCHAR2, p_args IN t_vargs) RETURN BLOB IS LANGUAGE JAVA
        NAME 'de.enerko.reports2.PckEnerkoReports2.createReport(java.lang.String, oracle.sql.ARRAY) return oracle.sql.BLOB';

    FUNCTION f_create_report(p_method_name IN VARCHAR2, p_template IN BLOB) RETURN BLOB IS
    BEGIN
        RETURN f_create_report(p_method_name, p_template, null);
    END f_create_report;
   
    FUNCTION f_create_report(p_method_name IN VARCHAR2, p_template IN BLOB, p_args IN t_vargs) RETURN BLOB IS LANGUAGE JAVA
        NAME 'de.enerko.reports2.PckEnerkoReports2.createReport(java.lang.String, oracle.sql.BLOB, oracle.sql.ARRAY) return oracle.sql.BLOB';
        
    PROCEDURE p_blob_to_file(p_blob IN BLOB, p_directory_name IN VARCHAR2, p_filename IN VARCHAR2) IS
      v_file      UTL_FILE.FILE_TYPE;
      v_buffer    RAW(32767);
      v_amount    BINARY_INTEGER := 32767;
      v_pos       NUMBER := 1;      
      v_blob_len  NUMBER;
    BEGIN
      v_blob_len := DBMS_LOB.getlength(p_blob);

      -- Open the destination file.
      v_file := UTL_FILE.fopen(upper(p_directory_name), p_filename, 'wb', 32767);

      WHILE v_pos < v_blob_len LOOP
        DBMS_LOB.read(p_blob, v_amount, v_pos, v_buffer);
        UTL_FILE.put_raw(v_file, v_buffer, TRUE);
        v_pos := v_pos + v_amount;
      END LOOP;

      -- Close the file.
      UTL_FILE.fclose(v_file);
    END p_blob_to_file;
    
    FUNCTION f_file_to_blob(p_directory_name IN VARCHAR2, p_filename IN VARCHAR2) RETURN BLOB IS
        v_file BFILE;
        v_blob BLOB;
        v_dest_offset INTEGER := 1;
        v_src_offset INTEGER :=1;
    BEGIN
        v_file := BFILENAME(upper(p_directory_name), p_filename);
        
        -- Create temporary blob
    	DBMS_LOB.createtemporary(v_blob, true, DBMS_LOB.SESSION);
        
        -- Read file into blob
        DBMS_LOB.fileopen(v_file, dbms_lob.file_readonly);
        DBMS_LOB.loadblobfromfile(v_blob, v_file, DBMS_LOB.LOBMAXSIZE, v_dest_offset, v_src_offset);
        DBMS_LOB.fileclose(v_file);
        
        RETURN v_blob;
    END f_file_to_blob;
    
    PROCEDURE p_evaluate_workbook(p_workbook IN BLOB, p_result IN OUT table_of_er_cell_definitions) IS LANGUAGE JAVA
        NAME 'de.enerko.reports2.PckEnerkoReports2.evaluateWorkbook(oracle.sql.BLOB, oracle.sql.ARRAY[])';
        
    FUNCTION f_evaluate_workbook(p_workbook IN BLOB) RETURN table_of_er_cell_definitions pipelined IS
        v_results table_of_er_cell_definitions;
        i         NUMBER;
    BEGIN   
        BEGIN      
            p_evaluate_workbook(p_workbook, v_results);
            i := v_results.FIRST;
            WHILE i IS NOT NULL LOOP 
                pipe row(v_results(i));
                i := v_results.NEXT(i);
            END LOOP;
        EXCEPTION
	        WHEN no_data_found THEN
                null;	    
        END;
        RETURN;
    END f_evaluate_workbook;
END pck_enerko_reports2;
/