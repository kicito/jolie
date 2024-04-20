package jolie.runtime.embedding.java;

import java.util.SequencedCollection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import jolie.runtime.ByteArray;
import jolie.runtime.JavaService.ValueConverter;
import jolie.runtime.Value;
import jolie.runtime.ValueVector;
import jolie.runtime.embedding.java.util.AbstractListBuilder;
import jolie.runtime.embedding.java.util.UntypedBuilder;

public interface JolieValue extends ValueConverter {

    JolieNative<?> content();
    Map<String, List<JolieValue>> children();

    default List<JolieValue> getChild( String name ) {
        return children().get( name );
    }

    default List<JolieValue> getChildOrDefault( String name, List<JolieValue> defaultValue ) {
        return children().getOrDefault( name, defaultValue );
    }

    default Optional<JolieValue> getFirstChild( String name ) {
        return Optional.ofNullable( getChildOrDefault( name, null ) ).map( c -> c.isEmpty() ? null : c.getFirst() );
    }

    public static Builder construct() { return new Builder(); }
    public static Builder construct( JolieNative<?> content ) { return construct().content( content ); }
    public static Builder construct( Boolean contentValue ) { return construct( JolieNative.create( contentValue ) ); }
    public static Builder construct( Integer contentValue ) { return construct( JolieNative.create( contentValue ) ); }
    public static Builder construct( Long contentValue ) { return construct( JolieNative.create( contentValue ) ); }
    public static Builder construct( Double contentValue ) { return construct( JolieNative.create( contentValue ) ); }
    public static Builder construct( String contentValue ) { return construct( JolieNative.create( contentValue ) ); }
    public static Builder construct( ByteArray contentValue ) { return construct( JolieNative.create( contentValue ) ); }

    public static Builder constructFrom( JolieValue j ) { return new Builder( Objects.requireNonNull( j ) ); }

    public static ListBuilder constructList() { return new ListBuilder(); }

    public static ListBuilder constructListFrom( SequencedCollection<? extends JolieValue> c ) { return new ListBuilder( c ); }
    
    public static JolieValue create( JolieNative<?> content ) { return new UntypedStructure<>( content, Map.of() ); }
    public static JolieValue create( Boolean contentValue ) { return create( JolieNative.create( contentValue ) ); }
    public static JolieValue create( Integer contentValue ) { return create( JolieNative.create( contentValue ) ); }
    public static JolieValue create( Long contentValue ) { return create( JolieNative.create( contentValue ) ); }
    public static JolieValue create( Double contentValue ) { return create( JolieNative.create( contentValue ) ); }
    public static JolieValue create( String contentValue ) { return create( JolieNative.create( contentValue ) ); }
    public static JolieValue create( ByteArray contentValue ) { return create( JolieNative.create( contentValue ) ); }
    public static JolieValue create() { return create( JolieNative.create() ); }

    public static JolieValue createFrom( JolieValue j ) { return j; }

    public static JolieValue fromValue( Value v ) {
        return new UntypedStructure<>(
            JolieNative.contentFromValue( v ), 
            v.children()
                .entrySet()
                .parallelStream()
                .collect( Collectors.toUnmodifiableMap(
                    Map.Entry::getKey,
                    e -> e.getValue().stream().map( JolieValue::fromValue ).toList()
                ) )
        );
    }

    public static Value toValue( JolieValue t ) { 
        final Value value = t.content().jolieRepr();
        t.children().forEach( (name, ls) -> {
            if ( !ls.isEmpty() ) {
                final ValueVector vv = value.getChildren( name );
                ls.forEach( e -> vv.add( toValue( e ) ) );
            }
        } );
        return value;
    }

    public static class Builder extends UntypedBuilder<Builder> {

        private JolieNative<?> content;

        private Builder() {}
        private Builder( JolieValue j ) { 
            super( j.children() );
            content = j.content();
        }

        protected Builder self() { return this; }

        public Builder content( JolieNative<?> content ) { this.content = content; return self(); }

        public Builder content( Boolean value ) { return content( JolieNative.create( value ) ); }
        public Builder content( Integer value ) { return content( JolieNative.create( value ) ); }
        public Builder content( Long value ) { return content( JolieNative.create( value ) ); }
        public Builder content( Double value ) { return content( JolieNative.create( value ) ); }
        public Builder content( String value ) { return content( JolieNative.create( value ) ); }
        public Builder content( ByteArray value ) { return content( JolieNative.create( value ) ); }

        public Builder content( UnaryOperator<JolieNative<?>> contentOperator ) { return content( contentOperator.apply( content ) ); }

        public JolieValue build() { return new UntypedStructure<>( content == null ? JolieNative.create() : content, children ); }
    }
    
    public static class ListBuilder extends AbstractListBuilder<ListBuilder, JolieValue> {
        
        private ListBuilder() {}
        private ListBuilder( SequencedCollection<? extends JolieValue> c ) { super( c, JolieValue::createFrom ); }

        protected ListBuilder self() { return this; }
        
        public ListBuilder add( Function<Builder, JolieValue> b ) { return add( b.apply( construct() ) ); }

        public ListBuilder add( JolieNative<?> contentEntry ) { return add( JolieValue.create( contentEntry ) ); }
        public ListBuilder add( Boolean valueEntry ) { return add( JolieNative.create( valueEntry ) ); }
        public ListBuilder add( Integer valueEntry ) { return add( JolieNative.create( valueEntry ) ); }
        public ListBuilder add( Long valueEntry ) { return add( JolieNative.create( valueEntry ) ); }
        public ListBuilder add( Double valueEntry ) { return add( JolieNative.create( valueEntry ) ); }
        public ListBuilder add( String valueEntry ) { return add( JolieNative.create( valueEntry ) ); }
        public ListBuilder add( ByteArray valueEntry ) { return add( JolieNative.create( valueEntry ) ); }

        public ListBuilder set( int index, Function<Builder, JolieValue> b ) { return set( index, b.apply( construct() ) ); }

        public ListBuilder set( int index, JolieNative<?> contentEntry ) { return set( index, JolieValue.create( contentEntry ) ); }
        public ListBuilder set( int index, Boolean valueEntry ) { return set( index, JolieNative.create( valueEntry ) ); }
        public ListBuilder set( int index, Integer valueEntry ) { return set( index, JolieNative.create( valueEntry ) ); }
        public ListBuilder set( int index, Long valueEntry ) { return set( index, JolieNative.create( valueEntry ) ); }
        public ListBuilder set( int index, Double valueEntry ) { return set( index, JolieNative.create( valueEntry ) ); }
        public ListBuilder set( int index, String valueEntry ) { return set( index, JolieNative.create( valueEntry ) ); }
        public ListBuilder set( int index, ByteArray valueEntry ) { return set( index, JolieNative.create( valueEntry ) ); }

        public ListBuilder reconstruct( int index, Function<Builder, JolieValue> b ) { return replace( index, j -> b.apply( constructFrom( j ) ) ); }
    }
}
