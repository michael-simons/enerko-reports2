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

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import oracle.jdbc.OracleConnection;
import de.enerko.reports2.FormalArgument.DataType;

/**
 * Diese Reportquelle repräsentiert den Aufruf einer pipelined Function.
 * Es werden zur Zeit folgende Datentypen als Parameter unterstützt {@link DataType}.
 * @author Michael J. Simons, 2013-06-18
 */
public class FunctionBasedReportSource implements ReportSource {
	private final OracleConnection connection;
	private final String sqlStatement;
	private final ConcreteArgument[] arguments;
	
	public FunctionBasedReportSource(OracleConnection connection, final String methodName, final String... arguments) throws SQLException {
		this.connection = connection;
		
		// Anzahl und Typ der Argumente bestimmen
		final ArgumentResolver argumentResolver = new ArgumentResolver(connection);
		final List<FormalArgument> formalArguments = argumentResolver.getArguments(methodName);
		
		if(formalArguments.size() != arguments.length)
			throw new RuntimeException("Anzahl formaler Argumente != Anzahl konkreter Argumente!");
		
		// SQL Statementstring erstellen
		final StringBuilder sb = new StringBuilder();
		String sep = "";
		sb.append("Select * from table(").append(methodName).append("(");
		for(int i=0; i<arguments.length; ++i) {
			sb.append(sep).append("?");
			sep = ", ";			
		}
		sb.append("))");			
		this.sqlStatement = sb.toString();
		
		this.arguments = new ConcreteArgument[arguments.length];
		int cnt = 0;
		for(FormalArgument argument : formalArguments)
			// Position in PL/SQL ist 1-basiert
			this.arguments[cnt++] = new ConcreteArgument(argument, arguments[argument.position-1]);		
	}

	public Iterator<CellDefinition> iterator() {
		try {
			final PreparedStatement statement = this.connection.prepareStatement(this.sqlStatement);
			for(ConcreteArgument argument : arguments)
				argument.setTo(statement);
			return new ReportSourceIterator(statement.executeQuery());
		} catch (SQLException e) {
			throw Unchecker.uncheck(e);
		}
	}
}