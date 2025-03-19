/**
 * 
 */
package fsb.extrapolation;


import fsb.utils.Options;
import fsb.utils.Options.PredicateSynthesisMethodT;

/**
 * @author yurime
 *
 */
public class PredicateSynthesisFactory {
	/**
	 * returns the synthesis method according to the previously parsed method from parameters or the default one.
	 * @return
	 */
	public static IGuessPredicates getPredicateSynthesizer(){
		if(Options.predicate_synthesis_method == PredicateSynthesisMethodT.WEAKES_PRECONDITION){
			return new WPInfer(); //probably we need to use predicateExtrapolationFactory.
			
		}else if(Options.predicate_synthesis_method == PredicateSynthesisMethodT.INTERPOLATION){
			return new Interpolate();
			
		}//else{
		throw new RuntimeException("Error State in fsb.extrapolation.PredicateSynthesisFactory: unknown state of predicate_synthesis_method");
	}
}
