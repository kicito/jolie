package joliex.meta.spec;

public interface AstInterface {

	joliex.meta.spec.types.ResolveSymbolResponse resolveSymbol( joliex.meta.spec.types.LocatedSymbolRef request )
		throws joliex.meta.spec.faults.CodeCheckException;

	joliex.meta.spec.types.Module parseModule( java.lang.String request )
		throws joliex.meta.spec.faults.CodeCheckException;
}
