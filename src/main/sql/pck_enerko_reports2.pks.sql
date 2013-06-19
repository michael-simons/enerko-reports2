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
CREATE OR REPLACE PACKAGE pck_enerko_reports2 IS
    /**
     * Creates an Excel Workbook from the result of the statement given
     * through p_statement and returns the binary data
     */
    FUNCTION f_create_report_from_statement(p_statement IN VARCHAR2) RETURN BLOB;
    
    /**
     * Creates an Excel workbook from the result of the statement given
     * through p_statement and returns the binary data. The template 
     * p_template is used for the resulting report.
     */
    FUNCTION f_create_report_from_statement(p_statement IN VARCHAR2, p_template IN BLOB) RETURN BLOB;

    /**
     * See f_create_report with vargs. Java stored procedure cannot have defaulted parameters
     */
    FUNCTION f_create_report(p_method_name IN VARCHAR2) RETURN BLOB;
    
    /**
     * Creates an Excel workbook by calling the given pipelined function. All strings in p_args
     * are converted to the appropriate type and passed as parameters to the method.
     */
    FUNCTION f_create_report(p_method_name IN VARCHAR2, p_args IN t_vargs) RETURN BLOB;

    /**
     * See f_create_report with vargs. Java stored procedure cannot have defaulted parameters
     */
    FUNCTION f_create_report(p_method_name IN VARCHAR2, p_template IN BLOB) RETURN BLOB;

    /**
     * Creates an Excel workbook by calling the given pipelined function. All strings in p_args
     * are converted to the appropriate type and passed as parameters to the method. The template 
     * p_template is used for the resulting report.
     */    
    FUNCTION f_create_report(p_method_name IN VARCHAR2, p_template IN BLOB, p_args IN t_vargs) RETURN BLOB;
    
    /**
     * Stores the blob p_blob into the file named p_filename inside the Oracle directory p_directory_name
     * The use must have write permissions for that directory
     */
    PROCEDURE p_blob_to_file(p_blob IN BLOB, p_directory_name IN VARCHAR2, p_filename IN VARCHAR2);
    
    /**
     * Reads the file p_filename inside the Oracle directory p_directory_name into a temporary blob
     */
    FUNCTION f_file_to_blob(p_directory_name IN VARCHAR2, p_filename IN VARCHAR2) RETURN BLOB;
END pck_enerko_reports2;
/