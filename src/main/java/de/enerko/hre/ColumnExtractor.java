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

import static de.enerko.hre.Unchecker.uncheck;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.enerko.hre.ReportSource.MissingReportColumn;

/**
 * Ein Wrapper um ein ResultSet, das über Reflections skalare Werte extrahiert.
 * Wird eine Spalte nicht gefunden, wird eine eindeutige {@link MissingReportColumn}
 * Exception geworfen, alle anderen {@link SQLException} werden weitergereicht.
 * 
 * @author Michael J. Simons, 2013-06-18
 */
public class ColumnExtractor {
	private final Class<ResultSet> clazz;
	private final ResultSet resultSet;
	
	public ColumnExtractor(ResultSet resultSet) {
		this.resultSet = resultSet;		
		this.clazz = (Class<ResultSet>) this.resultSet.getClass();
	}

	/**
	 * Extrahiert den Wert vom Typ <code>typeClazz</code> aus dem 
	 * <code>resultSet</code> für die Spalte <code>columnName</code><br>
	 * Es wird davon ausgegangen, dass für typeClazz nur Werte benutzt werden,
	 * die als getXXX Methode in {@link ResultSet} vorhanden sind.
	 * 
	 * @param typeClazz
	 * @param columnName
	 * @return
	 */
	public <T> T get(final Class<T> typeClazz, String columnName) {
		try {
			final String typeName = typeClazz.getSimpleName();
			final Method m = this.clazz.getMethod(String.format("get%s%s", Character.toUpperCase(typeName.charAt(0)), typeName.substring(1)), String.class);
			// Oracle hat oracle.jdbc.driver.* deprecated, gibt aber immer noch Klassen innerhalb dieses Packages 
			// zurück. Die Methoden sind leider als inAccessible deklariert.
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

	public boolean next() throws SQLException {
		return resultSet.next();
	}

	public void close() throws SQLException {
		resultSet.close();
	}	
}