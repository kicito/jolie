package joliex.meta.spec.types;

/**
 * this class is a {@link jolie.runtime.embedding.java.TypedStructure} which can be described as
 * follows:
 *
 * <pre>
 * rawTag("raw"): {@link jolie.runtime.embedding.java.JolieNative.JolieVoid}
 * </pre>
 *
 * @see jolie.runtime.embedding.java.JolieValue
 * @see jolie.runtime.embedding.java.JolieNative
 * @see #builder()
 */
public final class RawBasicType extends jolie.runtime.embedding.java.TypedStructure {

	private static final java.util.Set< java.lang.String > FIELD_KEYS = fieldKeys( RawBasicType.class );

	@jolie.runtime.embedding.java.util.JolieName( "raw" )
	private final jolie.runtime.embedding.java.JolieNative.JolieVoid rawTag;

	public RawBasicType( jolie.runtime.embedding.java.JolieNative.JolieVoid rawTag ) {
		this.rawTag = jolie.runtime.embedding.java.util.ValueManager.validated( "rawTag", rawTag );
	}

	public jolie.runtime.embedding.java.JolieNative.JolieVoid rawTag() {
		return rawTag;
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

	public static jolie.runtime.embedding.java.util.StructureListBuilder< RawBasicType, Builder > listBuilder() {
		return new jolie.runtime.embedding.java.util.StructureListBuilder<>( RawBasicType::builder );
	}

	public static jolie.runtime.embedding.java.util.StructureListBuilder< RawBasicType, Builder > listBuilder(
		java.util.SequencedCollection< ? extends jolie.runtime.embedding.java.JolieValue > from ) {
		return from != null
			? new jolie.runtime.embedding.java.util.StructureListBuilder<>( from, RawBasicType::from,
				RawBasicType::builder )
			: listBuilder();
	}

	public static RawBasicType from( jolie.runtime.embedding.java.JolieValue j )
		throws jolie.runtime.embedding.java.TypeValidationException {
		return new RawBasicType(
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "raw" ),
				c -> c.content() instanceof jolie.runtime.embedding.java.JolieNative.JolieVoid content ? content
					: null ) );
	}

	public static RawBasicType fromValue( jolie.runtime.Value v ) throws jolie.runtime.typing.TypeCheckingException {
		jolie.runtime.embedding.java.util.ValueManager.requireChildren( v, FIELD_KEYS );
		return new RawBasicType(
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "raw",
				jolie.runtime.embedding.java.JolieNative.JolieVoid::fromValue ) );
	}

	public static jolie.runtime.Value toValue( RawBasicType t ) {
		final jolie.runtime.Value v = jolie.runtime.Value.create();

		v.getFirstChild( "raw" ).setValue( t.rawTag().value() );

		return v;
	}

	public static class Builder {

		private jolie.runtime.embedding.java.JolieNative.JolieVoid rawTag;

		private Builder() {}

		private Builder( jolie.runtime.embedding.java.JolieValue j ) {
			this.rawTag = jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "raw" ),
				c -> c.content() instanceof jolie.runtime.embedding.java.JolieNative.JolieVoid content ? content
					: null );
		}

		public Builder rawTag( jolie.runtime.embedding.java.JolieNative.JolieVoid rawTag ) {
			this.rawTag = rawTag;
			return this;
		}

		public RawBasicType build() {
			return new RawBasicType( rawTag );
		}
	}
}
