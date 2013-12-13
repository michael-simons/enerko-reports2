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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import de.enerko.reports2.utils.Unchecker;

/**
 * This is a helper class that iterates a given result set and
 * dynamically retrieves the parameters of a {@link CellDefinition}<br>
 * It is only used internally.
 * @author Michael J. Simons, 2013-06-18
 */
class ReportSourceIterator implements Iterator<CellDefinition> {
	private final static Set<String> optionalColumns;
	static {
		final Set<String> hlp = new HashSet<String>();
		hlp.add("cell_comment");
		hlp.add("comment_author");
		hlp.add("comment_column");
		hlp.add("comment_row");
		hlp.add("comment_width");		
		hlp.add("comment_height");
		hlp.add("comment_visible");

		optionalColumns = Collections.unmodifiableSet(hlp);		
	}
		
	private final ResultSet resultSet;
	private final Set<String> availableColumns;
			
	public ReportSourceIterator(final ResultSet resultSet) {
		this.resultSet = resultSet;			
		final Set<String> hlp = new HashSet<String>();
		
		try {
			final ResultSetMetaData metaData = this.resultSet.getMetaData();
			for(int i=1; i<=metaData.getColumnCount(); ++i)
				hlp.add(metaData.getColumnName(i).toLowerCase());			
		} catch(SQLException e) {
			throw Unchecker.uncheck(e);
		}
		
		this.availableColumns = Collections.unmodifiableSet(hlp);
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
					this.get(String.class, "cell_value"),
					new CommentDefinition(					
						this.get(String.class, "comment_author"),
						this.get(String.class, "cell_comment"),
						this.get(Integer.class, "comment_column"),
						this.get(Integer.class, "comment_row"),
						this.get(int.class, "comment_width"),
						this.get(int.class, "comment_height"),
						Boolean.parseBoolean(this.get(String.class, "comment_visible"))
					)
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
		T rv = null;
		if(!optionalColumns.contains(columnName) || this.availableColumns.contains(columnName)) {
			try {			
				final String typeName = typeClazz.getSimpleName();			
				if("String".equals(typeName))
					rv = (T) this.resultSet.getString(columnName);
				else if("int".equals(typeName))
					rv = (T)((Integer)this.resultSet.getInt(columnName));
				else if("Integer".equals(typeName)) {
					Number hlp = (Number)this.resultSet.getObject(columnName);
					if(hlp != null) 
						rv = (T) new Integer(hlp.intValue());
				} else 
					throw new RuntimeException(String.format("Unsupported type %s", typeClazz.getName()));			
			} catch (SQLException e) {				
				if(e.getErrorCode() == 17006)
					throw new ReportSource.MissingReportColumn(columnName);
				else 
					throw uncheck(e);	
			}
		}
		return rv;
	}
}