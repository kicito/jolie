package joliex.meta.spec.types;

/**
 * This is a sealed interface representing the following choice type:
 *
 * <pre>
 * ResolveSymbolResponse: joliex.meta.spec.types.TypeDef | joliex.meta.spec.types.InterfaceDef | joliex.meta.spec.types.ServiceDef
 * </pre>
 *
 * @see jolie.runtime.embedding.java.JolieValue
 * @see jolie.runtime.embedding.java.JolieNative
 * @see joliex.meta.spec.types.TypeDef
 * @see joliex.meta.spec.types.InterfaceDef
 * @see joliex.meta.spec.types.ServiceDef
 * @see #of1(joliex.meta.spec.types.TypeDef)
 * @see #of2(joliex.meta.spec.types.InterfaceDef)
 * @see #of3(joliex.meta.spec.types.ServiceDef)
 */
public sealed interface ResolveSymbolResponse extends jolie.runtime.embedding.java.JolieValue {

	jolie.runtime.Value jolieRepr();

	public static record C1(joliex.meta.spec.types.TypeDef option) implements ResolveSymbolResponse {

		public C1 {
			jolie.runtime.embedding.java.util.ValueManager.validated( "option", option );
		}

		public jolie.runtime.embedding.java.JolieNative.JolieVoid content() {
			return option.content();
		}

		public java.util.Map< java.lang.String, java.util.List< jolie.runtime.embedding.java.JolieValue > > children() {
			return option.children();
		}

		public jolie.runtime.Value jolieRepr() {
			return joliex.meta.spec.types.TypeDef.toValue( option );
		}

		public boolean equals( java.lang.Object obj ) {
			return obj != null && obj instanceof jolie.runtime.embedding.java.JolieValue j && option.equals( j );
		}

		public int hashCode() {
			return option.hashCode();
		}

		public java.lang.String toString() {
			return option.toString();
		}

		public static C1 from( jolie.runtime.embedding.java.JolieValue j )
			throws jolie.runtime.embedding.java.TypeValidationException {
			return new C1( joliex.meta.spec.types.TypeDef.from( j ) );
		}

		public static C1 fromValue( jolie.runtime.Value v ) throws jolie.runtime.typing.TypeCheckingException {
			return new C1( joliex.meta.spec.types.TypeDef.fromValue( v ) );
		}

		public static jolie.runtime.Value toValue( C1 t ) {
			return t.jolieRepr();
		}
	}

	public static record C2(joliex.meta.spec.types.InterfaceDef option) implements ResolveSymbolResponse {

		public C2 {
			jolie.runtime.embedding.java.util.ValueManager.validated( "option", option );
		}

		public jolie.runtime.embedding.java.JolieNative.JolieVoid content() {
			return option.content();
		}

		public java.util.Map< java.lang.String, java.util.List< jolie.runtime.embedding.java.JolieValue > > children() {
			return option.children();
		}

		public jolie.runtime.Value jolieRepr() {
			return joliex.meta.spec.types.InterfaceDef.toValue( option );
		}

		public boolean equals( java.lang.Object obj ) {
			return obj != null && obj instanceof jolie.runtime.embedding.java.JolieValue j && option.equals( j );
		}

		public int hashCode() {
			return option.hashCode();
		}

		public java.lang.String toString() {
			return option.toString();
		}

		public static C2 from( jolie.runtime.embedding.java.JolieValue j )
			throws jolie.runtime.embedding.java.TypeValidationException {
			return new C2( joliex.meta.spec.types.InterfaceDef.from( j ) );
		}

		public static C2 fromValue( jolie.runtime.Value v ) throws jolie.runtime.typing.TypeCheckingException {
			return new C2( joliex.meta.spec.types.InterfaceDef.fromValue( v ) );
		}

		public static jolie.runtime.Value toValue( C2 t ) {
			return t.jolieRepr();
		}
	}

	public static record C3(joliex.meta.spec.types.ServiceDef option) implements ResolveSymbolResponse {

		public C3 {
			jolie.runtime.embedding.java.util.ValueManager.validated( "option", option );
		}

