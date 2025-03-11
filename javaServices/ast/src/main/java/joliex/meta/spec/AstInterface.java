package joliex.meta.spec;

public interface AstInterface {

	joliex.meta.spec.types.ResolveSymbolResponse resolveSymbol( joliex.meta.spec.types.LocatedSymbolRef request );

	joliex.meta.spec.types.Module parseModule( java.lang.String request );
}
