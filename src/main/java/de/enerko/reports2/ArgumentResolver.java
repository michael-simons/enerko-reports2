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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;

/**
 * This is a heloer class that retrives arguments vor PL/SQL procedures and functions
 * @author Michael J. Simons, 2013-06-17
 */
public class ArgumentResolver {
	/**
	 * Thrown if a procedure or function is not found in global or package scope
	 */
	public static class MethodNotFoundException extends RuntimeException {
		private static final long serialVersionUID = 1044851139822866147L;
		
		public final String methodName;

		public MethodNotFoundException(String methodName) {
			super(String.format("Method '%s' not found!", methodName));
			this.methodName = methodName;
		}
	}
	
	private final OracleConnection connection;

	public ArgumentResolver(OracleConnection connection) {
		this.connection = connection;
	}
	
	/**
	 * Determines the list of all arguments of a given Oracle PL/SQL procedure or function.
	 * 
	 * @param fqn The fully qualified name of a procedure or function 
	 * @return a list of parameters (all real arguments, not the type of a function)
	 */
	public List<FormalArgument> getArguments(final String fqn) {
		final List<FormalArgument> rv = new ArrayList<FormalArgument>();
		
		String packageName = null;
		String procedureName = null;
		if(fqn.indexOf('.') < 0)
			procedureName = fqn;
		else {
			final String[] tmp = fqn.split(Pattern.quote("."));
			packageName = tmp[0];
			procedureName = tmp[1];
		}
		
		OraclePreparedStatement statement = null;
		ResultSet rs = null;
		try {			
			// check if the procedure or function exists
			statement = (OraclePreparedStatement) this.connection.prepareStatement(
					"Select count(*) as cnt " +
					"  from user_procedures p " +
					" where " +
					"   (" +
					"     lower(p.object_name) = lower(:package_name) and " +
					"     lower(p.procedure_name) = lower(:procedure_name) " +
					"   ) " +
					"   or lower(p.object_name) = lower(:procedure_name)"
			);
			statement.setStringAtName("package_name", packageName);
			statement.setStringAtName("procedure_name", procedureName);			
			rs = statement.executeQuery();
			rs.next();
			if(rs.getInt("cnt") != 1)
				throw new MethodNotFoundException(fqn);
			rs.close();
			statement.close();
			
			// get arguments
			statement = (OraclePreparedStatement) this.connection.prepareStatement(
					"Select a.position as position, " + 
					"       lower(a.argument_name) as argument_name, " + 
				    "       a.type_name, " +
				    "       a.data_type, " +
				    "       decode(DEFAULTED, 'Y', 'true', 'false') as defaulted " +
				    "  from user_arguments a " +
				    "  join user_procedures p on (p.object_name = a.package_name and p.procedure_name = a.object_name or a.package_name IS NULL and p.object_name = a.object_name) " + 
				    " where a.position > 0 " + // don't retrieve the type of a function 
				    "   and a.sequence >= 1 " + // and not it's name
				    "   and a.argument_name is not null " + // don't retrive the type of a pipelined function
				    "   and lower(nvl(a.package_name, '-')) = lower(nvl(:package_name, '-')) and lower(a.object_name) = lower(:procedure_name) " +
				    " order by a.position asc"
			);
			statement.setStringAtName("package_name", packageName);
			statement.setStringAtName("procedure_name", procedureName);			
			rs = statement.executeQuery();
			while(rs.next())
				rv.add(new FormalArgument(rs.getInt("position"), rs.getString("argument_name"), rs.getString("data_type")));
			rs.close();
			statement.close();
		} catch(SQLException e) {
			throw uncheck(e);
		} finally {
			try {
				if(rs != null)  
					rs.close();
			} catch (SQLException e) {
			}
				
			try {
				if(statement != null) 				
					statement.close();
			} catch (SQLException e) {			
			}
		}
		
		Collections.sort(rv);
		return rv;
	}
}