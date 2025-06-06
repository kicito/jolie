package joliex.meta.spec.types;

/**
 * this class is a {@link jolie.runtime.embedding.java.TypedStructure} which can be described as
 * follows:
 *
 * <pre>
 * textLocation: {@link joliex.meta.spec.types.Location}
 * operations[0,2147483647]: {@link joliex.meta.spec.types.Operation}
 * name: {@link joliex.meta.spec.types.LocatedString}
 * </pre>
 *
 * @see jolie.runtime.embedding.java.JolieValue
 * @see jolie.runtime.embedding.java.JolieNative
 * @see joliex.meta.spec.types.Location
 * @see joliex.meta.spec.types.Operation
 * @see joliex.meta.spec.types.LocatedString
 * @see #builder()
 */
public final class InterfaceDef extends jolie.runtime.embedding.java.TypedStructure {

	private static final java.util.Set< java.lang.String > FIELD_KEYS = fieldKeys( InterfaceDef.class );

	@jolie.runtime.embedding.java.util.JolieName( "textLocation" )
	private final joliex.meta.spec.types.Location textLocation;
	@jolie.runtime.embedding.java.util.JolieName( "operations" )
	private final java.util.List< joliex.meta.spec.types.Operation > operations;
	@jolie.runtime.embedding.java.util.JolieName( "name" )
	private final joliex.meta.spec.types.LocatedString name;

	public InterfaceDef( joliex.meta.spec.types.Location textLocation,
		java.util.SequencedCollection< joliex.meta.spec.types.Operation > operations,
		joliex.meta.spec.types.LocatedString name ) {
		this.textLocation = jolie.runtime.embedding.java.util.ValueManager.validated( "textLocation", textLocation );
		this.operations =
			jolie.runtime.embedding.java.util.ValueManager.validated( "operations", operations, 0, 2147483647, t -> t );
		this.name = jolie.runtime.embedding.java.util.ValueManager.validated( "name", name );
	}

	public joliex.meta.spec.types.Location textLocation() {
		return textLocation;
	}

	public java.util.List< joliex.meta.spec.types.Operation > operations() {
		return operations;
	}

	public joliex.meta.spec.types.LocatedString name() {
		return name;
	}

	public jolie.runtime.embedding.java.JolieNative.JolieVoid content() {
		return new jolie.runtime.embedding.java.JolieNative.JolieVoid();
	}

	public static Builder builder() {
		return new Builder();
	}

	public static Builder builder( jolie.runtime.embedding.java.JolieValue from ) {
		return from != null ? new Builder( from ) : builder();
	}

	public static jolie.runtime.embedding.java.util.StructureListBuilder< InterfaceDef, Builder > listBuilder() {
		return new jolie.runtime.embedding.java.util.StructureListBuilder<>( InterfaceDef::builder );
	}

	public static jolie.runtime.embedding.java.util.StructureListBuilder< InterfaceDef, Builder > listBuilder(
		java.util.SequencedCollection< ? extends jolie.runtime.embedding.java.JolieValue > from ) {
		return from != null
			? new jolie.runtime.embedding.java.util.StructureListBuilder<>( from, InterfaceDef::from,
				InterfaceDef::builder )
			: listBuilder();
	}

	public static InterfaceDef from( jolie.runtime.embedding.java.JolieValue j )
		throws jolie.runtime.embedding.java.TypeValidationException {
		return new InterfaceDef(
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "textLocation" ),
				joliex.meta.spec.types.Location::from ),
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom(
				j.getChildOrDefault( "operations", java.util.List.of() ), joliex.meta.spec.types.Operation::from ),
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "name" ),
				joliex.meta.spec.types.LocatedString::from ) );
	}

	public static InterfaceDef fromValue( jolie.runtime.Value v ) throws jolie.runtime.typing.TypeCheckingException {
		jolie.runtime.embedding.java.util.ValueManager.requireChildren( v, FIELD_KEYS );
		return new InterfaceDef(
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "textLocation",
				joliex.meta.spec.types.Location::fromValue ),
			jolie.runtime.embedding.java.util.ValueManager.vectorFieldFrom( v, "operations",
				joliex.meta.spec.types.Operation::fromValue ),
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "name",
				joliex.meta.spec.types.LocatedString::fromValue ) );
	}

	public static jolie.runtime.Value toValue( InterfaceDef t ) {
		final jolie.runtime.Value v = jolie.runtime.Value.create();

		v.getFirstChild( "textLocation" ).deepCopy( joliex.meta.spec.types.Location.toValue( t.textLocation() ) );
		t.operations()
			.forEach( c -> v.getNewChild( "operations" ).deepCopy( joliex.meta.spec.types.Operation.toValue( c ) ) );
		v.getFirstChild( "name" ).deepCopy( joliex.meta.spec.types.LocatedString.toValue( t.name() ) );

		return v;
	}

	public static class Builder {

		private joliex.meta.spec.types.Location textLocation;
		private java.util.SequencedCollection< joliex.meta.spec.types.Operation > operations;
		private joliex.meta.spec.types.LocatedString name;

		private Builder() {}

		private Builder( jolie.runtime.embedding.java.JolieValue j ) {
			this.textLocation = jolie.runtime.embedding.java.util.ValueManager
				.fieldFrom( j.getFirstChild( "textLocation" ), joliex.meta.spec.types.Location::from );
			this.operations = jolie.runtime.embedding.java.util.ValueManager.fieldFrom(
				j.getChildOrDefault( "operations", java.util.List.of() ), joliex.meta.spec.types.Operation::from );
			this.name = jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "name" ),
				joliex.meta.spec.types.LocatedString::from );
		}

		public Builder textLocation( joliex.meta.spec.types.Location textLocation ) {
			this.textLocation = textLocation;
			return this;
		}

		public Builder textLocation(
			java.util.function.Function< joliex.meta.spec.types.Location.Builder, joliex.meta.spec.types.Location > f ) {
			return textLocation( f.apply( joliex.meta.spec.types.Location.builder() ) );
		}

		public Builder operations( java.util.SequencedCollection< joliex.meta.spec.types.Operation > operations ) {
			this.operations = operations;
			return this;
		}

		public Builder operations(
			java.util.function.Function< joliex.meta.spec.types.Operation.ListBuilder, java.util.List< joliex.meta.spec.types.Operation > > f ) {
			return operations( f.apply( joliex.meta.spec.types.Operation.listBuilder() ) );
		}

		public Builder name( joliex.meta.spec.types.LocatedString name ) {
			this.name = name;
			return this;
		}

		public Builder name(
			java.util.function.Function< joliex.meta.spec.types.LocatedString.Builder, joliex.meta.spec.types.LocatedString > f ) {
			return name( f.apply( joliex.meta.spec.types.LocatedString.builder() ) );
		}

		public InterfaceDef build() {
			return new InterfaceDef( textLocation, operations, name );
		}
	}
}
