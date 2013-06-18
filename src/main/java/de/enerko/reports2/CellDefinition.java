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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Repräsentiert eine Zelle eines Worksheets und korrespondiert
 * mit dem PL/SQL Type t_hre_cell_definition
 * @author Michael J. Simons, 2013-06-17
 */
public class CellDefinition {	
	public static class CellPointer {
		public final String sheetname;
		public final int column;
		public final int row;
		
		public CellPointer(String sheetname, int column, int row) {
			this.sheetname = sheetname;
			this.column = column;
			this.row = row;
		}

		@Override
		public String toString() {
			return "CellPointer [sheetname=" + sheetname + ", column=" + column
					+ ", row=" + row + "]";
		}					
	}
	
	/** Dient dazu, Typ und eine optionale Referenzzelle aus #type zu ermitteln */
	public final static Pattern FORMAT_PATTERN = Pattern.compile("(\\w+)(\\s*;\\s*\"([^\"]+)\"\\s*(\\w{1,3}\\d{1,}))?");
	
	public final String sheetname;
	public final int column;
	public final int row;
	public final String name;
	/** Kann Typ aber auch Formatvorlage bzw. Referenzzelle in Form 'datentyp; "Name des Worksheets" SPALTEZEILE' enthalten */
	private final String type;
	public final String value;
	
	/** Tatsächlicher Datentyp */
	private String actualType;
	/** Referenzzelle */
	private CellPointer referenceCell;
	
	public CellDefinition(String sheetname, int column, int row, String name, String type, String value) {
		this.sheetname = sheetname;
		this.column = column;
		this.row = row;
		this.name = name;
		this.type = type;
		this.value = value;
	}	
	
	public String getType() {
		if(this.actualType == null)
			this.computeActualTypeAndReferenceCell();	
		return this.actualType;
	}
	
	public CellPointer getReferenceCell() {
		if(this.actualType == null)
			this.computeActualTypeAndReferenceCell();
		return referenceCell;
	}

	public void setReferenceCell(CellPointer referenceCell) {
		this.referenceCell = referenceCell;
	}

	private void computeActualTypeAndReferenceCell() {
		final Matcher m = FORMAT_PATTERN.matcher(this.type);
		if(!m.matches())
			throw new RuntimeException("Invalid type definition: " + type);
		this.actualType = m.group(1);		
		this.referenceCell = m.group(2) == null ? null :new CellPointer(m.group(3), CellReferenceHelper.getColumn(m.group(4)), CellReferenceHelper.getRow(m.group(4)));		
	}
}