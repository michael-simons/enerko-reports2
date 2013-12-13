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

import org.apache.poi.ss.usermodel.Comment;

/**
 * @author Michael J. Simons, 2013-12-13
 */
public class CommentDefinition {
	public final String author;
	public final String text;
	public final Integer column;
	public final Integer row;
	/** Only used when creating comments */
	public final int width;
	/** Only used when creating comments */ 
	public final int height;
	public final boolean visible;
	
	public CommentDefinition(final Comment comment) {		
		this(comment.getAuthor(), comment.getString().getString(), comment.getColumn(), comment.getRow(), -1, -1, comment.isVisible());
	}
	
	public CommentDefinition(String author, String text, final Integer column, final Integer row,  Integer width, Integer height, boolean visible) {
		this.author = author;
		this.text = text;
		this.column = column == null ? null : column;
		this.row = row == null ? null : row;
		this.width = width == null ? 1 : width;
		this.height = height == null ? 1 : height;
		this.visible = visible;
	}	
}