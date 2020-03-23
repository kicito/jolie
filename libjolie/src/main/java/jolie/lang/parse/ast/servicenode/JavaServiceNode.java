package jolie.lang.parse.ast.servicenode;

import jolie.lang.Constants;
import jolie.lang.Constants.ServiceType;
import jolie.lang.parse.OLVisitor;
import jolie.lang.parse.ast.InputPortInfo;
import jolie.lang.parse.ast.OutputPortInfo;
import jolie.lang.parse.context.ParsingContext;
import jolie.lang.parse.util.ProgramInspector;

public class JavaServiceNode extends ServiceNodeParameterize
{

    private static final long serialVersionUID = Constants.serialVersionUID();
    private String javaClass;

    public JavaServiceNode( ParsingContext context, String javaClass, String name )
    {
        super( context, name );
        this.javaClass = javaClass;
    }

    @Override
    public void accept( OLVisitor visitor )
    {
        visitor.visit( this );
    }

    @Override
    public String getTarget()
    {
        return javaClass;
    }

    @Override
    public String toString()
    {
        return "decl service Java(\"" + javaClass + "\") " + super.name() + "( + "
                + super.parameterType() + " )";
    }

    @Override
    public ServiceType getType()
    {
        return ServiceType.JAVA;
    }

    @Override
    public ServiceNodeParameterize resolve( ParsingContext ctx, ProgramInspector pi, String localID )
    {
        ServiceNodeParameterize localService = new JavaServiceNode( ctx, this.javaClass, this.name() );
        localService.setParameter( this.parameterType(), this.parameterPath() );
        this.getInputPortInfos().forEach(
                ( String name, InputPortInfo ip ) -> localService.addInputPortInfo( ip ) );

        this.getOutputPortInfos().forEach(
                ( String name, OutputPortInfo ip ) -> localService.addOutputPortInfo( ip ) );
        if ( this.main() != null ) {
            localService.setMain( this.main() );
        }
        if ( this.init() != null ) {
            localService.addInit( this.init() );
        }
        this.deploymentInstructions().forEach( di -> localService.addDeploymentInstruction( di ) );
        return localService;
    }

    
}