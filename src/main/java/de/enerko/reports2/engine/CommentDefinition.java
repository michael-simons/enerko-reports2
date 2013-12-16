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

import static de.enerko.reports2.utils.Types.numberToInteger;

import java.sql.SQLException;
import java.sql.Struct;

import org.apache.poi.ss.usermodel.Comment;

/**
 * @author Michael J. Simons, 2013-12-13
 */
public class CommentDefinition {	
	public final String text;
	public final String author;
	public final Integer column;
	public final Integer row;
	/** Only used when creating comments */
	public final int width;
	/** Only used when creating comments */ 
	public final int height;
	public final boolean visible;
	
	public static CommentDefinition fromStruct(final Struct struct) throws SQLException {
		CommentDefinition rv = null;
		if(struct != null) {
			// Attributes are retrieved as they are declared in t_er_comment_definition
			// could cast to STRUCT and use ResultSetMetaData but i don't see the point
			final Object[] attributes = struct.getAttributes();					
			rv = new CommentDefinition(
					(String)attributes[0],
					(String)attributes[1],
					numberToInteger((Number)attributes[2]),
					numberToInteger((Number)attributes[3]),
					numberToInteger((Number)attributes[4]),
					numberToInteger((Number)attributes[5]),
					Boolean.parseBoolean((String)attributes[6])
			);
		}
		return rv;
	}
	
	public CommentDefinition(final Comment comment) {		
		this(comment.getString().getString(), comment.getAuthor(), comment.getColumn(), comment.getRow(), -1, -1, comment.isVisible());
	}
	
	public CommentDefinition(String text, String author, final Integer column, final Integer row,  final Integer width, final Integer height, boolean visible) {		
		this.text = text;
		this.author = author;
		this.column = column == null ? null : column;
		this.row = row == null ? null : row;
		this.width = width == null ? 1 : width;
		this.height = height == null ? 1 : height;
		this.visible = visible;
	}
	
	public Object[] toSQLStructObject() {
		return new Object[] {
				this.text,
				this.author,
				this.column,
				this.row,
				this.width,
				this.height,
				Boolean.toString(this.visible)
		};
	}
}