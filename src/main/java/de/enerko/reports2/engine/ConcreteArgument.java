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

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * A composition of a {@link FormalArgument} and a concrete {@link #value} into a
 * concrete argument.
 * @author Michael J. Simons, 2013-06-18
 */
public class ConcreteArgument {
	public final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
	public final static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	
	/** The formal argument that represents datatype and position in the method call */
	private final FormalArgument formalArgument;
	/** The string representation of the value */
	private final String value;
	/** A flag if the actual value is null */
	private final boolean isNull;
	
	public ConcreteArgument(FormalArgument formalArgument, String value) {
		this.formalArgument = formalArgument;
		this.value = value;
		this.isNull = this.value == null || this.value.trim().length() == 0;
	}
	
	/**
	 * Stores the concrete value {@link #value} into the given {@link PreparedStatement}
	 * at position {@link FormalArgument#position}
	 * @param ps The prepared statement to store the value into
	 */
	public void setTo(final PreparedStatement ps) {
		if(formalArgument.dataType == null)
			throw new IllegalArgumentException(String.format("No dataType for argument \"%s\"", formalArgument.name));
		try{
			switch(formalArgument.dataType) {			
				case varchar2:
					if(isNull)
						ps.setNull(this.formalArgument.position, Types.VARCHAR);
					else
						ps.setString(this.formalArgument.position, value);
					break;
				case number:				
					if(isNull)
						ps.setNull(this.formalArgument.position, Types.NUMERIC);
					else
						ps.setBigDecimal(this.formalArgument.position, new BigDecimal(value));
					break;					
				case date:				
					if(isNull)
						ps.setNull(this.formalArgument.position, Types.DATE);
					else
						ps.setDate(this.formalArgument.position, new Date(dateFormat.parse(value).getTime()));
					break;
				case timestamp:				
					if(isNull)
						ps.setNull(this.formalArgument.position, Types.DATE);
					else
						ps.setTimestamp(this.formalArgument.position, new Timestamp(dateTimeFormat.parse(value).getTime()));
					break;
				default:
					throw new IllegalArgumentException(String.format("Datatype \"%s\" is not supported!", formalArgument.dataType));
						
			}
		} catch(NumberFormatException e) {
			throw uncheck(e);
		} catch(ParseException e) {
			throw uncheck(e);
		} catch(SQLException e) {
			throw uncheck(e);
		}
	}	
}