/*
 *     Julia language support plugin for Intellij-based IDEs.
 *     Copyright (C) 2024 julia-intellij contributors
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.ice1000.julia.lang

import com.intellij.lang.ParserDefinition
import com.intellij.testFramework.ParsingTestCase
import org.ice1000.julia.lang.docfmt.DocfmtParserDefinition
import org.junit.Test

class JuliaParsingTest : ParsingTestCase("", JULIA_EXTENSION,
		// To make the IDE happy
		JuliaParserDefinition() as ParserDefinition) {
	override fun getTestDataPath() = "src/test/resources/parsing"
	override fun skipSpaces() = true
	fun testParsing0() = doTest(true)
	fun testParsing1() = doTest(true)
	fun testParseFunctions() = doTest(true)
	fun testParseRegexString() = doTest(true)
	fun testParseIssue135() = doTest(true)
	fun testParseIssue188() = doTest(true)
	fun testParseIssue195() = doTest(true)
	fun testParseIssue196() = doTest(true)
	fun testParseIssue204() = doTest(true)
	fun testParseIssue206() = doTest(true)
	fun testParseIssue207() = doTest(true)
	fun testParseIssue208() = doTest(true)
	fun testParseIssue212() = doTest(true)
	fun testParseIssue213() = doTest(true)
	fun testParseIssue215() = doTest(true)
	fun testParseIssue220() = doTest(true)
	fun testParseIssue223() = doTest(true)
	fun testParseIssue225() = doTest(true)
	fun testParseIssue227() = doTest(true)
	fun testParseIssue228() = doTest(true)
	fun testParseIssue232() = doTest(true)
	fun testParseIssue240() = doTest(true)
	fun testParseIssue246() = doTest(true)
	fun testParseIssue247() = doTest(true)
	fun testParseIssue250() = doTest(true)
	fun testParseIssue255() = doTest(true)
	fun testParseIssue297() = doTest(true)
	fun testParseIssue300() = doTest(true)
	fun testParseIssue312() = doTest(true)
	fun testParseIssue323() = doTest(true)
	// TODO 373
	fun testParseIssue373() = doTest(true)
	fun testParseIssue379() = doTest(true)
	fun testParseIssue426() = doTest(true)
	fun testParseEnd() = doTest(true)
	fun testParseEolAfterComma() = doTest(true)
	fun testParseEolAfterWhere() = doTest(true)
	fun testParseJuliac() = doTest(true)
	fun testParseLazy() = doTest(true)
	fun testRegex() = doTest(true)
	fun testParseRed() = doTest(true)
	fun testParseFor() = doTest(true)
	fun testParseLet() = doTest(true)
	fun testParseEscapeInsideRegEx() = doTest(true)
	fun testParseImport() = doTest(true)
	fun testParseGlobal() = doTest(true)
	fun testParseCharEscape() = doTest(true)
	fun testSoftKeywordType() = doTest(true)

	fun testComment() {
		println("我永远喜欢结城明日奈")
		doTest(true)
	}

	fun testVersionRawByteArray() {
		println("我永远喜欢时崎狂三")
		doTest(true)
	}

	fun testlearn_julia_in_Y_minutes() {

		doTest(true)
	}
}

class JuliaLexerTest {
	@Test
	fun test0() {
		JuliaLexer().let {
			// Star platinum the world!
		}
	}
}

class DocfmtParsingTest : ParsingTestCase("", DOCFMT_EXTENSION,
		DocfmtParserDefinition() as ParserDefinition) {
	override fun getTestDataPath() = "src/test/resources/parsing"
	override fun skipSpaces() = true
	fun test() {
		doTest(true)
	}
}
