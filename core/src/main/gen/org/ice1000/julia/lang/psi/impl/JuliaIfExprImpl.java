// This is a generated file. Not intended for manual editing.
package org.ice1000.julia.lang.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static org.ice1000.julia.lang.psi.JuliaTypes.*;
import org.ice1000.julia.lang.psi.*;

public class JuliaIfExprImpl extends JuliaExprImpl implements JuliaIfExpr {

  public JuliaIfExprImpl(ASTNode node) {
    super(node);
  }

  @Override
  public <R> R accept(@NotNull JuliaVisitor<R> visitor) {
    return visitor.visitIfExpr(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JuliaVisitor) accept((JuliaVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @NotNull
  public List<JuliaComment> getCommentList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JuliaComment.class);
  }

  @Override
  @Nullable
  public JuliaElseClause getElseClause() {
    return PsiTreeUtil.getChildOfType(this, JuliaElseClause.class);
  }

  @Override
  @NotNull
  public List<JuliaElseIfClause> getElseIfClauseList() {
    return PsiTreeUtil.getChildrenOfTypeAsList(this, JuliaElseIfClause.class);
  }

  @Override
  @Nullable
  public JuliaExpr getExpr() {
    return PsiTreeUtil.getChildOfType(this, JuliaExpr.class);
  }

  @Override
  @Nullable
  public JuliaStatements getStatements() {
    return PsiTreeUtil.getChildOfType(this, JuliaStatements.class);
  }

}
