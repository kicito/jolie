package joliex.meta.spec.types;

/**
 * this class is a {@link jolie.runtime.embedding.java.TypedStructure} which can be described as
 * follows:
 *
 * <pre>
 * textLocation: {@link joliex.meta.spec.types.Location}
 * outputPort[0,1]: {@link joliex.meta.spec.types.LocatedSymbolRef}
 * embeddedService: {@link joliex.meta.spec.types.LocatedSymbolRef}
 * </pre>
 *
 * @see jolie.runtime.embedding.java.JolieValue
 * @see jolie.runtime.embedding.java.JolieNative
 * @see joliex.meta.spec.types.Location
 * @see joliex.meta.spec.types.LocatedSymbolRef
 * @see #builder()
 */
public final class Embedding extends jolie.runtime.embedding.java.TypedStructure {

	private static final java.util.Set< java.lang.String > FIELD_KEYS = fieldKeys( Embedding.class );

	@jolie.runtime.embedding.java.util.JolieName( "textLocation" )
	private final joliex.meta.spec.types.Location textLocation;
	@jolie.runtime.embedding.java.util.JolieName( "outputPort" )
	private final joliex.meta.spec.types.LocatedSymbolRef outputPort;
	@jolie.runtime.embedding.java.util.JolieName( "embeddedService" )
	private final joliex.meta.spec.types.LocatedSymbolRef embeddedService;

	public Embedding( joliex.meta.spec.types.Location textLocation, joliex.meta.spec.types.LocatedSymbolRef outputPort,
		joliex.meta.spec.types.LocatedSymbolRef embeddedService ) {
		this.textLocation = jolie.runtime.embedding.java.util.ValueManager.validated( "textLocation", textLocation );
		this.outputPort = outputPort;
		this.embeddedService =
			jolie.runtime.embedding.java.util.ValueManager.validated( "embeddedService", embeddedService );
	}

	public joliex.meta.spec.types.Location textLocation() {
		return textLocation;
	}

	public java.util.Optional< joliex.meta.spec.types.LocatedSymbolRef > outputPort() {
		return java.util.Optional.ofNullable( outputPort );
	}

	public joliex.meta.spec.types.LocatedSymbolRef embeddedService() {
		return embeddedService;
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

	public static jolie.runtime.embedding.java.util.StructureListBuilder< Embedding, Builder > listBuilder() {
		return new jolie.runtime.embedding.java.util.StructureListBuilder<>( Embedding::builder );
	}

	public static jolie.runtime.embedding.java.util.StructureListBuilder< Embedding, Builder > listBuilder(
		java.util.SequencedCollection< ? extends jolie.runtime.embedding.java.JolieValue > from ) {
		return from != null
			? new jolie.runtime.embedding.java.util.StructureListBuilder<>( from, Embedding::from, Embedding::builder )
			: listBuilder();
	}

	public static Embedding from( jolie.runtime.embedding.java.JolieValue j )
		throws jolie.runtime.embedding.java.TypeValidationException {
		return new Embedding(
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "textLocation" ),
				joliex.meta.spec.types.Location::from ),
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "outputPort" ),
				joliex.meta.spec.types.LocatedSymbolRef::from ),
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "embeddedService" ),
				joliex.meta.spec.types.LocatedSymbolRef::from ) );
	}

	public static Embedding fromValue( jolie.runtime.Value v ) throws jolie.runtime.typing.TypeCheckingException {
		jolie.runtime.embedding.java.util.ValueManager.requireChildren( v, FIELD_KEYS );
		return new Embedding(
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "textLocation",
				joliex.meta.spec.types.Location::fromValue ),
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "outputPort",
				joliex.meta.spec.types.LocatedSymbolRef::fromValue ),
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "embeddedService",
				joliex.meta.spec.types.LocatedSymbolRef::fromValue ) );
	}

	public static jolie.runtime.Value toValue( Embedding t ) {
		final jolie.runtime.Value v = jolie.runtime.Value.create();

		v.getFirstChild( "textLocation" ).deepCopy( joliex.meta.spec.types.Location.toValue( t.textLocation() ) );
		t.outputPort().ifPresent(
			c -> v.getFirstChild( "outputPort" ).deepCopy( joliex.meta.spec.types.LocatedSymbolRef.toValue( c ) ) );
		v.getFirstChild( "embeddedService" )
			.deepCopy( joliex.meta.spec.types.LocatedSymbolRef.toValue( t.embeddedService() ) );

		return v;
	}

	public static class Builder {

		private joliex.meta.spec.types.Location textLocation;
		private joliex.meta.spec.types.LocatedSymbolRef outputPort;
		private joliex.meta.spec.types.LocatedSymbolRef embeddedService;

		private Builder() {}

		private Builder( jolie.runtime.embedding.java.JolieValue j ) {
			this.textLocation = jolie.runtime.embedding.java.util.ValueManager
				.fieldFrom( j.getFirstChild( "textLocation" ), joliex.meta.spec.types.Location::from );
			this.outputPort = jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "outputPort" ),
				joliex.meta.spec.types.LocatedSymbolRef::from );
			this.embeddedService = jolie.runtime.embedding.java.util.ValueManager
				.fieldFrom( j.getFirstChild( "embeddedService" ), joliex.meta.spec.types.LocatedSymbolRef::from );
		}

		public Builder textLocation( joliex.meta.spec.types.Location textLocation ) {
			this.textLocation = textLocation;
			return this;
		}

		public Builder textLocation(
			java.util.function.Function< joliex.meta.spec.types.Location.Builder, joliex.meta.spec.types.Location > f ) {
			return textLocation( f.apply( joliex.meta.spec.types.Location.builder() ) );
		}

		public Builder outputPort( joliex.meta.spec.types.LocatedSymbolRef outputPort ) {
			this.outputPort = outputPort;
			return this;
		}

		public Builder outputPort(
			java.util.function.Function< joliex.meta.spec.types.LocatedSymbolRef.Builder, joliex.meta.spec.types.LocatedSymbolRef > f ) {
			return outputPort( f.apply( joliex.meta.spec.types.LocatedSymbolRef.builder() ) );
		}

		public Builder embeddedService( joliex.meta.spec.types.LocatedSymbolRef embeddedService ) {
			this.embeddedService = embeddedService;
			return this;
		}

		public Builder embeddedService(
			java.util.function.Function< joliex.meta.spec.types.LocatedSymbolRef.Builder, joliex.meta.spec.types.LocatedSymbolRef > f ) {
			return embeddedService( f.apply( joliex.meta.spec.types.LocatedSymbolRef.builder() ) );
		}

		public Embedding build() {
			return new Embedding( textLocation, outputPort, embeddedService );
		}
	}
}
