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
package de.enerko.reports2.engine;

import static de.enerko.reports2.utils.Unchecker.uncheck;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import oracle.jdbc.OracleConnection;
import oracle.sql.ARRAY;

import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.udf.AggregatingUDFFinder;
import org.apache.poi.ss.formula.udf.DefaultUDFFinder;
import org.apache.poi.ss.formula.udf.UDFFinder;

import de.enerko.reports2.functions.NormInv;

/**
 * This is the main entry point for creating reports.
 * @author Michael J. Simons, 2013-06-18
 */
public class ReportEngine {	
	public final static Logger logger = Logger.getLogger(ReportEngine.class.getName());
	private final OracleConnection connection;
	private final Map<String, FreeRefFunction> customFunctions = new HashMap<String, FreeRefFunction>();
	private final String version;
	
	public ReportEngine(OracleConnection connection) {
		this.connection = connection;
		this.addCustomFunction("Enerko_NormInv", new NormInv());
		try {
			final Properties properties = new Properties();
			properties.load(this.getClass().getResourceAsStream("/de/enerko/reports2/ReportEngine.properties"));
			final String hlp = properties.getProperty("de.enerko.reports2.version");
			this.version = "${pom.version}".equalsIgnoreCase(hlp.trim()) ?  "n/a" : hlp;
		} catch(Exception e) {
			throw uncheck(e);
		}
	}
	
	public ReportEngine addCustomFunction(final String name, final FreeRefFunction freeRefFunction) {
		this.customFunctions.put(name, freeRefFunction);
		return this;
	}
	
	/**
	 * Creates a report based on a SQL-Statement
	 * @param statement
	 * @return
	 */
	public Report createReportFromStatement(final String statement) {
		return new Report(new StatementBasedReportSource(this.connection, statement), this.createCustomFunctions());
	}
	
	/**
	 * Creates a report based on a SQL-Statement and a template will be used.
	 * @param statement
	 * @param template Input stream for a template. It will automatically be buffered.
	 * @return
	 */
	public Report createReportFromStatement(final String statement, final InputStream template) {
		return new Report(new StatementBasedReportSource(this.connection, statement), this.createCustomFunctions(), template);		
	}
	
	/**
	 * Creates a report based on an Oracle ARRAY containing elements of type t_er_cell_definition
	 * @param dataset The data on which the report should be created
	 * @return
	 */
	public Report createReportFromDataset(final ARRAY dataset) {
		return new Report(new DatasetBasedReportSource(dataset), this.createCustomFunctions());
	}
	
	/**
	 * Creates a report based on an Oracle ARRAY containing elements of type t_er_cell_definition
	 * and a template will be used.
	 * @param dataset The data on which the report should be created
	 * @param template Input stream for a template. It will automatically be buffered.
	 * @return
	 */
	public Report createReportFromDataset(final ARRAY dataset, final InputStream template) {
		return new Report(new DatasetBasedReportSource(dataset), this.createCustomFunctions(), template);
	}
	
	/**
	 * Creates a report based on a pipelined function
	 * @param methodName
	 * @param arguments
	 * @return
	 */
	public Report createReport(final String methodName, final String... arguments) {
		return new Report(new FunctionBasedReportSource(this.connection, methodName, arguments), this.createCustomFunctions());	
	}
	
	/**
	 * Creates a report based on a pipelined function and a template will be used.
	 * @param methodName
	 * @param template Input stream for a template. It will automatically be buffered.
	 * @param arguments
	 * @return
	 */
	public Report createReport(final String methodName, final InputStream template, final String... arguments) {		
		return new Report(new FunctionBasedReportSource(this.connection, methodName, arguments), this.createCustomFunctions(), template);	
	}
	
	public Report createReport(final InputStream workbook) {
		return new Report(workbook, this.createCustomFunctions());
	}
	
	private UDFFinder createCustomFunctions() {
		UDFFinder rv = null;
		
		if(this.customFunctions.size() > 0) {
			String[] names = new String[this.customFunctions.size()];
			FreeRefFunction[] implementations = new FreeRefFunction[this.customFunctions.size()];
			int i=0;
			for(Map.Entry<String, FreeRefFunction> entry : this.customFunctions.entrySet()) {
				names[i] = entry.getKey();
				implementations[i] = entry.getValue();
				++i;
			}
			rv = new AggregatingUDFFinder(new DefaultUDFFinder(names, implementations));
		}
		
		return rv;
	}

	public String getVersion() {
		return version;
	}
}