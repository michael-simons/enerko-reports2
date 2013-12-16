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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.enerko.reports2.AbstractDatabaseTest;
import de.enerko.reports2.engine.ReportSource.MissingReportColumn;

/**
 * @author Michael J. Simons, 2013-06-18
 */
public class StatementBasedReportSourceTest extends AbstractDatabaseTest {
	@Test(expected=MissingReportColumn.class)
	public void shouldHandleMissingColumns() throws SQLException {
		final StatementBasedReportSource reportSource = new StatementBasedReportSource(connection, "Select 1 as test from dual");
		for(CellDefinition cellDefinition : reportSource)			
			System.out.println(cellDefinition);
	}
	
	@Test(expected=SQLException.class)
	public void shouldHandleSQLErrors() throws SQLException {
		final StatementBasedReportSource reportSource = new StatementBasedReportSource(connection, "Select 1");
		for(CellDefinition cellDefinition : reportSource)			
			System.out.println(cellDefinition);
	}
	
	@Test
	public void shouldHandleValidSelect() throws SQLException {
		final StatementBasedReportSource reportSource = 
				new StatementBasedReportSource(connection, 
						"Select 's1' as sheetname, 1 as cell_column, 1 as cell_row, 'c1' as cell_name, 'ct' as cell_type, 'cv' as cell_value from dual");
		final List<CellDefinition> cellDefinitions = new ArrayList<CellDefinition>();
		for(CellDefinition cellDefinition : reportSource)
			cellDefinitions.add(cellDefinition);
		assertThat(cellDefinitions.size(), is(1));
	}
	
	@Test
	public void shouldHandleValidSelectWithComment() throws SQLException {
		final StatementBasedReportSource reportSource = 
				new StatementBasedReportSource(connection, 
						"Select 's1' as sheetname, 1 as cell_column, 1 as cell_row, 'c1' as cell_name, 'ct' as cell_type, 'cv' as cell_value, t_er_comment_definition('test') as cell_comment from dual");
		final List<CellDefinition> cellDefinitions = new ArrayList<CellDefinition>();
		for(CellDefinition cellDefinition : reportSource)
			cellDefinitions.add(cellDefinition);
		assertThat(cellDefinitions.size(), is(1));
		final CommentDefinition commentDefinition = cellDefinitions.get(0).comment; 
		assertThat(commentDefinition, notNullValue());
		assertThat(commentDefinition.author, is("HRE"));
		assertThat(commentDefinition.column, nullValue());
		assertThat(commentDefinition.row, nullValue());
		assertThat(commentDefinition.width, is(1));
		assertThat(commentDefinition.height, is(1));
		assertThat(commentDefinition.visible, is(false));
	}
	
	@Test
	public void shouldCreateValidCommentDefinition() throws SQLException {
		final StatementBasedReportSource reportSource = 
				new StatementBasedReportSource(connection, 
						"Select 's1' as sheetname, 1 as cell_column, 1 as cell_row, 'c1' as cell_name, 'ct' as cell_type, 'cv' as cell_value, " +
						"       t_er_comment_definition('test', p_column => 23, p_row => 42, p_width => 3, p_height => 4, p_visible => 'true') as cell_comment " +
						"from dual");
		final List<CellDefinition> cellDefinitions = new ArrayList<CellDefinition>();
		for(CellDefinition cellDefinition : reportSource)
			cellDefinitions.add(cellDefinition);
		assertThat(cellDefinitions.size(), is(1));
		final CommentDefinition commentDefinition = cellDefinitions.get(0).comment; 
		assertThat(commentDefinition, notNullValue());
		assertThat(commentDefinition.author, is("HRE"));
		assertThat(commentDefinition.column, is(23));
		assertThat(commentDefinition.row, is(42));
		assertThat(commentDefinition.width, is(3));
		assertThat(commentDefinition.height, is(4));
		assertThat(commentDefinition.visible, is(true));				
	}
}