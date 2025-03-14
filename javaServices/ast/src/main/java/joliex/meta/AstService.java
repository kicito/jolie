/*
 * Copyright (C) 2024 Fabrizio Montesi <famontesi@gmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */

package joliex.meta;

import jolie.Interpreter;
import jolie.cli.CommandLineException;
import jolie.lang.CodeCheckException;
import jolie.lang.parse.ParserException;
import jolie.lang.parse.SemanticVerifier;
import jolie.lang.parse.ast.*;
import jolie.lang.parse.ast.expression.ConstantStringExpression;
import jolie.lang.parse.ast.expression.InlineTreeExpressionNode;
import jolie.lang.parse.ast.expression.VariableExpressionNode;
import jolie.lang.parse.ast.types.*;
import jolie.lang.parse.ast.types.refinements.*;
import jolie.lang.parse.module.ModuleException;
import jolie.lang.parse.util.ParsingUtils;
import jolie.cli.CommandLineParser;

import jolie.runtime.JavaService;
import jolie.runtime.Value;
import jolie.runtime.embedding.java.JolieNative;
import joliex.meta.spec.types.*;
import joliex.meta.spec.types.Module;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class AstService extends JavaService {

	private Program getModuleProgram( String moduleURIString ) {
		URI moduleURI = URI.create( moduleURIString );
		Path modulePath = Paths.get( moduleURI );
		String jolieHomePath = System.getenv( "JOLIE_HOME" );
		String[] packagesPath = new String[] { System.getenv( "JOLIE_HOME" ) + "/packages" };
		String[] includePaths = new String[] { jolieHomePath + "/include", modulePath.getParent().toUri().toString() };
		String[] cliArgs = new String[] { moduleURI.toString() };

		try( FileReader mainFileReader = new FileReader(
			modulePath.toFile() );
			CommandLineParser cliParser = new CommandLineParser( cliArgs, AstService.class.getClassLoader(), false ) ) {
			Interpreter.Configuration configuration = cliParser.getInterpreterConfiguration();
			SemanticVerifier.Configuration semanticVerificationConfiguration = new SemanticVerifier.Configuration(
				configuration.executionTarget() );
			// don't check for main to allow us to read modules without services.
			semanticVerificationConfiguration.setCheckForMain( false );
			ClassLoader classLoader = configuration.jolieClassLoader();
			InputStream mainFileInputStream = new FileInputStream( modulePath.toFile() );
			return ParsingUtils.parseProgram(
				configuration.source(),
				configuration.charset(),
				includePaths,
				packagesPath,
				configuration.jolieClassLoader(),
				configuration.constants(),
				semanticVerificationConfiguration,
				true );
		} catch( FileNotFoundException e ) {
			throw new RuntimeException( e );
		} catch( IOException e ) {
			throw new RuntimeException( e );
		} catch( CommandLineException e ) {
			throw new RuntimeException( e );
		} catch( ParserException e ) {
			throw new RuntimeException( e );
		} catch( ModuleException e ) {
			throw new RuntimeException( e );
		} catch( CodeCheckException e ) {
			throw new RuntimeException( e );
		}
	}

	private SemanticVerifier getSemanticVerifier( String moduleURIString ) {
		// TODO this is extremely similar to getModuleProgram, maybe merge them.
		URI moduleURI = URI.create( moduleURIString );
		Path modulePath = Paths.get( moduleURI );
		String jolieHomePath = System.getenv( "JOLIE_HOME" );
		String[] packagesPath = new String[] { System.getenv( "JOLIE_HOME" ) + "/packages" };
		String[] includePaths = new String[] { jolieHomePath + "/include", modulePath.getParent().toUri().toString() };
		String[] cliArgs = new String[] { moduleURI.toString() };

		try( FileReader mainFileReader = new FileReader(
			modulePath.toFile() );
			CommandLineParser cliParser = new CommandLineParser( cliArgs, AstService.class.getClassLoader(), false ) ) {
			Interpreter.Configuration configuration = cliParser.getInterpreterConfiguration();
			SemanticVerifier.Configuration semanticVerificationConfiguration = new SemanticVerifier.Configuration(
				configuration.executionTarget() );
			// don't check for main to allow us to read modules without services.
			semanticVerificationConfiguration.setCheckForMain( false );
			ClassLoader classLoader = configuration.jolieClassLoader();
			InputStream mainFileInputStream = new FileInputStream( modulePath.toFile() );
			return ParsingUtils.parseProgramModule(
				configuration.source(),
				configuration.charset(),
				includePaths,
				packagesPath,
				configuration.jolieClassLoader(),
				configuration.constants(),
				semanticVerificationConfiguration,
				true );
		} catch( FileNotFoundException e ) {
			throw new RuntimeException( e );
		} catch( IOException e ) {
			throw new RuntimeException( e );
		} catch( CommandLineException e ) {
			throw new RuntimeException( e );
		} catch( ParserException e ) {
			throw new RuntimeException( e );
		} catch( ModuleException e ) {
			throw new RuntimeException( e );
		} catch( CodeCheckException e ) {
			throw new RuntimeException( e );
		}
	}

	private List< ServiceDef > parseServices( Program module ) {
		return module.children().stream()
			.filter( child -> child instanceof ServiceNode )
			.map( node -> (ServiceNode) node )
			.map( this::getService )
			.toList();
	}

	private List< TypeDef > parseTypes( Program module ) {
		return module.children().stream()
			.filter( child -> child instanceof TypeDefinition )
			.map( node -> (TypeDefinition) node )
			.map( this::getTypeDef )
			.toList();
	}

	private List< InterfaceDef > parseInterfaces( Program module ) {
		// TODO consider storing the list of interfaces to easily distinguish between local vs imported
		// interfaces
		return module.children().stream()
			.filter( child -> child instanceof InterfaceDefinition )
			.map( node -> (InterfaceDefinition) node )
			.map( this::getInterface )
			.toList();
	}

	private InterfaceDef getInterface( InterfaceDefinition interfaceDefinition ) {
		LocatedString name = new LocatedString( interfaceDefinition.name(), location( interfaceDefinition ) );
		return InterfaceDef.builder()
			.name( name )
			.textLocation( location( interfaceDefinition ) )
			.operations( getOperations( interfaceDefinition ) )
			.build();
	}

	private ServiceDef getService( ServiceNode service ) {

		Optional< String > documentationString = service.getDocumentation();
		Location location = location( service );
		// FIXME ImportableSymbol::name is just a string so it has no location
		LocatedString name = new LocatedString( service.name(), location );

		ServiceDef.Builder builder = ServiceDef.builder()
			.textLocation( location )
			.name( name );
		if( documentationString.isPresent() ) {
			// FIXME DocumentedNode::Documentation is just a string so it has no location
			Documentation documentation = new Documentation( documentationString.get(), location );
			builder.documentation( documentation );
		}

		List< InputPort > inputPorts = service.program().children().stream()
			.filter( node -> node instanceof InputPortInfo )
			.map( node -> (InputPortInfo) node )
			.map( this::createInputPort )
			.toList();

		List< OutputPort > outputPorts = service.program().children().stream()
			.filter( node -> node instanceof OutputPortInfo )
			.map( node -> (OutputPortInfo) node )
			.map( this::createOutputPort )
			.toList();

		builder.inputPorts( inputPorts )
			.outputPorts( outputPorts );

		return builder.build();
	}

	private OutputPort createOutputPort( OutputPortInfo outputPortInfo ) {
		OutputPort.Builder builder = OutputPort.builder()
			.textLocation( location( outputPortInfo ) );

		// FIXME ImportableSymbol::name is just a string so it has no location
		LocatedString name = new LocatedString( outputPortInfo.id(), location( outputPortInfo ) );
		builder.name( name );


		Optional< LocatedString > locationExpression = switch( outputPortInfo.location() ) {
		// e.g protocol = "http"
		case ConstantStringExpression constantStringExpression ->
			Optional.of( new LocatedString( constantStringExpression.value(), location( constantStringExpression ) ) );
		// e.g protocol = param.protocol
		case VariableExpressionNode variableExpressionNode -> {
			VariablePathNode variablePathNode = variableExpressionNode.variablePath();
			// TODO test if this is the right string
			yield Optional.of( new LocatedString( variablePathNode.toString(), location( variablePathNode ) ) );
		}
		case null -> Optional.empty();
		default ->
			throw new IllegalStateException( "Unexpected outputPort location type: " + outputPortInfo.location() );
		};

		locationExpression.ifPresent( builder::location );

		Optional< ConstantStringExpression > protocolExpression = switch( outputPortInfo.protocol() ) {
		// e.g. "protocol = "http" "
		case VariableExpressionNode variableExpressionNode ->
			Optional.of( (ConstantStringExpression) variableExpressionNode.variablePath().path().getFirst().key() );
		// e.g. "protocol = "http" { .format = "json" }"
		case InlineTreeExpressionNode inlineTreeExpressionNode -> {
			VariableExpressionNode variableExpressionNode =
				(VariableExpressionNode) inlineTreeExpressionNode.rootExpression();
			yield Optional
				.of( (ConstantStringExpression) variableExpressionNode.variablePath().path().getFirst().key() );
		}
		case null -> Optional.empty();
		// There shouldn't be other cases.
		default ->
			throw new IllegalStateException( "Unexpected outputPort protocol type: " + outputPortInfo.protocol() );
		};

		if( protocolExpression.isPresent() ) {
			LocatedString protocol =
				new LocatedString( protocolExpression.get().value(), location( protocolExpression.get() ) );
			builder.protocol( protocol );
		}

		return builder
			.interfaces( getInterfaces( outputPortInfo ) )
			.operations( getOperations( outputPortInfo ) )
			.build();
	}

	private InputPort createInputPort( InputPortInfo inputPortInfo ) {
		InputPort.Builder builder = InputPort.builder()
			.textLocation( location( inputPortInfo ) );

		// FIXME ImportableSymbol::name is just a string so it has no location
		LocatedString name = new LocatedString( inputPortInfo.id(), location( inputPortInfo ) );
		builder.name( name );

		Optional< LocatedString > locationExpression = switch( inputPortInfo.location() ) {
		// e.g protocol = "http"
		case ConstantStringExpression constantStringExpression ->
			Optional.of( new LocatedString( constantStringExpression.value(), location( constantStringExpression ) ) );
		// e.g protocol = param.protocol
		case VariableExpressionNode variableExpressionNode -> {
			VariablePathNode variablePathNode = variableExpressionNode.variablePath();
			// TODO test if this is the right string
			yield Optional.of( new LocatedString( variablePathNode.toString(), location( variablePathNode ) ) );
		}
		case null -> Optional.empty();
		default -> throw new IllegalStateException( "Unexpected inputPort location type: " + inputPortInfo.location() );
		};

		locationExpression.ifPresent( builder::location );

		Optional< ConstantStringExpression > protocolExpression = switch( inputPortInfo.protocol() ) {
		// e.g. "protocol = "http" "
		case VariableExpressionNode variableExpressionNode ->
			Optional.of( (ConstantStringExpression) variableExpressionNode.variablePath().path().getFirst().key() );
		// e.g. "protocol = "http" { .format = "json" }"
		case InlineTreeExpressionNode inlineTreeExpressionNode -> {
			VariableExpressionNode variableExpressionNode =
				(VariableExpressionNode) inlineTreeExpressionNode.rootExpression();
			yield Optional
				.of( (ConstantStringExpression) variableExpressionNode.variablePath().path().getFirst().key() );
		}
		case null -> Optional.empty();
		// There shouldn't be other cases.
		default -> throw new IllegalStateException( "Unexpected inputPort protocol type " + inputPortInfo.protocol() );
		};

		if( protocolExpression.isPresent() ) {
			LocatedString protocol =
				new LocatedString( protocolExpression.get().value(), location( protocolExpression.get() ) );
			builder.protocol( protocol );
		}

		return builder.redirections( getRedirections( inputPortInfo ) )
			.interfaces( getInterfaces( inputPortInfo ) )
			.operations( getOperations( inputPortInfo ) )
			.aggregations( getAggregations( inputPortInfo ) )
			.build();
	}

	private List< Aggregation > getAggregations( InputPortInfo inputPort ) {
		Location location = location( inputPort );
		return Arrays.stream( inputPort.aggregationList() )
			.map( x -> getAggregation( x, location ) )
			.toList();
	}

	private Aggregation getAggregation( InputPortInfo.AggregationItemInfo aggregationItemInfo, Location location ) {
		aggregationItemInfo.interfaceExtender();
		List< LocatedSymbolRef > outputPorts = Arrays.stream( aggregationItemInfo.outputPortList() )
			// TODO OutputPorts in AggregationItemInfo are only stored as String, would be nice to store a
			// ParsingContext with the String,
			// using the location of the entire inputPort for now.
			.map( outputPort -> new LocatedSymbolRef( outputPort, location ) )
			.toList();
		Aggregation.Builder builder = Aggregation.builder()
			// FIXME aggregationItemInfo doesn't have a ParsingContext
			.textLocation( location )
			.outputPort( outputPorts );

		// don't like null checking
		Optional< InterfaceExtenderDefinition > interfaceExtenderDefinition =
			Optional.ofNullable( aggregationItemInfo.interfaceExtender() );
		if( interfaceExtenderDefinition.isPresent() ) {
			// double-check this is correct if the interface extender is imported with an alias (from .X import
			// Y as Z).
			String name = interfaceExtenderDefinition.get().name();
			// FIXME names are only stored as String, would be nice to store a ParsingContext with the String,
			builder.extender( new LocatedSymbolRef( name, location ) );
		}

		if( aggregationItemInfo.interfaceExtender() != null ) {
			// double-check this is correct if the interface extender is imported with an alias (from .X import
			// Y as Z).
			String name = aggregationItemInfo.interfaceExtender().name();
			// FIXME names are only stored as String, would be nice to store a ParsingContext with the String,
			builder.extender( new LocatedSymbolRef( name, location ) );
		}
		return builder.build();
	}

	private List< Redirection > getRedirections( InputPortInfo inputPort ) {
		// Map from operation name to outputPort
		return inputPort.redirectionMap()
			.entrySet()
			.stream()
			.map( entry -> {
				/*
				 * FIXME InputPortInfo::redirectionMap has no ParsingContext, but we should be able to store a
				 * Map<String, OutputPort> and get them through that.
				 */
				LocatedSymbolRef outputPort = new LocatedSymbolRef( entry.getValue(), location( inputPort ) );
				LocatedString name = new LocatedString( entry.getKey(), location( inputPort ) );
				return new Redirection( location( inputPort ), outputPort, name );
			} )
			.toList();
	}

	private List< String > getInterfaces( PortInfo port ) {
		return port.getInterfaceList().stream()
			.map( InterfaceDefinition::name )
			// TODO add logic to look through imports if the interface is not defined in the same module.
			.toList();
	}

	private List< Operation > getOperations( OperationCollector operationCollector ) {
		return operationCollector.operationsMap()
			// the operation name is also stored in OperationDeclaration, so we don't need the key.
			.values()
			.stream()
			.map( this::getOperation )
			.toList();
	}

	private Operation getOperation( OperationDeclaration operationDeclaration ) {
		return switch( operationDeclaration ) {
		case OneWayOperationDeclaration oneWayOperationDeclaration -> {
			yield Operation.of1( getOneWayOperation( oneWayOperationDeclaration ) );
		}
		case RequestResponseOperationDeclaration requestResponseOperationDeclaration -> {
			yield Operation.of2( getRequestResponseOperation( requestResponseOperationDeclaration ) );
		}
		case null, default ->
			throw new UnsupportedOperationException( "operation: " + operationDeclaration +
				" is neither a OneWayOperationDeclaration or a RequestResponseOperationDeclaration" );
		};
	}

	private OneWayOperation getOneWayOperation( OneWayOperationDeclaration oneWayOperationDeclaration ) {
		return OneWayOperation.builder()
			.textLocation( location( oneWayOperationDeclaration ) )
			.requestType( getType( oneWayOperationDeclaration.requestType() ) )
			// FIXME name location
			.name( new LocatedString( oneWayOperationDeclaration.id(), location( oneWayOperationDeclaration ) ) )
			.build();

	}

	private RequestResponseOperation getRequestResponseOperation(
		RequestResponseOperationDeclaration requestResponseOperationDeclaration ) {
		return RequestResponseOperation.builder()
			.textLocation( location( requestResponseOperationDeclaration ) )
			.responseType( getType( requestResponseOperationDeclaration.responseType() ) )
			.requestType( getType( requestResponseOperationDeclaration.requestType() ) )
			// FIXME name location
			.name( new LocatedString( requestResponseOperationDeclaration.id(),
				location( requestResponseOperationDeclaration ) ) )
			.faults( requestResponseOperationDeclaration
				.faults()
				.entrySet()
				.stream()
				.map( entry -> {
					Location location = location( requestResponseOperationDeclaration );
					LocatedString locatedName = new LocatedString( entry.getKey(), location );
					Type faultType = getType( entry.getValue() );
					return new FaultType( location, locatedName, faultType );
				} )
				.toList() )
			.build();
	}

	private TypeDef getTypeDef( TypeDefinition typeDefinition ) {
		TypeDef.Builder builder = TypeDef.builder();
		// FIXME ImportableSymbol::name is just a string so it has no location
		LocatedString name = new LocatedString( typeDefinition.name(), location( typeDefinition ) );

		return builder
			.textLocation( location( typeDefinition ) )
			.name( name )
			.type( getType( typeDefinition ) )
			.build();
	}

	private Type getType( TypeDefinition typeDefinition ) {
		switch( typeDefinition ) {
		case TypeChoiceDefinition typeChoiceDefinition -> {
			// S2 is the choice type
			return Type.of2(
				new Type.S2( getChoiceType( typeChoiceDefinition ) ) );
		}
		case TypeDefinitionLink typeDefinitionLink -> {
			return Type.of3(
				new Type.S3( getTypeReference( typeDefinitionLink ) ) );
		}
		// TODO implement undefined type in ast.ol
		/*
		 * case TypeDefinitionUndefined u -> throw new UnsupportedOperationException(
		 * "Type undefined is not supported yet" + typeDefinition ); // Can't // be // shown // in // ast.ol
		 * // types // InlineDefinition
		 */
		case TypeInlineDefinition typeInlineDefinition -> {
			return Type.of1(
				new Type.S1( getTreeType( typeInlineDefinition ) ) );
		}
		default -> throw new IllegalStateException( "Unexpected value: " + typeDefinition );
		}
	}

	private ChoiceType getChoiceType( TypeChoiceDefinition typeChoiceDefinition ) {
		return ChoiceType.builder()
			.textLocation( location( typeChoiceDefinition ) )
			.left( getType( typeChoiceDefinition.left() ) )
			.right( getType( typeChoiceDefinition.right() ) )
			.build();
	}

	private LocatedSymbolRef getTypeReference( TypeDefinitionLink typeDefinitionLink ) {
		// TODO should the LocatedSymbolRef.contentValue here be the local name of the type or the name at
		// the original definition?
		return new LocatedSymbolRef( typeDefinitionLink.simpleName(), location( typeDefinitionLink ) );
	}

	private TreeType getTreeType( TypeInlineDefinition typeInlineDefinition ) {
		/*
		 * List< TreeNodeType > nodes = typeInlineDefinition // TODO this fails if subtypes() == null,
		 * create an issue for it xD. .subTypes() .stream() .map( Map.Entry::getValue ) .map(
		 * this::getTreeNodeType ) .toList();
		 */
		List< TreeNodeType > nodes = switch( typeInlineDefinition ) {
		case TypeInlineDefinition x when x.hasSubTypes() -> x.subTypes()
			.stream()
			.map( Map.Entry::getValue )
			.map( this::getTreeNodeType )
			.toList();
		case TypeInlineDefinition x -> List.of();
		};
		TreeType.Builder builder = TreeType.builder();
		Optional< String > documentationString = typeInlineDefinition.getDocumentation();
		if( documentationString.isPresent() ) {
			// FIXME DocumentedNode::Documentation is just a string so it has no location
			Documentation documentation =
				new Documentation( documentationString.get(), location( typeInlineDefinition ) );
			builder.documentation( documentation );
		}
		return builder.textLocation( location( typeInlineDefinition ) )
			.basicType( getBasicType( typeInlineDefinition.basicType() ) )
			.nodes( nodes )
			.build();

	}

	private TreeNodeType getTreeNodeType( TypeDefinition typeDefinition ) {
		TreeNodeType.Builder builder = TreeNodeType.builder();
		Optional< String > documentationString = typeDefinition.getDocumentation();
		if( documentationString.isPresent() ) {
			// FIXME DocumentedNode::Documentation is just a string so it has no location
			Documentation documentation = new Documentation( documentationString.get(), location( typeDefinition ) );
			builder.documentation( documentation );
		}
		return builder
			.textLocation( location( typeDefinition ) )
			.name( new LocatedString( typeDefinition.name(), location( typeDefinition ) ) )
			.range( getRange( typeDefinition ) )
			.type( getType( typeDefinition ) )
			.build();

	}

	private NonNegativeIntRange getRange( TypeDefinition typeDefinition ) {
		return NonNegativeIntRange.builder()
			.min( typeDefinition.cardinality().min() )
			.max( typeDefinition.cardinality().max() )
			.build();
	}

	/*
	 * private Optional<Documentation> getDocumentation( DocumentedNode node ) { return
	 * node.getDocumentation() .flatMap( documentation -> { // FIXME DocumentedNode and its
	 * documentation have no ParsingContext. return new Documentation( documentation, null ); } ); }
	 */

	private BasicType getBasicType( BasicTypeDefinition basicTypeDefinition ) {

		return switch( basicTypeDefinition.nativeType() ) {
		case STRING -> BasicType.of6( getStringBasicType( basicTypeDefinition ) );
		case INT -> BasicType.of3( getIntBasicType( basicTypeDefinition ) );
		case LONG -> BasicType.of4( getLongBasicType( basicTypeDefinition ) );
		case BOOL -> BasicType.of2( getBoolBasicType() );
		case DOUBLE -> BasicType.of5( getDoubleBasicType( basicTypeDefinition ) );
		case VOID -> BasicType.of1( getVoidBasicType() );
		case ANY -> BasicType.of7( getAnyBasicType() );
		case RAW -> BasicType.of8( getRawBasicType() );
		case null, default -> {
			throw new UnsupportedOperationException( "no corresponding type in ast.ol" );
		}
		};


	}

	private StringBasicType getStringBasicType( BasicTypeDefinition basicTypeDefinition ) {

		List< StringRefinement > stringRefinements = basicTypeDefinition.refinements().stream()
			.map( basicTypeRefinement -> switch( basicTypeRefinement ) {
			case BasicTypeRefinementStringLength length -> {
				IntRange range = new IntRange( length.getMin(), length.getMax() );
				yield StringRefinement.of1(
					new StringRefinement.S1( range ) );
			}
			case BasicTypeRefinementStringList list -> {
				List< String > enumeration = list.getList();
				yield StringRefinement.of2( new StringRefinement.S2( enumeration ) );
			}
			case BasicTypeRefinementStringRegex regex -> {
				yield StringRefinement.of3( new StringRefinement.S3( regex.getRegex() ) );
			}
			default -> throw new IllegalStateException( "Unexpected value: " + basicTypeRefinement );
			} )
			.toList();
		return new StringBasicType( new JolieNative.JolieVoid(), stringRefinements );
	}



	private IntBasicType getIntBasicType( BasicTypeDefinition basicTypeDefinition ) {
		/*
		 * TODO can we ever have more than one BasicTypeRefinementIntegerRanges? if not, we can simplify
		 * into: List<IntRefinement> intRefinements = new ArrayList<>(); if
		 * (!basicTypeDefinition.refinements().isEmpty() ) { BasicTypeRefinementIntegerRanges refinement =
		 * (BasicTypeRefinementIntegerRanges) basicTypeDefinition.refinements().get( 0 ); List<IntRange>
		 * intRanges = refinement .getRanges() .stream() .map( interval -> { return new IntRange(
		 * interval.getMin(), interval.getMax() ); } ).toList(); intRefinements.add( new IntRefinement(
		 * intRanges ) ); }
		 */
		List< IntRefinement > intRefinements = basicTypeDefinition.refinements().stream()
			// should be safe as this is the only possible refinement.
			.map( refinement -> (BasicTypeRefinementIntegerRanges) refinement )
			.map( refinement -> {
				List< IntRange > intRanges = refinement
					.getRanges()
					.stream()
					.map( interval -> new IntRange( interval.getMin(), interval.getMax() ) )
					.toList();
				return new IntRefinement( intRanges );
			} )
			.toList();


		return new IntBasicType( new JolieNative.JolieVoid(), intRefinements );
	}

	private LongBasicType getLongBasicType( BasicTypeDefinition basicTypeDefinition ) {
		List< LongRefinement > longRefinements = basicTypeDefinition.refinements().stream()
			.map( refinement -> {
				// should be safe as this is the only possible refinement.
				return (BasicTypeRefinementLongRanges) refinement;
			} )
			.map( refinement -> {
				List< LongRange > longRanges = refinement
					.getRanges()
					.stream()
					.map( interval -> new LongRange( interval.getMin(), interval.getMax() ) )
					.toList();
				return new LongRefinement( longRanges );
			} )
			.toList();
		return new LongBasicType( new JolieNative.JolieVoid(), longRefinements );
	}

	private BoolBasicType getBoolBasicType() {
		return new BoolBasicType( new JolieNative.JolieVoid() );

	}

	private DoubleBasicType getDoubleBasicType( BasicTypeDefinition basicTypeDefinition ) {
		List< DoubleRefinement > doubleRefinements = basicTypeDefinition.refinements().stream()
			.map( refinement -> {
				// should be safe as this is the only possible refinement.
				return (BasicTypeRefinementDoubleRanges) refinement;
			} )
			.map( refinement -> {
				List< DoubleRange > doubleRanges = refinement
					.getRanges()
					.stream()
					.map( interval -> new DoubleRange( interval.getMin(), interval.getMax() ) )
					.toList();
				return new DoubleRefinement( doubleRanges );
			} )
			.toList();
		return new DoubleBasicType( new JolieNative.JolieVoid(), doubleRefinements );
	}

	private VoidBasicType getVoidBasicType() {
		return new VoidBasicType( new JolieNative.JolieVoid() );
	}

	private AnyBasicType getAnyBasicType() {
		return new AnyBasicType( new JolieNative.JolieVoid() );
	}

	private RawBasicType getRawBasicType() {
		return new RawBasicType( new JolieNative.JolieVoid() );
	}


	/**
	 * Creates a Jolie type Location from an AST node.
	 *
	 * @param node an AST node
	 * @return The Location
	 */
	private Location location( OLSyntaxNode node ) {
		jolie.lang.parse.context.Location nodeLocation = node.context().location();

		Position startPosition =
			new Position( nodeLocation.range().start().character(), nodeLocation.range().start().line() );
		Position endPosition =
			new Position( nodeLocation.range().end().character(), nodeLocation.range().end().line() );
		Range range = new Range( startPosition, endPosition );

		return Location
			.builder()
			.source( nodeLocation.documentUri().toString() )
			.range( range )
			.build();
	}

	/**
	 *
	 * @param modulePath The path to the module in URI form (e.g. "file:///home/user/main.ol").
	 * @return The Module
	 */
	public Value parseModule( String modulePath ) {

		Program moduleProgram = getModuleProgram( modulePath );

		return Module.toValue( Module.builder()
			.types( parseTypes( moduleProgram ) )
			.services( parseServices( moduleProgram ) )
			.interfaces( parseInterfaces( moduleProgram ) )
			.build() );
	}

	public Value resolveSymbol( LocatedSymbolRef request ) {
		String modulePath = request.textLocation().source();
		URI moduleURI = URI.create( modulePath );
		SemanticVerifier semanticVerifier = getSemanticVerifier( modulePath );

		ImportableSymbol node = (ImportableSymbol) semanticVerifier
			.symbolTables()
			.get( moduleURI )
			// TODO error handling in case the symbol is not found.
			.getSymbol( request.contentValue() )
			.orElseThrow()
			.node();

		return switch( node ) {
		case ServiceNode serviceNode -> ServiceDef.toValue( getService( serviceNode ) );
		case InterfaceDefinition interfaceDefinition -> InterfaceDef.toValue( getInterface( interfaceDefinition ) );
		case TypeDefinition typeDefinition -> TypeDef.toValue( getTypeDef( typeDefinition ) );
		default -> throw new IllegalStateException( "Unexpected value: " + node );
		};
	}
}