		public jolie.runtime.embedding.java.JolieNative.JolieVoid content() {
			return option.content();
		}

		public java.util.Map< java.lang.String, java.util.List< jolie.runtime.embedding.java.JolieValue > > children() {
			return option.children();
		}

		public jolie.runtime.Value jolieRepr() {
			return joliex.meta.spec.types.ServiceDef.toValue( option );
		}

		public boolean equals( java.lang.Object obj ) {
			return obj != null && obj instanceof jolie.runtime.embedding.java.JolieValue j && option.equals( j );
		}

		public int hashCode() {
			return option.hashCode();
		}

		public java.lang.String toString() {
			return option.toString();
		}

		public static C3 from( jolie.runtime.embedding.java.JolieValue j )
			throws jolie.runtime.embedding.java.TypeValidationException {
			return new C3( joliex.meta.spec.types.ServiceDef.from( j ) );
		}

		public static C3 fromValue( jolie.runtime.Value v ) throws jolie.runtime.typing.TypeCheckingException {
			return new C3( joliex.meta.spec.types.ServiceDef.fromValue( v ) );
		}

		public static jolie.runtime.Value toValue( C3 t ) {
			return t.jolieRepr();
		}
	}

	public static ListBuilder listBuilder() {
		return new ListBuilder();
	}

	public static ListBuilder listBuilder(
		java.util.SequencedCollection< jolie.runtime.embedding.java.JolieValue > from ) {
		return from != null ? new ListBuilder( from ) : listBuilder();
	}

	public static ResolveSymbolResponse of1( joliex.meta.spec.types.TypeDef option ) {
		return new C1( option );
	}

	public static ResolveSymbolResponse of1(
		java.util.function.Function< joliex.meta.spec.types.TypeDef.Builder, joliex.meta.spec.types.TypeDef > f ) {
		return of1( f.apply( joliex.meta.spec.types.TypeDef.builder() ) );
	}

	public static ResolveSymbolResponse of2( joliex.meta.spec.types.InterfaceDef option ) {
		return new C2( option );
	}

	public static ResolveSymbolResponse of2(
		java.util.function.Function< joliex.meta.spec.types.InterfaceDef.Builder, joliex.meta.spec.types.InterfaceDef > f ) {
		return of2( f.apply( joliex.meta.spec.types.InterfaceDef.builder() ) );
	}

	public static ResolveSymbolResponse of3( joliex.meta.spec.types.ServiceDef option ) {
		return new C3( option );
	}

	public static ResolveSymbolResponse of3(
		java.util.function.Function< joliex.meta.spec.types.ServiceDef.Builder, joliex.meta.spec.types.ServiceDef > f ) {
		return of3( f.apply( joliex.meta.spec.types.ServiceDef.builder() ) );
	}

	public static ResolveSymbolResponse from( jolie.runtime.embedding.java.JolieValue j )
		throws jolie.runtime.embedding.java.TypeValidationException {
		return jolie.runtime.embedding.java.util.ValueManager.choiceFrom( j,
			java.util.List.of( jolie.runtime.embedding.java.util.ValueManager.castFunc( C1::from ),
				jolie.runtime.embedding.java.util.ValueManager.castFunc( C2::from ),
				jolie.runtime.embedding.java.util.ValueManager.castFunc( C3::from ) ) );
	}

	public static ResolveSymbolResponse fromValue( jolie.runtime.Value v )
		throws jolie.runtime.typing.TypeCheckingException {
		return jolie.runtime.embedding.java.util.ValueManager.choiceFrom( v,
			java.util.List.of( jolie.runtime.embedding.java.util.ValueManager.castFunc( C1::fromValue ),
				jolie.runtime.embedding.java.util.ValueManager.castFunc( C2::fromValue ),
				jolie.runtime.embedding.java.util.ValueManager.castFunc( C3::fromValue ) ) );
	}

	public static jolie.runtime.Value toValue( ResolveSymbolResponse t ) {
		return t.jolieRepr();
	}

