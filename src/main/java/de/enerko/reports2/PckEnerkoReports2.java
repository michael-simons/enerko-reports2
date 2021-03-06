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
package de.enerko.reports2;

import static de.enerko.reports2.utils.Unchecker.uncheck;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.BLOB;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;
import de.enerko.reports2.engine.CellDefinition;
import de.enerko.reports2.engine.Report;
import de.enerko.reports2.engine.ReportEngine;

/**
 * This is the main entry point for the PL/SQL package pck_enerko_reports2
 * @author Michael J. Simons, 2013-06-19
 */
public class PckEnerkoReports2 {
	final static OracleConnection connection;
	final static ReportEngine reportEngine;
	static {
		try {
			// Open the default, internal JDBC connection
			connection = (OracleConnection) DriverManager.getConnection("jdbc:default:connection:");
			reportEngine = new ReportEngine(connection);		
		} catch (SQLException e) {
			throw uncheck(e);
		}		
	}
	
	public static String getVersion() {
		return reportEngine.getVersion();
	}
	
	public static String getJavaVersion() {
		return System.getProperty("java.version");
	}
	
	public static BLOB createReportFromStatement(final String statement) throws SQLException, IOException {		
		final Report report = reportEngine.createReportFromStatement(statement);
		
		return writeReportToBlob(report);
	}

	public static BLOB createReportFromStatement(final String statement, final BLOB template) throws SQLException, IOException {
		final Report report = reportEngine.createReportFromStatement(statement, template.getBinaryStream());
		
		return writeReportToBlob(report);
	}
	
	public static BLOB createReportFromDataset(final ARRAY dataset) throws SQLException, IOException {
		final Report report = reportEngine.createReportFromDataset(dataset);
		
		return writeReportToBlob(report);		
	}
	
	public static BLOB createReportFromDataset(final ARRAY dataset, final BLOB template) throws SQLException, IOException {
		final Report report = reportEngine.createReportFromDataset(dataset, template.getBinaryStream());
		
		return writeReportToBlob(report);		
	}
	
	public static BLOB createReport(final String methodName, final ARRAY arguments) throws SQLException, IOException {		
		final Report report = reportEngine.createReport(methodName, extractVargs(arguments));
		
		return writeReportToBlob(report);
	}
	
	public static BLOB createReport(final String methodName, final BLOB template, final ARRAY arguments) throws SQLException, IOException {
		final Report report = reportEngine.createReport(methodName, template.getBinaryStream(), extractVargs(arguments));
		
		return writeReportToBlob(report);
	}
	
	public static void createAndEvaluateReport(final String statement, final String methodName, final BLOB template, final ARRAY arguments, final ARRAY[] result) throws SQLException, IOException {
		final Report report;
		boolean isStatementBased = statement != null && statement.trim().length() != 0;
		boolean isFunctionBased = methodName != null && methodName.trim().length() != 0;
		final InputStream $template = template == null ? null : template.getBinaryStream();
		if(isStatementBased && !isFunctionBased)
			report = reportEngine.createReportFromStatement(statement, $template);
		else if(isFunctionBased && !isStatementBased)
			report = reportEngine.createReport(methodName, $template, extractVargs(arguments));
		else {
			if($template != null)
				$template.close();
			throw new RuntimeException("A report must either be statement or function based!");
		}
		
		result[0] = convertListOfCellsToOracleArray(report.evaluateWorkbook());
	}
		
	public static void evaluateWorkbook(final BLOB in, final ARRAY[] result) throws Exception {
		final Report report = reportEngine.createReport(in.getBinaryStream());
		result[0] = convertListOfCellsToOracleArray(report.evaluateWorkbook());	
	}

	private static String[] extractVargs(final ARRAY arguments) throws SQLException {
		final String[] $arguments;
		if(arguments == null)
			$arguments = new String[0];
		else {
			$arguments = new String[arguments.length()];
			final ResultSet hlp = arguments.getResultSet();
			int i = 0;
			while(hlp.next()) {
				// The actual data resides at index 2
				$arguments[i++] = hlp.getString(2);
			}
		}
		return $arguments;
	}
	
	private static BLOB writeReportToBlob(final Report report) throws SQLException, IOException {
		final BLOB rv = BLOB.createTemporary(connection, true, BLOB.DURATION_SESSION);
		final OutputStream out = new BufferedOutputStream(rv.setBinaryStream(0));
		report.write(out);
		return rv;
	}
	
	private static ARRAY convertListOfCellsToOracleArray(final List<CellDefinition> cellDefinitions) throws SQLException {
		final StructDescriptor resultStruct = StructDescriptor.createDescriptor("T_ER_CELL_DEFINITION", connection);
		final ArrayDescriptor  arrayDesc = ArrayDescriptor.createDescriptor("TABLE_OF_ER_CELL_DEFINITIONS", connection);
		
		final STRUCT[] rv = new STRUCT[cellDefinitions.size()];						
		int i=0;
		for(CellDefinition cellDefinition : cellDefinitions)
			rv[i++] = new STRUCT(resultStruct, connection, cellDefinition.toSQLStructObject());		
		return new ARRAY(arrayDesc, connection, rv);
	}	
}