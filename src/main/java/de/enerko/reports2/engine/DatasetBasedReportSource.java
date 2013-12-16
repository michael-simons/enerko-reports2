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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Iterator;

import oracle.sql.ARRAY;
import de.enerko.reports2.utils.Unchecker;

/**
 * @author Michael J. Simons, 2013-12-16
 */
public class DatasetBasedReportSource implements ReportSource {
	private static class DatasetIterator implements Iterator<CellDefinition> {
		private final ResultSet resultSet;
		
		public DatasetIterator(ResultSet resultSet) {
			this.resultSet = resultSet;
		}

		public boolean hasNext() {
			try {
				return this.resultSet.next();
			} catch (SQLException e) {
				throw Unchecker.uncheck(e);
			}
		}

		public CellDefinition next() {
			try {
				return CellDefinition.fromStruct((Struct) this.resultSet.getObject(2));				
			
			} catch (SQLException e) {
				throw Unchecker.uncheck(e);
			}
		}

		public void remove() {
			throw new UnsupportedOperationException("Method \"remove\" is not supported!");			
		}		
	}
	
	private final ARRAY dataset;
	
	public DatasetBasedReportSource(ARRAY dataset) {
		this.dataset = dataset;
	}

	public Iterator<CellDefinition> iterator() {
		try {
			return new DatasetIterator(this.dataset.getResultSet());
		} catch (SQLException e) {
			throw Unchecker.uncheck(e);
		}
	}
}