	public static class ListBuilder
		extends jolie.runtime.embedding.java.util.AbstractListBuilder< ListBuilder, ResolveSymbolResponse > {

		private ListBuilder() {}

		private ListBuilder( java.util.SequencedCollection< ? extends jolie.runtime.embedding.java.JolieValue > c ) {
			super( c, ResolveSymbolResponse::from );
		}

		protected ListBuilder self() {
			return this;
		}

		public ListBuilder add1( joliex.meta.spec.types.TypeDef option ) {
			return add( new C1( option ) );
		}

		public ListBuilder add1( int index, joliex.meta.spec.types.TypeDef option ) {
			return add( index, new C1( option ) );
		}

		public ListBuilder set1( int index, joliex.meta.spec.types.TypeDef option ) {
			return set( index, new C1( option ) );
		}

		public ListBuilder add1(
			java.util.function.Function< joliex.meta.spec.types.TypeDef.Builder, joliex.meta.spec.types.TypeDef > b ) {
			return add1( b.apply( joliex.meta.spec.types.TypeDef.builder() ) );
		}

		public ListBuilder add1( int index,
			java.util.function.Function< joliex.meta.spec.types.TypeDef.Builder, joliex.meta.spec.types.TypeDef > b ) {
			return add1( index, b.apply( joliex.meta.spec.types.TypeDef.builder() ) );
		}

		public ListBuilder set1( int index,
			java.util.function.Function< joliex.meta.spec.types.TypeDef.Builder, joliex.meta.spec.types.TypeDef > b ) {
			return set1( index, b.apply( joliex.meta.spec.types.TypeDef.builder() ) );
		}

		public ListBuilder add2( joliex.meta.spec.types.InterfaceDef option ) {
			return add( new C2( option ) );
		}

		public ListBuilder add2( int index, joliex.meta.spec.types.InterfaceDef option ) {
			return add( index, new C2( option ) );
		}

		public ListBuilder set2( int index, joliex.meta.spec.types.InterfaceDef option ) {
			return set( index, new C2( option ) );
		}

		public ListBuilder add2(
			java.util.function.Function< joliex.meta.spec.types.InterfaceDef.Builder, joliex.meta.spec.types.InterfaceDef > b ) {
			return add2( b.apply( joliex.meta.spec.types.InterfaceDef.builder() ) );
		}

		public ListBuilder add2( int index,
			java.util.function.Function< joliex.meta.spec.types.InterfaceDef.Builder, joliex.meta.spec.types.InterfaceDef > b ) {
			return add2( index, b.apply( joliex.meta.spec.types.InterfaceDef.builder() ) );
		}

		public ListBuilder set2( int index,
			java.util.function.Function< joliex.meta.spec.types.InterfaceDef.Builder, joliex.meta.spec.types.InterfaceDef > b ) {
			return set2( index, b.apply( joliex.meta.spec.types.InterfaceDef.builder() ) );
		}

		public ListBuilder add3( joliex.meta.spec.types.ServiceDef option ) {
			return add( new C3( option ) );
		}

		public ListBuilder add3( int index, joliex.meta.spec.types.ServiceDef option ) {
			return add( index, new C3( option ) );
		}

		public ListBuilder set3( int index, joliex.meta.spec.types.ServiceDef option ) {
			return set( index, new C3( option ) );
		}

		public ListBuilder add3(
			java.util.function.Function< joliex.meta.spec.types.ServiceDef.Builder, joliex.meta.spec.types.ServiceDef > b ) {
			return add3( b.apply( joliex.meta.spec.types.ServiceDef.builder() ) );
		}

		public ListBuilder add3( int index,
			java.util.function.Function< joliex.meta.spec.types.ServiceDef.Builder, joliex.meta.spec.types.ServiceDef > b ) {
			return add3( index, b.apply( joliex.meta.spec.types.ServiceDef.builder() ) );
		}

		public ListBuilder set3( int index,
			java.util.function.Function< joliex.meta.spec.types.ServiceDef.Builder, joliex.meta.spec.types.ServiceDef > b ) {
			return set3( index, b.apply( joliex.meta.spec.types.ServiceDef.builder() ) );
		}

		public java.util.List< ResolveSymbolResponse > build() {
			return super.build();
		}
	}
}
