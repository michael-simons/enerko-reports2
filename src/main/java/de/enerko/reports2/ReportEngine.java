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

import java.io.InputStream;
import java.util.logging.Logger;

import oracle.jdbc.OracleConnection;

/**
 * This is the main entry point for creating reports.
 * @author Michael J. Simons, 2013-06-18
 */
public class ReportEngine {	
	public final static Logger logger = Logger.getLogger(ReportEngine.class.getName());
	private final OracleConnection connection;
	
	public ReportEngine(OracleConnection connection) {
		this.connection = connection;
	}
	
	/**
	 * Creates a report based on a SQL-Statement
	 * @param statement
	 * @return
	 */
	public Report createReport(final String statement) {
		return new Report(new StatementBasedReportSource(this.connection, statement));
	}
	
	/**
	 * Creates a report based on a SQL-Statement and a template will be used.
	 * @param statement
	 * @param template
	 * @return
	 */
	public Report createReport(final String statement, final InputStream template) {
		return new Report(new StatementBasedReportSource(this.connection, statement), template);		
	}
	
	/**
	 * Creates a report based on a pipelined function
	 * @param methodName
	 * @param arguments
	 * @return
	 */
	public Report createReport(final String methodName, final String... arguments) {
		return new Report(new FunctionBasedReportSource(this.connection, methodName, arguments));	
	}
	
	/**
	 * Creates a report based on a pipelined function and a template will be used.
	 * @param methodName
	 * @param template
	 * @param arguments
	 * @return
	 */
	public Report createReport(final String methodName, final InputStream template, final String... arguments) {		
		return new Report(new FunctionBasedReportSource(this.connection, methodName, arguments), template);	
	}
}