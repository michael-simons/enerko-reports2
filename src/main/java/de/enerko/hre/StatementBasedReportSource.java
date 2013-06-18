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
package de.enerko.hre;

import java.sql.SQLException;
import java.sql.Statement;

import oracle.jdbc.OracleConnection;

/**
 * Führt das im Konstruktor übergebene Statement aus und stellt eine Liste von
 * {@link CellDefinition}s zur Verfügung. Es wird während der Iteration geprüft,
 * ob alle notwendingen Spalten definiert sind.
 * @author Michael J. Simons, 2013-06-18
 */
public class StatementBasedReportSource implements ReportSource {
	private final Statement statement;
	private final ColumnExtractor resultSet;
	
	public StatementBasedReportSource(OracleConnection connection, final String statement) throws SQLException {		
		this.statement = connection.createStatement();
		this.resultSet = new ColumnExtractor(this.statement.executeQuery(statement));
	}

	public boolean hasNext() {
		boolean rv = false;
		try {
			rv = this.resultSet.next();
		} catch(SQLException e) {			
		} finally {
			if(!rv) {
				try {
					this.statement.close();
					this.resultSet.close();
				} catch(SQLException e) {					
				}
			}
		}		
		return rv;
	}

	public CellDefinition next() {
		return new CellDefinition(
					this.resultSet.get(String.class, "sheetname"),
					this.resultSet.get(int.class, "cell_column"),
					this.resultSet.get(int.class, "cell_row"),
					this.resultSet.get(String.class, "cell_name"),
					this.resultSet.get(String.class, "cell_type"),
					this.resultSet.get(String.class, "cell_value")
			);
	}

	public void remove() {
		throw new UnsupportedOperationException("Method \"remove\" is not supported!");
	}
}