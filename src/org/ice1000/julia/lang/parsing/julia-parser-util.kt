package org.ice1000.julia.lang.parsing

import com.intellij.lang.*
import com.intellij.lang.parser.GeneratedParserUtilBase
import com.intellij.psi.tree.IElementType
import com.intellij.util.containers.Stack
import org.ice1000.julia.lang.JuliaElementType.Companion.LAZY_PARSEABLE_BLOCK
import org.ice1000.julia.lang.psi.JuliaTypes.*

@Suppress("UNUSED_PARAMETER")
object JuliaGeneratedParserUtilBase : GeneratedParserUtilBase() {

	private val END_SET = listOf(END_KEYWORD)

	private val PAIRS = listOf(
		LET_KEYWORD,
		FOR_KEYWORD,
		MODULE_KEYWORD,
		QUOTE_KEYWORD,
		IF_KEYWORD,
		BEGIN_KEYWORD,
		TRY_KEYWORD,
		DO_KEYWORD,
		WHILE_KEYWORD,
		FUNCTION_KEYWORD,
		TYPE_KEYWORD,
		MACRO_KEYWORD
	)

	private val LEFT_BRACKETS = listOf(LEFT_BRACKET, LEFT_M_BRACKET, LEFT_B_BRACKET)
	private val RIGHT_BRACKETS = listOf(RIGHT_BRACKET, RIGHT_M_BRACKET, RIGHT_B_BRACKET)

	/**
	 * ignore to parse last END_KEYWORD.
	 */
	@JvmStatic
	fun parseBlockLazy(builder: PsiBuilder,
										 foldableTokenTypes: List<IElementType> = PAIRS,
										 endTokenTypes: List<IElementType> = END_SET,
										 parseEnd: Boolean = false): PsiBuilder.Marker? {
		// ignore itself only once
		val marker = builder.mark()
		val parStack = Stack<IElementType>()
		val forStack = Stack<IElementType>()
		var braceCount = 1
		var lastToken: IElementType? = null
		while (braceCount > 0 && !builder.eof()) {
			val tokenType = builder.tokenType
			when (tokenType) {
				in foldableTokenTypes -> {
					if (tokenType == FOR_KEYWORD) {
						if (parStack.tryPeek() == null) { // no pairs, then forExpr
							braceCount++
						} else {
							forStack.push(tokenType) // forComprehension
						}
					} else {
						if (tokenType == MODULE_KEYWORD) {
							if (lastToken != DOT_SYM) {
								braceCount++
							}
						} else {
							braceCount++
						}
					}
					builder.advanceLexer()
				}
				in endTokenTypes -> {
					if (parStack.empty()) {
						braceCount--
					}
					advance(braceCount, builder, parseEnd)
					forStack.tryPop()
				}
				else -> {
					if (tokenType in LEFT_BRACKETS) {
						parStack.push(tokenType)
					} else if (tokenType in RIGHT_BRACKETS) {
						val top = parStack.tryPeek()
						if (top != null) {
							when {
								(top == LEFT_BRACKET && tokenType == RIGHT_BRACKET) ||
									(top == LEFT_M_BRACKET && tokenType == RIGHT_M_BRACKET) ||
									(top == LEFT_B_BRACKET && tokenType == RIGHT_B_BRACKET) -> {
									parStack.pop()
									forStack.tryPop()
								}
							}
						}
					}
					advance(braceCount, builder, parseEnd)
				}
			}
			lastToken = tokenType
		}

		marker.collapse(LAZY_PARSEABLE_BLOCK)
		if (braceCount > 0) {
			marker.setCustomEdgeTokenBinders(null, WhitespacesBinders.GREEDY_RIGHT_BINDER)
		}

		return marker
	}


	@JvmStatic
	fun lazyBlockNotParseEndImpl(builder: PsiBuilder, level: Int): Boolean {
		return parseBlockLazy(builder) != null
	}

	@JvmStatic
	fun lazyBlockParseEndImpl(builder: PsiBuilder, level: Int): Boolean {
		return parseBlockLazy(builder, parseEnd = true) != null
	}

	private fun advance(braceCount: Int, builder: PsiBuilder, parseEnd: Boolean = false) {
		if (parseEnd || braceCount != 0) {
			builder.advanceLexer()
		}
	}

	/**
	 * What a silly stack for JB so that I need to write an extension function!
	 */
	private fun <T> Stack<T>.tryPeek(): T? {
		return if (isEmpty()) null else peek()
	}
}
