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

import static de.enerko.reports2.Unchecker.uncheck;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

/**
 * This is a helper class that iterates a given result set and
 * dynamically retrieves the parameters of a {@link CellDefinition}<br>
 * It is only used internally.
 * @author Michael J. Simons, 2013-06-18
 */
class ReportSourceIterator implements Iterator<CellDefinition> {			
	private final ResultSet resultSet;
	private final Class<ResultSet> clazz;	
	
	public ReportSourceIterator(final ResultSet resultSet) {
		this.resultSet = resultSet;		
		this.clazz = (Class<ResultSet>) this.resultSet.getClass();
	}

	public boolean hasNext() {
		boolean rv = false;
		try {
			rv = this.resultSet.next();
		} catch(SQLException e) {			
		} finally {
			if(!rv) {
				try {					
					final Statement statement = this.resultSet.getStatement();
					this.resultSet.close();
					statement.close();
				} catch(SQLException e) {					
				}
			}
		}		
		return rv;
	}

	public CellDefinition next() {
		return new CellDefinition(
					this.get(String.class, "sheetname"),
					this.get(int.class, "cell_column"),
					this.get(int.class, "cell_row"),
					this.get(String.class, "cell_name"),
					this.get(String.class, "cell_type"),
					this.get(String.class, "cell_value")
			);
	}

	public void remove() {
		throw new UnsupportedOperationException("Method \"remove\" is not supported!");
	}		
	
	/**
	 * Extracts the value with the type <code>typeClazz</code> from the
	 * given <code>resultSet</code>.<br>
	 * It is assumed here that only typeClazz represents only public getXXX 
	 * methods from {@link ResultSet} 
	 *  
	 * @param typeClazz
	 * @param columnName
	 * @return
	 */
	private <T> T get(final Class<T> typeClazz, String columnName) {
		try {
			final String typeName = typeClazz.getSimpleName();
			final Method m = this.clazz.getMethod(String.format("get%s%s", Character.toUpperCase(typeName.charAt(0)), typeName.substring(1)), String.class);
			// Oracle has deprecated access to classes and methods inside oracle.jdbc.driver.*
			// and marked methods as inAccessible but the connection returns a ResultSet inside that package			
			m.setAccessible(true);			
			return (T) m.invoke(this.resultSet, columnName);
		} catch (NoSuchMethodException e) {
			throw uncheck(e);
		} catch (SecurityException e) {
			throw uncheck(e);
		} catch (IllegalAccessException e) {
			throw uncheck(e);
		} catch (IllegalArgumentException e) {
			throw uncheck(e);
		} catch (InvocationTargetException e) {
			if(e.getCause() instanceof SQLException) {
				final SQLException cause = (SQLException) e.getCause();
				if(cause.getErrorCode() == 17006)
					throw new ReportSource.MissingReportColumn(columnName);
				else 
					throw uncheck(cause);	
			} else
				throw uncheck(e.getCause());
		}
	}
}