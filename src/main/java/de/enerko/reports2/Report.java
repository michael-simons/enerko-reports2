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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Implementierung der Reports auf Basis von HSSF.<br> 
 * Falls eine numerische, date oder datetime Zelle zu einer ParseException führt,
 * wird der ganze Report abgebrochen.<br>
 * Am Ende des Reports wird versucht, alle Funktionen zu evaluieren. Schlägt dies
 * fehl, so wird der Report trotzdem ausgegeben (Excel wertet in diesem Fall die Formeln 
 * aus)
 * @author Michael J. Simons, 2013-06-18
 *
 */
public class Report {
	public final static Map<String, SimpleDateFormat> DATE_FORMATS_SQL;
	public final static Map<String, String> DATE_FORMATS_EXCEL;

	static {
		final HashMap<String, SimpleDateFormat> dateFormatsSqlTmp = new HashMap<String, SimpleDateFormat>();
		dateFormatsSqlTmp.put("date", ConcreteArgument.dateFormat);
		dateFormatsSqlTmp.put("datetime", ConcreteArgument.dateTimeFormat);
		DATE_FORMATS_SQL = Collections.unmodifiableMap(dateFormatsSqlTmp);
		
		final HashMap<String, String> dateFormatsExcel = new HashMap<String, String>();
		dateFormatsExcel.put("date",    "dd/mm/yyyy");
		dateFormatsExcel.put("datetime", "dd/mm/yyyy HH:mm");			
		DATE_FORMATS_EXCEL = Collections.unmodifiableMap(dateFormatsExcel);
	}
	
	private final Workbook workbook;
	
	Report(final ReportSource reportSource) {
		this(reportSource, null);
	}
	
	Report(final ReportSource reportSource, final InputStream template) {
		if(template == null)
			this.workbook = new HSSFWorkbook();
		else
			try {
				this.workbook = new HSSFWorkbook(new BufferedInputStream(template));
			} catch(IOException e) {
				throw new RuntimeException("Konnte Template nicht laden!");
			}
	
		String previousSheetName = null;
		Sheet sheet = null;
		// Ergebnis verarbeiten
		for(CellDefinition cellDefinition : reportSource) {
			// Zwischenspeichern des aktuellen Sheets, damit nicht 
			// in jedem Loop ein getSheet durchgeführt wird			
			if(previousSheetName == null || !previousSheetName.equals(cellDefinition.sheetname)) {
				previousSheetName = cellDefinition.sheetname;
				sheet = getSheet(workbook, cellDefinition.sheetname);					
			}
			
			// Zelle erzeugen
			this.addCell(workbook, sheet, cellDefinition);
		}
		try {
			final FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
			formulaEvaluator.clearAllCachedResultValues();
			formulaEvaluator.evaluateAll();
		} catch(Exception e) {
		}
	}
	
	/**
	 * Schreibt den Report in den angegebenen OutputStream und flushed und schließt den Stream.
	 * @param out
	 * @throws IOException
	 */
	public void write(final OutputStream out) throws IOException {
		this.workbook.write(out);
		out.flush();
		out.close();
	}
	
	/**
	 * Erzeugt neues Sheet, falls Sheet mit <code>name</code> nicht vorhanden,
	 * ansonsten gibt es dieses zurück
	 * @param workbook
	 * @param name
	 * @return Sheet mit Namen <code>name</code>
	 */
	private Sheet getSheet(final Workbook workbook, final String name) {
		Sheet sheet = workbook.getSheet(name);
		if(sheet == null)			
			sheet = workbook.createSheet(name);
		return sheet;
	}
	
	private void addCell(final Workbook workbook, final Sheet sheet, final CellDefinition cellDefinition) {		
		final String type = cellDefinition.getType();
		
		final int columnNum = cellDefinition.column, rowNum = cellDefinition.row;
		
		Row row = sheet.getRow(rowNum);
		if(row == null)
			row = sheet.createRow(rowNum);
		
		Cell cell = row.getCell(columnNum);
		// Falls die Zelle bereits existiert und keine leere Zelle ist,
		// wird diese inklusive aller Formatierungen übernommen
		if(cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK) {
			cell = fill(workbook, cell, type, cellDefinition.value, false);			
		}
		// Ansonsten wird eine neue Zelle aufgebaut, der Datentyp entsprechend
		// gesetzt und im Falle von Zahlen und Datumsangaben eine Formatierung aufgebau
		else {
			cell = fill(workbook, row.createCell(columnNum), type, cellDefinition.value, true);
			
			final Sheet referenceSheet;
			if(cellDefinition.getReferenceCell() != null && (referenceSheet = workbook.getSheet(cellDefinition.getReferenceCell().sheetname)) != null) {				
				final Row referenceRow = referenceSheet.getRow(cellDefinition.getReferenceCell().row);
				final Cell referenceCell = referenceRow == null ? null : referenceRow.getCell(cellDefinition.getReferenceCell().column);				
				if(referenceCell != null && referenceCell.getCellStyle() != null)
					cell.setCellStyle(referenceCell.getCellStyle());				
			}
		}		
	}
	
	private CellStyle getFormat(final Workbook workbook, final Cell cell, String type, String value) {		
		String format = (String) DATE_FORMATS_EXCEL.get(type.toLowerCase());		
		// Type is number
		if(format == null) {
			final String[] hlp  = value.split("@@");
			format = hlp.length > 1 ? hlp[1] : "0.00####";
		}
		
		final CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.cloneStyleFrom(cell.getCellStyle());
		cellStyle.setDataFormat(workbook.createDataFormat().getFormat(format));
		return cellStyle;		
	}
	
	private SimpleDateFormat getDateFormatSql(String type) {
		return ((SimpleDateFormat)DATE_FORMATS_SQL.get(type.toLowerCase()));
	}
	
	private Cell fill(final Workbook workbook, Cell tmp, String type, String value, boolean setType) {
		if(type.equalsIgnoreCase("string")) {
			if(setType)
				tmp.setCellType(Cell.CELL_TYPE_STRING);
			tmp.setCellValue(value);						
		} else if(type.equalsIgnoreCase("number")) {
			if(setType) {
				tmp.setCellType(Cell.CELL_TYPE_NUMERIC);
				tmp.setCellStyle(getFormat(workbook, tmp, type, value));
			}
			try {
				tmp.setCellValue(Double.parseDouble(value.split("@@")[0]));
			} catch(NumberFormatException e) {
				throw new RuntimeException(String.format("Could not parse value \"%s\" for numeric cell %dx%d!", value, tmp.getColumnIndex(), tmp.getRowIndex()));
			}
		} else if(type.equalsIgnoreCase("date") || type.equalsIgnoreCase("datetime")) {
			if(setType) {
				tmp.setCellType(Cell.CELL_TYPE_NUMERIC);
				tmp.setCellStyle(getFormat(workbook, tmp, type, value));
			}
			try {
				tmp.setCellValue(getDateFormatSql(type).parse(value));
			} catch(ParseException e) {
				throw new RuntimeException(String.format("Could not parse value \"%s\" for date/datetime cell %dx%d!", value, tmp.getColumnIndex(), tmp.getRowIndex()));
			}
		} else if(type.equalsIgnoreCase("formula")) {
			if(setType)
				tmp.setCellType(Cell.CELL_TYPE_FORMULA);
			tmp.setCellFormula(value);			
		} else
			throw new RuntimeException("Invalid type " + type);
		return tmp;
	}
}