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
package de.enerko.hre;

/**
 * Repräsentiert einen formalen Parameter einer Oracle Datenbank Funktion / Prozedur
 * @author Michael J. Simons, 2013-06-17
 */
public class FormalArgument implements Comparable<FormalArgument> {
	/** Unterstützte Datentypen von Funktions / Methodenparametern */
	public static enum DataType {
		varchar2, date, timestamp, number
	}
	
	/** Position in der Parameterliste */
	public final int position;
	/** Name (eines benannten Parameters) */
	public final String name;
	/** Name des Datentyp (wie im Oracle Datadictionary */
	public final String dataTypeName;
	/** Unterstützter Datentyp für den dynamischen Aufruf */
	public final DataType dataType;
	
	public FormalArgument(int position, String name, final String dataTypeName) {
		this.position = position;
		this.name = name;
		this.dataTypeName = dataTypeName;
		DataType hlp = null;
		try {
			if(this.dataTypeName != null)
			hlp = DataType.valueOf(this.dataTypeName.trim().toLowerCase());
		} catch(IllegalArgumentException e) {
			// Wird nicht in einem dynamischen Aufruf unterstützt
		} catch(NullPointerException e) {			
			// Wird nicht in einem dynamischen Aufruf unterstützt
		}
		this.dataType = hlp;
	}

	public int compareTo(FormalArgument o) {
		return this.position - o.position;
	}
}