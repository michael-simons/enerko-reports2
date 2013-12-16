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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import org.junit.Test;

import de.enerko.reports2.AbstractDatabaseTest;

/**
 * @author Michael J. Simons, 2013-12-16
 */
public class DatasetBasedReportSourceTest extends AbstractDatabaseTest {
	private static ARRAY convertListOfCellsToOracleArray(final List<CellDefinition> cellDefinitions) throws SQLException {
		final StructDescriptor resultStruct = StructDescriptor.createDescriptor("T_ER_CELL_DEFINITION", connection);
		final ArrayDescriptor  arrayDesc = ArrayDescriptor.createDescriptor("TABLE_OF_ER_CELL_DEFINITIONS", connection);
		
		final STRUCT[] rv = new STRUCT[cellDefinitions.size()];						
		int i=0;
		for(CellDefinition cellDefinition : cellDefinitions)
			rv[i++] = new STRUCT(resultStruct, connection, cellDefinition.toSQLStructObject());		
		return new ARRAY(arrayDesc, connection, rv);
	}	
		
	@Test
	public void shouldHandleDatasets() throws SQLException {
		final DatasetBasedReportSource reportSource = new DatasetBasedReportSource(convertListOfCellsToOracleArray(		
				Arrays.asList(
					new CellDefinition("sheet1", 1, 1, null, "string", "test1"),					
					new CellDefinition("sheet1", 2, 3, null, "number", "23", 
							new CommentDefinition("test_comment")
					)				
				)
		));
		
		final List<CellDefinition> cellDefinitions = new ArrayList<CellDefinition>();
		for(CellDefinition cellDefinition : reportSource)
			cellDefinitions.add(cellDefinition);
		assertThat(cellDefinitions.size(), is(2));
				
		CellDefinition cellDefinition = cellDefinitions.get(0);
		CommentDefinition commentDefinition = cellDefinition.comment;
		assertThat(cellDefinition.getType(), is(equalTo("string")));
		assertThat(cellDefinition.value, is(equalTo("test1")));		
		assertThat(cellDefinition.column, is(equalTo(1)));
		assertThat(cellDefinition.row, is(equalTo(1)));
		assertThat(commentDefinition, nullValue());
			
		cellDefinition = cellDefinitions.get(1);
		commentDefinition = cellDefinition.comment;
		assertThat(cellDefinition.getType(), is(equalTo("number")));
		assertThat(cellDefinition.value, is(equalTo("23")));
		assertThat(cellDefinition.column, is(equalTo(2)));
		assertThat(cellDefinition.row, is(equalTo(3)));
		assertThat(commentDefinition, notNullValue());
				
		assertThat(commentDefinition.text, is(equalTo("test_comment")));
		assertThat(commentDefinition.column, is(nullValue()));
		assertThat(commentDefinition.row, is(nullValue()));
		assertThat(commentDefinition.width, is(1));
		assertThat(commentDefinition.height, is(1));
		assertThat(commentDefinition.visible, is(false));
	}
}