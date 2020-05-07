package jolie.lang.parse.ast;

import jolie.lang.parse.module.SymbolInfo.Privacy;

public interface SymbolNode
{
    public Privacy privacy();
    public void setPrivacy(boolean isPrivate);
    public String name();
    public OLSyntaxNode node();
}