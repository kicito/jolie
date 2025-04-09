package joliex.meta.spec.faults;

public class CodeCheckException extends jolie.runtime.FaultException {

	private final jolie.runtime.embedding.java.JolieValue fault;

	public CodeCheckException( jolie.runtime.embedding.java.JolieValue fault ) {
		super( "CodeCheckException", jolie.runtime.embedding.java.JolieValue.toValue( fault ) );
		this.fault = java.util.Objects.requireNonNull( fault );
	}

	public jolie.runtime.embedding.java.JolieValue fault() {
		return fault;
	}
}
