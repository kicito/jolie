package joliex.meta.spec.types;

/**
 * this class is a {@link jolie.runtime.embedding.java.TypedStructure} which can be described as
 * follows:
 *
 * <pre>
 * min: {@link java.lang.Integer}
 * max: {@link java.lang.Integer}
 * </pre>
 *
 * @see jolie.runtime.embedding.java.JolieValue
 * @see jolie.runtime.embedding.java.JolieNative
 * @see #builder()
 */
public final class IntRange extends jolie.runtime.embedding.java.TypedStructure {

	private static final java.util.Set< java.lang.String > FIELD_KEYS = fieldKeys( IntRange.class );

	@jolie.runtime.embedding.java.util.JolieName( "min" )
	private final java.lang.Integer min;
	@jolie.runtime.embedding.java.util.JolieName( "max" )
	private final java.lang.Integer max;

	public IntRange( java.lang.Integer min, java.lang.Integer max ) {
		this.min = jolie.runtime.embedding.java.util.ValueManager.validated( "min", min );
		this.max = jolie.runtime.embedding.java.util.ValueManager.validated( "max", max );
	}

	public java.lang.Integer min() {
		return min;
	}

	public java.lang.Integer max() {
		return max;
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

	public static jolie.runtime.embedding.java.util.StructureListBuilder< IntRange, Builder > listBuilder() {
		return new jolie.runtime.embedding.java.util.StructureListBuilder<>( IntRange::builder );
	}

	public static jolie.runtime.embedding.java.util.StructureListBuilder< IntRange, Builder > listBuilder(
		java.util.SequencedCollection< ? extends jolie.runtime.embedding.java.JolieValue > from ) {
		return from != null
			? new jolie.runtime.embedding.java.util.StructureListBuilder<>( from, IntRange::from, IntRange::builder )
			: listBuilder();
	}

	public static IntRange from( jolie.runtime.embedding.java.JolieValue j )
		throws jolie.runtime.embedding.java.TypeValidationException {
		return new IntRange(
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "min" ),
				c -> c.content() instanceof jolie.runtime.embedding.java.JolieNative.JolieInt content ? content.value()
					: null ),
			jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "max" ),
				c -> c.content() instanceof jolie.runtime.embedding.java.JolieNative.JolieInt content ? content.value()
					: null ) );
	}

	public static IntRange fromValue( jolie.runtime.Value v ) throws jolie.runtime.typing.TypeCheckingException {
		jolie.runtime.embedding.java.util.ValueManager.requireChildren( v, FIELD_KEYS );
		return new IntRange(
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "min",
				jolie.runtime.embedding.java.JolieNative.JolieInt::fieldFromValue ),
			jolie.runtime.embedding.java.util.ValueManager.singleFieldFrom( v, "max",
				jolie.runtime.embedding.java.JolieNative.JolieInt::fieldFromValue ) );
	}

	public static jolie.runtime.Value toValue( IntRange t ) {
		final jolie.runtime.Value v = jolie.runtime.Value.create();

		v.getFirstChild( "min" ).setValue( t.min() );
		v.getFirstChild( "max" ).setValue( t.max() );

		return v;
	}

	public static class Builder {

		private java.lang.Integer min;
		private java.lang.Integer max;

		private Builder() {}

		private Builder( jolie.runtime.embedding.java.JolieValue j ) {
			this.min = jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "min" ),
				c -> c.content() instanceof jolie.runtime.embedding.java.JolieNative.JolieInt content ? content.value()
					: null );
			this.max = jolie.runtime.embedding.java.util.ValueManager.fieldFrom( j.getFirstChild( "max" ),
				c -> c.content() instanceof jolie.runtime.embedding.java.JolieNative.JolieInt content ? content.value()
					: null );
		}

		public Builder min( java.lang.Integer min ) {
			this.min = min;
			return this;
		}

		public Builder max( java.lang.Integer max ) {
			this.max = max;
			return this;
		}

		public IntRange build() {
			return new IntRange( min, max );
		}
	}
}
