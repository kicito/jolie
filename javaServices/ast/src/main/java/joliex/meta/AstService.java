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
import jolie.lang.parse.ast.expression.SumExpressionNode;
import jolie.lang.parse.ast.expression.VariableExpressionNode;
import jolie.lang.parse.ast.types.*;
import jolie.lang.parse.module.ModuleException;
import jolie.lang.parse.util.ParsingUtils;
import jolie.cli.CommandLineParser;

import jolie.runtime.JavaService;
import jolie.runtime.Value;
import jolie.runtime.ValueVector;
import jolie.runtime.embedding.java.JolieNative;
import joliex.meta.spec.types.*;
import joliex.meta.spec.types.Module;
import org.apache.commons.lang3.NotImplementedException;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AstService extends JavaService {

	private Program getModuleProgram(String modulePathString) {
		Path modulePath = Paths.get( modulePathString );
		String jolieHomePath = System.getenv("JOLIE_HOME");
		String[] packagesPath = new String[] {System.getenv("JOLIE_HOME") + "/packages"};
		String[] includePaths = new String[]{jolieHomePath + "/include", modulePath.getParent().toUri().toString()};
		String[] cliArgs = new String[]{modulePath.toUri().toString()};

		try ( FileReader mainFileReader = new FileReader(
			modulePath.toFile()
			);
			CommandLineParser cliParser = new CommandLineParser(cliArgs, AstService.class.getClassLoader(), false)
		) {
			Interpreter.Configuration configuration = cliParser.getInterpreterConfiguration();
			SemanticVerifier.Configuration semanticVerificationConfiguration = new SemanticVerifier.Configuration(
				configuration.executionTarget());
			URI mainFileUri = modulePath.toUri();
			ClassLoader classLoader = configuration.jolieClassLoader();
			InputStream mainFileInputStream = new FileInputStream(modulePath.toFile());
			return ParsingUtils.parseProgram(mainFileInputStream, mainFileUri,
				mainFileReader.getEncoding(), includePaths, packagesPath, classLoader,
				configuration.constants(), semanticVerificationConfiguration, true);
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

	private List<ServiceDef> parseServices(Program module) {
		return module.children().stream()
			.filter( child -> child instanceof ServiceNode )
			.map( node -> {
				return (ServiceNode) node;
			} )
			.map( this::serviceToValue
			)
			.toList();
	}
	private List<TypeDef> parseTypes(Program module) {
		return List.of();
	}

	private List<InterfaceDef> parseInterface(Program module) {

	}

	private ServiceDef serviceToValue(ServiceNode service) {

		Optional<String> documentationString = service.getDocumentation();
		Location location = location(service);
		// FIXME ImportableSymbol::name is just a string so it has no location
		LocatedString name = new LocatedString( service.name(), location);

		ServiceDef.Builder builder = ServiceDef.builder()
			.textLocation( location )
			.name( name );
		if(documentationString.isPresent()) {
			// FIXME DocumentedNode::Documentation is just a string so it has no location
			Documentation documentation = new Documentation(documentationString.get(), location);
			builder.documentation( documentation );
		}

		for(OLSyntaxNode node : service.program().children()) {
			if(node instanceof InputPortInfo ) {
				InputPortInfo port = (InputPortInfo) node;
			} else if( node instanceof OutputPortInfo ) {
				OutputPortInfo port = (OutputPortInfo) node;

			}
		}
		builder.inputPorts(  )
			.outputPorts(  );

		return ServiceDef.toValue( )
	}

	private InputPort createInputPort(InputPortInfo inputPortInfo) {
		InputPort.Builder builder = InputPort.builder()
			.textLocation( location( inputPortInfo ) );

		// FIXME ImportableSymbol::name is just a string so it has no location
		LocatedString name = new LocatedString( inputPortInfo.id(), location(inputPortInfo) );
		builder.name( name );

		// might be too simple
		ConstantStringExpression locationExpression = (ConstantStringExpression) inputPortInfo.location();
		LocatedString location = new LocatedString( locationExpression.value(), location(locationExpression) );

		// FIXME probably buggy
		VariableExpressionNode variableExpressionNode = (VariableExpressionNode) inputPortInfo.protocol();
		ConstantStringExpression protocolExpression = (ConstantStringExpression) variableExpressionNode.variablePath().path().getFirst().key();
		LocatedString protocol = new LocatedString( protocolExpression.value(), location(protocolExpression));


		inputPortInfo.redirectionMap()
		builder.location( location )
			.protocol( protocol )
			.redirections( getRedirections( inputPortInfo ) )
			.interfaces( getInterfaces( inputPortInfo ) )
			.operations( getOperations( inputPortInfo ) )
			.aggregations(  )
	}

	private String getSymbolRef(ImportableSymbol node) {
		return node.name();
	}

	private List<Redirection> getRedirections(InputPortInfo inputPort) {
		//Map from operation name to outputPort
		return inputPort.redirectionMap()
			.entrySet()
			.stream()
			.map( entry -> {
				/*
					FIXME InputPortInfo::redirectionMap has no ParsingContext, but we should be able to store a Map<String, OutputPort> and get them through that.
				    But if we can find it anyway maybe Redirection should just save the OutputPort instead.
				*/
				LocatedSymbolRef outputPort = new LocatedSymbolRef( entry.getValue(), location(inputPort) );
				LocatedString name = new LocatedString( entry.getKey(), location(inputPort) );
				return new Redirection( location(inputPort), outputPort, name );
			} )
			.toList();
	}

	private List<String> getInterfaces(PortInfo port) {
		return port.getInterfaceList().stream()
			.map( InterfaceDefinition::name)
			.toList();
	}

	private List<Operation> getOperations(OperationCollector operationCollector) {
		return operationCollector.operationsMap()
			.entrySet()
			.stream()
			.map((entry) -> {

			})
			.toList();
	}

	private Operation getOperation(OperationDeclaration operationDeclaration) {
		if( operationDeclaration instanceof OneWayOperationDeclaration) {
			OneWayOperationDeclaration oneWayOperationDeclaration = (OneWayOperationDeclaration) operationDeclaration;
			TypeDefinition requestType = oneWayOperationDeclaration.requestType();
			OneWayOperation ow = new OneWayOperation(  );
		} else if( operationDeclaration instanceof RequestResponseOperationDeclaration) {

		} else {
			throw new NotImplementedException("operation: " + operationDeclaration + " is neither a OneWayOperationDeclaration or a RequestResponseOperationDeclaration");
		}
	}

	private Type getType(TypeDefinition typeDefinition) {
		switch( typeDefinition ) {
			case TypeChoiceDefinition typeChoiceDefinition -> {
				//S2 is the choice type
				return Type.of2(
					new Type.S2( getChoiceType( typeChoiceDefinition ) )
				);
			}
			case TypeDefinitionLink typeDefinitionLink -> {
				return Type.of3(
					new Type.S3(getTypeReference( typeDefinitionLink ))
				);
			}
			//case TypeDefinitionUndefined u -> u; // Can't be shown in ast.ol types InlineDefinition
			case TypeInlineDefinition typeInlineDefinition -> {
				return Type.of1(
					new Type.S1( getTreeType(typeInlineDefinition) )
				);
			}
		}
	}

	private ChoiceType getChoiceType(TypeChoiceDefinition typeChoiceDefinition) {
		return ChoiceType.builder()
			.textLocation( location( typeChoiceDefinition ) )
			.left( getType( typeChoiceDefinition.left() ) )
			.right( getType( typeChoiceDefinition.right() ) )
			.build();
	}

	private LocatedSymbolRef getTypeReference(TypeDefinitionLink typeDefinitionLink) {
		// TODO should the LocatedSymbolRef.contentValue here be the local name of the type or the name at the original definition?
		return new LocatedSymbolRef( typeDefinitionLink.simpleName(), location(typeDefinitionLink) );
	}

	private TreeType getTreeType(TypeInlineDefinition typeInlineDefinition) {
		TreeType.Builder builder = TreeType.builder()
			.textLocation( location(typeInlineDefinition) )
			.basicType( getBasicType(typeInlineDefinition.basicType()) );

	}

	private BasicType getBasicType(BasicTypeDefinition basicTypeDefinition) {

		return switch ( basicTypeDefinition.nativeType() ) {
			case STRING -> BasicType.of6( getStringBasicType( basicTypeDefinition ) );
			case INT ->  BasicType.of3( getIntBasicType( basicTypeDefinition ));
			case LONG -> BasicType.of4( getLongBasicType( basicTypeDefinition ) );
			case BOOL -> BasicType.of2( getBoolBasicType() );
			case DOUBLE -> BasicType.of5( getDoubleBasicType( basicTypeDefinition ) );
			case VOID -> BasicType.of1( getVoidBasicType() );
			case ANY, RAW -> {
				// FIXME not supported in ast.ol
				throw new NotImplementedException("no corresponding type in ast.ol");
			}
			case null, default -> {
				throw new NotImplementedException("no corresponding type in ast.ol");
			}
		};


	}

	private StringBasicType getStringBasicType(BasicTypeDefinition basicTypeDefinition) {

	}

	private IntBasicType getIntBasicType(BasicTypeDefinition basicTypeDefinition) {

	}

	private LongBasicType getLongBasicType(BasicTypeDefinition basicTypeDefinition) {

	}
	private BoolBasicType getBoolBasicType() {
		return new BoolBasicType( new JolieNative.JolieVoid() );

	}
	private DoubleBasicType getDoubleBasicType(BasicTypeDefinition basicTypeDefinition) {
		basicTypeDefinition.refinements()

		return new DoubleBasicType(  )
	}
	private VoidBasicType getVoidBasicType() {
		return new VoidBasicType( new JolieNative.JolieVoid() );

	}



	private Location location( OLSyntaxNode node ) {
		jolie.lang.parse.context.Location nodeLocation = node.context().location();

		Position startPosition = new Position( nodeLocation.range().start().character(), nodeLocation.range().start().line() );
		Position endPosition = new Position( nodeLocation.range().end().character(), nodeLocation.range().end().line() );
		Range range = new Range( startPosition, endPosition);

		return Location
			.builder()
			.source( nodeLocation.documentUri().toString() )
			.range(range)
			.build();
	}

	/**
	 *
	 * @param modulePath The path to the module in URI form (e.g. "file:///home/user/main.ol").
	 * @return The Module
	 */
	public Value parseModule( String modulePath ) {

		Program moduleProgram = getModuleProgram( modulePath );

		return Module.toValue(Module.builder()
			.types( parseTypes(moduleProgram) )
			.services(parseServices( moduleProgram ))
			.interfaces( parseInterface(moduleProgram) )
			.build()
		);
	}
}
