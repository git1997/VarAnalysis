package errormodel;

public interface SymExErrorHandler {

		public void error(SymExException exception) ;

		public void fatalError(SymExException exception);

		public void warning(SymExException exception) ;
}
