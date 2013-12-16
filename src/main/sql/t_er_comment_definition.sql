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
BEGIN EXECUTE immediate 'DROP TYPE t_er_comment_definition FORCE'; EXCEPTION WHEN others THEN IF SQLCODE != -4043 THEN RAISE; END IF; END;
/

CREATE TYPE t_er_comment_definition AS OBJECT (
  comment_text      VARCHAR2(32767),  -- Kommentartext
  comment_author    VARCHAR2(32767),  -- Optionaler Autor des Kommentars
  comment_column    INTEGER,          -- Spalte Kommentar (Default: Spalte Zelle + 1)
  comment_row       INTEGER,          -- Zeile Kommentar (Default: Zeile Zelle)
  comment_width     INTEGER,          -- Breite des Kommentars (default 1)
  comment_height    INTEGER,          -- Hoehe des Kommentars (default 1)
  comment_visible   VARCHAR2(8),      -- Flag, ob Kommentar sichtbar ist oder nicht (true, false), default false
  
  CONSTRUCTOR FUNCTION t_er_comment_definition(
	p_text    VARCHAR2,
	p_column  INTEGER  DEFAULT NULL,
	p_row     INTEGER  DEFAULT NULL,
	p_width   INTEGER  DEFAULT 1,
	p_height  INTEGER  DEFAULT 1,
	p_visible VARCHAR2 DEFAULT 'false'
  ) RETURN SELF AS RESULT
)
/

CREATE OR REPLACE TYPE BODY t_er_comment_definition AS
  CONSTRUCTOR FUNCTION t_er_comment_definition(
	p_text    VARCHAR2,
	p_column  INTEGER  DEFAULT NULL,
	p_row     INTEGER  DEFAULT NULL,
	p_width   INTEGER  DEFAULT 1,
	p_height  INTEGER  DEFAULT 1,
	p_visible VARCHAR2 DEFAULT 'false'
  ) RETURN SELF AS RESULT IS
  BEGIN
	self.comment_text   := p_text;
	self.comment_author := user;    
	self.comment_column := p_column;
	self.comment_row    := p_row;
	self.comment_width  := p_width;
	self.comment_height := p_height;
	self.comment_visible:= p_visible;
	RETURN;
  END t_er_comment_definition;
END;
/
