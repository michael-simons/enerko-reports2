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

import static org.hamcrest.core.Is.is;

import org.junit.Assert;
import org.junit.Test;

import de.enerko.hre.ArgumentResolver.MethodNotFoundException;

/**
 * @author Michael J. Simons, 2013-06-17
 */
public class ArgumentResolverTest extends AbstractDatabaseTest {	
	@Test
	public void shouldHandleNoArgs() {
		
		final ArgumentResolver argumentResolver = new ArgumentResolver(connection);
		// In Packages
		Assert.assertThat(argumentResolver.getArguments("pck_hre_test.f_no_args").size(), is(0));
		Assert.assertThat(argumentResolver.getArguments("pck_hre_test.p_no_args").size(), is(0));
		// und Standalone
		Assert.assertThat(argumentResolver.getArguments("f_arg_resolver_test_no_args").size(), is(0));
		Assert.assertThat(argumentResolver.getArguments("p_arg_resolver_test_no_args").size(), is(0));
	}
	
	@Test
	public void shouldHandleArgs() {
		final ArgumentResolver argumentResolver = new ArgumentResolver(connection);
		// In Packages
		Assert.assertThat(argumentResolver.getArguments("pck_hre_test.f_some_args").size(), is(4));
		Assert.assertThat(argumentResolver.getArguments("pck_hre_test.p_some_args").size(), is(4));
		Assert.assertThat(argumentResolver.getArguments("pck_hre_test.f_fb_report_source_test").size(), is(3));
		// und Standalone
		Assert.assertThat(argumentResolver.getArguments("f_arg_resolver_test_some_args").size(), is(4));
		Assert.assertThat(argumentResolver.getArguments("p_arg_resolver_test_some_args").size(), is(4));
	}
	
	@Test(expected=MethodNotFoundException.class)
	public void shouldHandleNotExistingMethods() {
		final ArgumentResolver argumentResolver = new ArgumentResolver(connection);
		argumentResolver.getArguments("foobar");
	}
	
	@Test(expected=MethodNotFoundException.class)
	public void shouldHandleNotExistingMethodsInPackages() {
		final ArgumentResolver argumentResolver = new ArgumentResolver(connection);
		argumentResolver.getArguments("foo.bar");
	}
}