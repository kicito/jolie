package joliex.meta.spec.types;

/**
 * this class is a {@link jolie.runtime.embedding.java.TypedStructure} which can be described as
 * follows:
 *
 * <pre>
 * anyTag("any"): {@link jolie.runtime.embedding.java.JolieNative.JolieVoid}
 * </pre>
 *
 * @see jolie.runtime.embedding.java.JolieValue
 * @see jolie.runtime.embedding.java.JolieNative
 * @see #builder()
 */
public final class AnyBasicType extends jolie.runtime.embedding.java.TypedStructure {

	private static final java.util.Set< java.lang.String > FIELD_KEYS = fieldKeys( AnyBasicType.class );

	@jolie.runtime.embedding.java.util.JolieName( "any" )
	private final jolie.runtime.embedding.java.JolieNative.JolieVoid anyTag;

	public AnyBasicType( jolie.runtime.embedding.java.JolieNative.JolieVoid anyTag ) {
		this.anyTag = jolie.runtime.embedding.java.util.ValueManager.validated( "anyTag", anyTag );
	}

	public jolie.runtime.embedding.java.JolieNative.JolieVoid anyTag() {
		return anyTag;
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

	public static jolie.runtime.embedding.java.util.StructureListBuilder< AnyBasicType, Builder > listBuilder() {
		return new jolie.runtime.embedding.java.util.StructureListBuilder<>( AnyBasicType::builder );
	}

	public static jolie.runtime.embedding.java.util.StructureListBuilder< AnyBasicType, Builder > listBuilder(
		java.util.SequencedCollection< ? extends jolie.runtime.embedding.java.JolieValue > from ) {
		return from != null
			? new jolie.runtime.embedding.java.util.StructureListBuilder<>( from, AnyBasicType::from,
				AnyBasicType::builder )
			: listBuilder();
	}

	public static AnyBasicType from( jolie.runtime.embedding.java.JolieValue j )
		throws jolie.runtime.embedding.java.TypeValidationException {
		return new AnyBasicType(
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "any" ),
				c -> c.content() instanceof jolie.runtime.embedding.java.JolieNative.JolieVoid content ? content
					: null ) );
	}

	public static AnyBasicType fromValue( jolie.runtime.Value v ) throws jolie.runtime.typing.TypeCheckingException {
		jolie.runtime.embedding.java.util.ValueManager.requireChildren( v, FIELD_KEYS );
		return new AnyBasicType(
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "any",
				jolie.runtime.embedding.java.JolieNative.JolieVoid::fromValue ) );
	}

	public static jolie.runtime.Value toValue( AnyBasicType t ) {
		final jolie.runtime.Value v = jolie.runtime.Value.create();

		v.getFirstChild( "any" ).setValue( t.anyTag().value() );

		return v;
	}

	public static class Builder {

		private jolie.runtime.embedding.java.JolieNative.JolieVoid anyTag;

		private Builder() {}

		private Builder( jolie.runtime.embedding.java.JolieValue j ) {
			this.anyTag = jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "any" ),
				c -> c.content() instanceof jolie.runtime.embedding.java.JolieNative.JolieVoid content ? content
					: null );
		}

		public Builder anyTag( jolie.runtime.embedding.java.JolieNative.JolieVoid anyTag ) {
			this.anyTag = anyTag;
			return this;
		}

		public AnyBasicType build() {
			return new AnyBasicType( anyTag );
		}
	}
}
