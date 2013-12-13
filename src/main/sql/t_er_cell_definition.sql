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
BEGIN EXECUTE immediate 'DROP TYPE t_er_cell_definition FORCE'; EXCEPTION WHEN others THEN IF SQLCODE != -4043 THEN RAISE; END IF; END;
/

CREATE TYPE t_er_cell_definition AS OBJECT (
  sheetname         VARCHAR2(512),
  cell_column       INTEGER,
  cell_row          INTEGER,
  cell_name         VARCHAR2(64),
  cell_type         VARCHAR2(512),    -- string|number|date|datetime
  cell_value        VARCHAR2(32767),  -- Textuelle Repräsentation des Wertes (number mit '.', date im Format DD.MM.YYYY, datetime im Format DD.MM.YYYY HH24:MI
  cell_comment      VARCHAR2(32767),  -- Optionaler Kommentar fuer die Zelle
  comment_author    VARCHAR2(32767),  -- Optionaler Autor des Kommentars
  comment_column    INTEGER,          -- Spalte Kommentar (Default: Spalte Zelle + 1)
  comment_row       INTEGER,          -- Zeile Kommentar (Default: Zeile Zelle)
  comment_width     INTEGER,          -- Breite des Kommentars (default 1)
  comment_height    INTEGER,          -- Hoehe des Kommentars (default 1)
  comment_visible   VARCHAR2(8),      -- Flag, ob Kommentar sichtbar ist oder nicht (true, false), default false

  CONSTRUCTOR FUNCTION t_er_cell_definition(
    p_sheetname         VARCHAR2,
    p_cell_column       INTEGER,
    p_cell_row          INTEGER,
    p_cell_type         VARCHAR2,
    p_cell_value        VARCHAR2
  ) RETURN self AS result,
  
  CONSTRUCTOR FUNCTION t_er_cell_definition(
    p_sheetname         VARCHAR2,
    p_cell_column       INTEGER,
    p_cell_row          INTEGER,
    p_cell_name         VARCHAR2,
    p_cell_type         VARCHAR2,
    p_cell_value        VARCHAR2
  ) RETURN self AS result,
  
   CONSTRUCTOR FUNCTION t_er_cell_definition(
    p_sheetname         VARCHAR2,
    p_cell_column       INTEGER,
    p_cell_row          INTEGER,
    p_cell_name         VARCHAR2,
    p_cell_type         VARCHAR2,
    p_cell_value        VARCHAR2,
    p_comment           VARCHAR2
  ) RETURN self AS result
)
/

CREATE OR REPLACE TYPE BODY t_er_cell_definition AS
	CONSTRUCTOR FUNCTION t_er_cell_definition(
    p_sheetname         VARCHAR2,
    p_cell_column       INTEGER,
    p_cell_row          INTEGER,
    p_cell_type         VARCHAR2,
    p_cell_value        VARCHAR2
  ) RETURN self AS result IS
  BEGIN
  	self.sheetname   := p_sheetname;
    self.cell_column := p_cell_column;
    self.cell_row    := p_cell_row;   
    self.cell_name   := null; 
    self.cell_type   := p_cell_type;
    self.cell_value  := p_cell_value;
    
    self.cell_comment  := null;
    self.comment_author:= user;
    RETURN;
  END t_er_cell_definition;
  
  CONSTRUCTOR FUNCTION t_er_cell_definition(
    p_sheetname         VARCHAR2,
    p_cell_column       INTEGER,
    p_cell_row          INTEGER,
    p_cell_name         VARCHAR2,
    p_cell_type         VARCHAR2,
    p_cell_value        VARCHAR2
  ) RETURN self AS result IS
  BEGIN
  	self.sheetname   := p_sheetname;
    self.cell_column := p_cell_column;
    self.cell_row    := p_cell_row;   
    self.cell_name   := p_cell_name; 
    self.cell_type   := p_cell_type;
    self.cell_value  := p_cell_value;
    
    self.cell_comment  := null;
    self.comment_author:= user;
    RETURN;
  END t_er_cell_definition;    
  
  CONSTRUCTOR FUNCTION t_er_cell_definition(
    p_sheetname         VARCHAR2,
    p_cell_column       INTEGER,
    p_cell_row          INTEGER,
    p_cell_name         VARCHAR2,
    p_cell_type         VARCHAR2,
    p_cell_value        VARCHAR2,
    p_comment           VARCHAR2
  ) RETURN self AS result IS
  BEGIN
  	self.sheetname   := p_sheetname;
    self.cell_column := p_cell_column;
    self.cell_row    := p_cell_row;   
    self.cell_name   := p_cell_name; 
    self.cell_type   := p_cell_type;
    self.cell_value  := p_cell_value;
    
    self.cell_comment  := p_comment;
    self.comment_author:= user;    
    RETURN;
  END t_er_cell_definition;  
END;
/