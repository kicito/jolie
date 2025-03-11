package joliex.meta.spec.types;

/**
 * this class is a {@link jolie.runtime.embedding.java.TypedStructure} which can be described as
 * follows:
 *
 * <pre>
 * textLocation: {@link joliex.meta.spec.types.Location}
 * extender[0,1]: {@link joliex.meta.spec.types.LocatedSymbolRef}
 * outputPort[0,2147483647]: {@link joliex.meta.spec.types.LocatedSymbolRef}
 * </pre>
 *
 * @see jolie.runtime.embedding.java.JolieValue
 * @see jolie.runtime.embedding.java.JolieNative
 * @see joliex.meta.spec.types.Location
 * @see joliex.meta.spec.types.LocatedSymbolRef
 * @see #builder()
 */
public final class Aggregation extends jolie.runtime.embedding.java.TypedStructure {

	private static final java.util.Set< java.lang.String > FIELD_KEYS = fieldKeys( Aggregation.class );

	@jolie.runtime.embedding.java.util.JolieName( "textLocation" )
	private final joliex.meta.spec.types.Location textLocation;
	@jolie.runtime.embedding.java.util.JolieName( "extender" )
	private final joliex.meta.spec.types.LocatedSymbolRef extender;
	@jolie.runtime.embedding.java.util.JolieName( "outputPort" )
	private final java.util.List< joliex.meta.spec.types.LocatedSymbolRef > outputPort;

	public Aggregation( joliex.meta.spec.types.Location textLocation, joliex.meta.spec.types.LocatedSymbolRef extender,
		java.util.SequencedCollection< joliex.meta.spec.types.LocatedSymbolRef > outputPort ) {
		this.textLocation = jolie.runtime.embedding.java.util.ValueManager.validated( "textLocation", textLocation );
		this.extender = extender;
		this.outputPort =
			jolie.runtime.embedding.java.util.ValueManager.validated( "outputPort", outputPort, 0, 2147483647, t -> t );
	}

	public joliex.meta.spec.types.Location textLocation() {
		return textLocation;
	}

	public java.util.Optional< joliex.meta.spec.types.LocatedSymbolRef > extender() {
		return java.util.Optional.ofNullable( extender );
	}

	public java.util.List< joliex.meta.spec.types.LocatedSymbolRef > outputPort() {
		return outputPort;
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

	public static jolie.runtime.embedding.java.util.StructureListBuilder< Aggregation, Builder > listBuilder() {
		return new jolie.runtime.embedding.java.util.StructureListBuilder<>( Aggregation::builder );
	}

	public static jolie.runtime.embedding.java.util.StructureListBuilder< Aggregation, Builder > listBuilder(
		java.util.SequencedCollection< ? extends jolie.runtime.embedding.java.JolieValue > from ) {
		return from != null
			? new jolie.runtime.embedding.java.util.StructureListBuilder<>( from, Aggregation::from,
				Aggregation::builder )
			: listBuilder();
	}

	public static Aggregation from( jolie.runtime.embedding.java.JolieValue j )
		throws jolie.runtime.embedding.java.TypeValidationException {
		return new Aggregation(
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "textLocation" ),
				joliex.meta.spec.types.Location::from ),
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "extender" ),
				joliex.meta.spec.types.LocatedSymbolRef::from ),
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom(
				j.getChildOrDefault( "outputPort", java.util.List.of() ),
				joliex.meta.spec.types.LocatedSymbolRef::from ) );
	}

	public static Aggregation fromValue( jolie.runtime.Value v ) throws jolie.runtime.typing.TypeCheckingException {
		jolie.runtime.embedding.java.util.ValueManager.requireChildren( v, FIELD_KEYS );
		return new Aggregation(
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "textLocation",
				joliex.meta.spec.types.Location::fromValue ),
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "extender",
				joliex.meta.spec.types.LocatedSymbolRef::fromValue ),
			jolie.runtime.embedding.java.util.ValueManager.vectorFieldFrom( v, "outputPort",
				joliex.meta.spec.types.LocatedSymbolRef::fromValue ) );
	}

	public static jolie.runtime.Value toValue( Aggregation t ) {
		final jolie.runtime.Value v = jolie.runtime.Value.create();

		v.getFirstChild( "textLocation" ).deepCopy( joliex.meta.spec.types.Location.toValue( t.textLocation() ) );
		t.extender().ifPresent(
			c -> v.getFirstChild( "extender" ).deepCopy( joliex.meta.spec.types.LocatedSymbolRef.toValue( c ) ) );
		t.outputPort().forEach(
			c -> v.getNewChild( "outputPort" ).deepCopy( joliex.meta.spec.types.LocatedSymbolRef.toValue( c ) ) );

		return v;
	}

	public static class Builder {

		private joliex.meta.spec.types.Location textLocation;
		private joliex.meta.spec.types.LocatedSymbolRef extender;
		private java.util.SequencedCollection< joliex.meta.spec.types.LocatedSymbolRef > outputPort;

		private Builder() {}

		private Builder( jolie.runtime.embedding.java.JolieValue j ) {
			this.textLocation = jolie.runtime.embedding.java.util.ValueManager
				.fieldFrom( j.getFirstChild( "textLocation" ), joliex.meta.spec.types.Location::from );
			this.extender = jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "extender" ),
				joliex.meta.spec.types.LocatedSymbolRef::from );
			this.outputPort = jolie.runtime.embedding.java.util.ValueManager.fieldFrom(
				j.getChildOrDefault( "outputPort", java.util.List.of() ),
				joliex.meta.spec.types.LocatedSymbolRef::from );
		}

		public Builder textLocation( joliex.meta.spec.types.Location textLocation ) {
			this.textLocation = textLocation;
			return this;
		}

		public Builder textLocation(
			java.util.function.Function< joliex.meta.spec.types.Location.Builder, joliex.meta.spec.types.Location > f ) {
			return textLocation( f.apply( joliex.meta.spec.types.Location.builder() ) );
		}

		public Builder extender( joliex.meta.spec.types.LocatedSymbolRef extender ) {
			this.extender = extender;
			return this;
		}

		public Builder extender(
			java.util.function.Function< joliex.meta.spec.types.LocatedSymbolRef.Builder, joliex.meta.spec.types.LocatedSymbolRef > f ) {
			return extender( f.apply( joliex.meta.spec.types.LocatedSymbolRef.builder() ) );
		}

		public Builder outputPort(
			java.util.SequencedCollection< joliex.meta.spec.types.LocatedSymbolRef > outputPort ) {
			this.outputPort = outputPort;
			return this;
		}

		public Builder outputPort(
			java.util.function.Function< jolie.runtime.embedding.java.util.StructureListBuilder< joliex.meta.spec.types.LocatedSymbolRef, joliex.meta.spec.types.LocatedSymbolRef.Builder >, java.util.List< joliex.meta.spec.types.LocatedSymbolRef > > f ) {
			return outputPort( f.apply( new jolie.runtime.embedding.java.util.StructureListBuilder<>(
				joliex.meta.spec.types.LocatedSymbolRef::builder ) ) );
		}

		public Aggregation build() {
			return new Aggregation( textLocation, extender, outputPort );
		}
	}
}
