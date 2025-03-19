package fsb.parser;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.security.cert.PKIXRevocationChecker.Option;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;


import ags.constraints.AtomicPredicateDictionary;
import fsb.ast.BoolExpr;
import fsb.ast.MProgram;
import fsb.ast.Statement;
import fsb.ast.SymbolTable;
import fsb.explore.Action;
import fsb.explore.Explorer;
import fsb.explore.ExprValidator;
import fsb.explore.SBState;
import fsb.explore.State;
import fsb.explore.Validator;
import fsb.explore.statespace.TrieMapStateSpace;
import fsb.extrapolation.IGuessPredicates;
import fsb.extrapolation.Interpolate;
import fsb.extrapolation.PredicateSynthesisFactory;
import fsb.extrapolation.WPInfer;
import fsb.semantics.Semantics;
import fsb.semantics.naivepso.NaiveAbsSemantics;
import fsb.semantics.eagerpso.EagerAbsSemantics;
import fsb.semantics.empeagerpso.EmpEagerAbsSemantics;
import fsb.semantics.pso.*;
import fsb.semantics.sc.*;
import fsb.utils.Debug;
import fsb.utils.Options;
import fsb.utils.Options.PredicateSynthesisMethodT;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;;
@SuppressWarnings("unused")
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] argv) {

		Lexer scanner = null;
		Semantics sem = null;
		SymbolTable.reset();
		if(argv.length < 1){
			System.out.println(Main.usage());
			System.out.println("\n missing parameters");
			System.exit(1);
		}
		scanner = getScannerFromInput(argv);

		System.out.println("--- Commencing parsing ---");

		try {
			parser p = new parser(scanner);
			MProgram mp = (MProgram) p.parse().value;
			sem = parseParametersAndReturnTheSemantics(argv, mp);

			if(mp.getValidator() == null){
				throw new RuntimeException("Check if you defined an asserion at the end of the boolean program");
			}
			
			System.out.println("--- Finished parsing successfully. ---\nPrinting out the parsed program:");
			System.out.println(mp);
	        System.out.println("--- Assertion:");
			System.out.println(((ExprValidator)mp.getValidator()).getAssertion());

			// The next line constracts the initial state and adds a
			// validator(that says if a state is an error state) to the run.
			// possible validators are defined below as private static
			// variables.
			// if a validator is not given an assertion is needed at the end of
			// the test.
			// State init = sem.getStateFactory().newState(mp, dekkerCritSec,
			// mp.getProcs());
			State init = sem.getStateFactory().newState();

			
			Explorer e = new Explorer(sem);
			e.setFileName(argv[0]);
			e.SetInitial(init);
			e.explore();
			if(Options.SYNTHESIZE_PREDICATES && e.getTs().getErrorStates().size() > 0){
				synthesizePredicates(e);
			}
			return;
		} catch (Throwable e) {
			System.out.println("An error occured : \n" + e + "\n" + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}
	
/**
 * TODO: the no new predicate, string comparison, check is bug prone
 * 			Should convert to fsb.BoolExpr (or to Z3.BoolExpr the original predicates) and compare.  
 * @param e
 */
	private static void synthesizePredicates(Explorer e){
			if( ! (Options.PARSE_ORIGPROG_TRACE_FROM_ERR_TRACE || Options.PARSE_PRED_FILE )){
				throw new IllegalArgumentException("why are PARSE_ORIGPROG_TRACE_FROM_ERR_TRACE and PARSE_PRED_FILE vars not set?" + e);
			}
			System.out.println("\nStarting parsing of concrete domain err trace counter part and assumes\n");
			ArrayList<Action> concrete_trace = e.getTs().errTraceCommentsAndAssumesToStatementList();
			
			printErrorTrace(concrete_trace);
		
			System.out.println("\nStarting parsing of predicates from predicate file");
			ArrayList<BoolExpr> predicates = parsePredicatesFromFile(Options.predicate_file);
			
			IGuessPredicates pred_synthesis = PredicateSynthesisFactory.getPredicateSynthesizer();

		    List<String > ret_predicates_str = pred_synthesis.guessStringPredicates(predicates, concrete_trace);
			
		    List<String> orig_pred = toStringList(predicates);
		    
		    //if (predicates.containsAll(ret_predicates)) {
		    if (orig_pred.containsAll(ret_predicates_str)) {
			        System.out.println("no new predicates found");
		        //throw new RuntimeException("no new predicates found");
			    return;
		    }
		    
		    //ret_predicates.addAll(predicates);
		    System.out.println("returned: " + ret_predicates_str);
			printPredicatesToFile(ret_predicates_str, Options.target_predicate_file);
		
	}


	/**
	 * @param concrete_trace
	 */
	private static void printErrorTrace(ArrayList<Action> concrete_trace) {
		concrete_trace.stream().forEach((a) -> {System.out.print(a.toString() + "\n" + a.getSource().toString());} );
	}


	/**
	 * @param predicates
	 * @return
	 */
	private static List<String> toStringList(ArrayList<BoolExpr> predicates) {
		return predicates.stream().map(p->p.toString()).collect(Collectors.<String> toList());
	}


	/**
	 * @param ret_predicates
	 * @return
	 */
	private static ArrayList<String> removeStringDuplicates(List<String> ret_predicates) {
		return new ArrayList<String>(new HashSet<String>(ret_predicates));
	}
	/**
	 * @param ret_predicates
	 * @return
	 */
	private static ArrayList<BoolExpr> removeDuplicates(List<BoolExpr> ret_predicates) {
		return new ArrayList<BoolExpr>(new HashSet<BoolExpr>(ret_predicates));
	}
	/**
	 * parses predicates from the original predicate file given during fender invocation.
	 * @param predicate_file TODO
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static ArrayList<BoolExpr> parsePredicatesFromFile(String predicate_file) {
		String[] filename = {predicate_file};
		if(predicate_file == "" ){
			throw new IllegalArgumentException((usage() 
						+ "\n No predicate file was given. Add flag \"-predicateFile\"\n"));
		}
		PredLexer scanner = getPredScannerFromInput(filename);
		PredicateParser pred_p = new PredicateParser(scanner);
		ArrayList<BoolExpr> predicates;
		try {
			predicates = (ArrayList<BoolExpr>) pred_p.parse().value;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Caught exception during file " + predicate_file + "parsing" + e.getMessage(), e);
		}
		System.out.println(predicates);
		System.out.println("\nParsing seems to be successful\n");
		return predicates;
	}


	private static void printPredicatesToFile(List<String> ret_predicates, String target_predicate_file) {				
			
			try(PrintWriter fw = new PrintWriter(target_predicate_file)) {
				for(String pred : ret_predicates){
					fw.println(pred);
				};
			} catch (FileNotFoundException e) {
				throw new RuntimeException("\n couldn't find file to write predicates into", e);
			}
	}


	private static Lexer getScannerFromInput(String[] argv) {
		Lexer scanner = null;
		try {
			scanner = new Lexer(new java.io.FileReader(argv[0]));
		} catch (java.io.FileNotFoundException e) {
			throw new IllegalArgumentException("File not found : \"" + argv[0] + "\"", e);
		} 
		argv[0] = "";
		return scanner;
	}
	
	private static PredLexer getPredScannerFromInput(String[] argv) {
		PredLexer scanner = null;
		try {
			scanner = new PredLexer(new java.io.FileReader(argv[0]));
		} catch (java.io.FileNotFoundException e) {
			throw new IllegalArgumentException("File not found : \"" + argv[0] + "\"", e);
		} 
		argv[0] = "";
		return scanner;
	}
	
	/**
	 *  TODO: Maybe use NULLABLE in parsing parameters.
	 */
	private static Semantics parseParametersAndReturnTheSemantics(
			String[] argv, MProgram mp) throws Exception {
		Semantics sem = null;
			// all those function maybe should check that the second (index+1) parameter is not null
			sem = getSemanticsLevelFromInput(argv, mp, sem);

			getErrorPathParameterFromInput(argv);

			getDebugLevelIndexFromInput(argv);

			getTrackLabelParameterFromInput(argv);

			getCubeStatisticsPrintoutParameterFromInput(argv);

			getDeleteStatesInAtomicsFromInput(argv);
			
			getSubsumptionLevel(argv);
			
			getKeepTransitionSystemParameterFormInput(argv);

			getStateSpaceTypeToUse(argv);
			
			getStateInvokeGCBeforeEndStatistics(argv);
			
			getPrintIntermediateMem(argv);
			
			getUseDFSSearchHeuristic(argv);
			
			getSythesizePredicateParameter(argv);
			
			
			checkThatAllTheParametersWereRecognized(argv);
			checkSetUpConsistency();
			
		return sem;

	}


/***
 * prints the usage flags of this tool 
 * TODO 
 * @return
 */
	static String usage(){
		return "The mandatory arguments to fender are:\n"
				+"\n"
				+ " <the name of the program to analize> \n"
				+ " (sc|pso|set|must|may) \n"
				+ "for relaxed memory models also an <int indicating the buffer size> must follow.\n"
				+"\n"
		     + "optional arguments are:\n"
				+ " -errorPath (succinct|onlyComments|full|none)\n"
				+ "default is full\n"
				+ " -synthesizePredicatesInto <predicateFileName>\n"
				+ "set to for predicate inference\n"				 
				+ " -predicateFile <predicateFileName>\n"
				+ "for predicate inference from the error trace using this option one should specify the predicate file with which the boolean program was created"				 
				+ " -predicateSynthesisMethod <Interp|WP>\n"
				+ "in what method to synthesize predicates. \"Interp\" for interpolation; \"WP\" for Weakest Precondition.\n"
				+ "default \"WP\"\n"
				+ " -debugLevel <an int indicating the debug level>\n"
				+ "default is 0 \n"
				+ " -printUsedCubes (true|perStatement|false)\"\n"
				+ "default is false \n"
				+ "-trackLabel (pc[<DesiredPID>]=<DesiredLabel>)\"\n"
				+ "to print the program state each time the execution reaches label <DesiredLabel> at program id <DesiredPID>\n"
				+ "-deleteStatesInAtomics (True|False) \n" 
				+ " delete states in atomics\n"
				+ "default is true\n"
				+"-keepTransitionSystem (True|False)\n"
				+" keep or not the transition system \n"
				+ "default is false\n"
				+ "-subsLev (<Int>)\n"
				+ "set subsumption level\n"
				+ "where -1 will turn the subsumption level off\n"
				+ "default is -1\n"
				+ "\"-stateSpace (HashMap|Trie)\"\n"
				+ "setting the state space structure type \n"
				+"for trie you can add a numeric parameter\"-stateSystem Trie (no_opt|optm1|optm2|optm3)\" describing the desired level of optimization\n"
				+ "default is HashMap\n"
				+"\"-invokeGCBeforeEndStatistics\""
				+" requesting the gc to be activated before the printout of the end statistics"
				+"\"-printIntermediateMemConsumption\""
				+" print intermediate memory consumption"
				+"\"-searchHeuristics (DFS|BFS)\" \n" 
				+ "chooses if the exploration method will be DFS or BFS.\n "
				+ "default is DFS\n";
	}
	
	/**
	 * @param argv
	 */	
	private static void getSythesizePredicateParameter(String[] argv)  throws Exception{
		int synthesize_predicates_target_file_index = getStringIndex(argv,
				"-synthesizePredicatesInto");
		
		getPredicateFile(argv);
		
		getPredicateSynthesisMethod(argv);
		if (synthesize_predicates_target_file_index != -1) {
			if(Options.predicate_file == null){
				throw new Exception("when using \"-synthesizePredicatesInto \" the flag \"-predicateFile\" should be set as well \n");
			}
			try{
				Options.SYNTHESIZE_PREDICATES = true;
				Options.target_predicate_file = argv[synthesize_predicates_target_file_index+1];
				
				Options.PRINT_INTERMEDIATE_MAX_MEM = true;
				argv[synthesize_predicates_target_file_index] = "";
				argv[synthesize_predicates_target_file_index+1] = "";
			}catch(Exception e){
				throw new Exception("The use of \"-synthesizePredicatesInto\" is \"-synthesizePredicatesInto <file name>\"\n" + e, e);
			}
			
		}else{
			if(Options.predicate_file != null){
				throw new Exception("when using \"-predicateFile\" the \"-synthesizePredicatesInto\" flag should be set as well \n");
			}
			Options.SYNTHESIZE_PREDICATES = false;
		}
	}
	
	
	/**
	 * The use of \"-searchHeuristics\" is \"-searchHeuristics (DFS|BFS)\"\n
	 * @param argv
	 */
	private static void getUseDFSSearchHeuristic(String[] argv) throws Exception{
		int search_heuristics_index = getStringIndex(argv,
				"-searchHeuristics");
		if (search_heuristics_index != -1) {
			try{
				String search_heuristics = argv[search_heuristics_index+1];
				if(search_heuristics.equals("DFS")){
					Options.EXPLORATION_METHOD_DFS = true;
				}else if(search_heuristics.equals("BFS")){
					Options.EXPLORATION_METHOD_DFS = false;
				}else{
					throw new RuntimeException("non DFS or BFS string");
				}
				Options.PRINT_INTERMEDIATE_MAX_MEM = true;
				argv[search_heuristics_index] = "";
				argv[search_heuristics_index+1] = "";
			}catch(Exception e){
				throw new IllegalArgumentException("The use of \"-searchHeuristics\" is \"-searchHeuristics (DFS|BFS)\"\n" + e, e);
			}
		}

	}
	/**
	 * If there is -printIntermediateMemConsumption or not
	 * @param argv
	 */
	private static void getPrintIntermediateMem(String[] argv) {
		int intermediate_mem_print_index = getStringIndex(argv,
				"-printIntermediateMemConsumption");
		if (intermediate_mem_print_index != -1) {
			Options.PRINT_INTERMEDIATE_MAX_MEM = true;
			argv[intermediate_mem_print_index] = "";
		}else{
			Options.PRINT_INTERMEDIATE_MAX_MEM = false;
		}

	}
	

	/**
	 * The use of \"-predicateFile\" is \"-predicateFile <file name>\"\n
	 * @param argv
	 */
	private static void getPredicateFile(String[] argv) throws Exception{
		int predicate_file_index = getStringIndex(argv,
				"-predicateFile");
		if (predicate_file_index != -1) {
			try{
				Options.predicate_file = argv[predicate_file_index+1];
				
				argv[predicate_file_index] = "";
				argv[predicate_file_index+1] = "";
			}catch(Exception e){
				throw new Exception("The use of \"-predicateFile\" is \"-predicateFile <file name>\"\n" + e, e);
			}
		}

	}
	/**
	 * 
	 * 				+ " -predicateSynthesisMethod <Interp|WP>\n"
				+ "in what method to synthesize predicates. \"Interp\" for interpolation; \"WP\" for Weakest Precondition."
	 * @param argv
	 */
	private static void getPredicateSynthesisMethod(String[] argv) throws Exception{
		int predicate_file_index = getStringIndex(argv,
				"-predicateSynthesisMethod");
		if (predicate_file_index != -1) {
			try{
				String syntheis_string  = argv[predicate_file_index+1];
				
				if(syntheis_string.equals("Interp")){
					Options.predicate_synthesis_method = Options.PredicateSynthesisMethodT.INTERPOLATION;
				}else if(syntheis_string.contentEquals("WP")){
					Options.predicate_synthesis_method = Options.PredicateSynthesisMethodT.WEAKES_PRECONDITION;
				}else{
					throw new Exception();
				}
				
				argv[predicate_file_index] = "";
				argv[predicate_file_index+1] = "";
			}catch(Exception e){
				throw new Exception(" The use of \"-predicateSynthesisMethod\" is \"-predicateSynthesisMethod <method>\"\n"
						+ " where method is  \"Interp\" for interpolation; \"WP\" for Weakest Precondition"+ e, e);
			}
		}

	}
	private static String getFlaghValueOrNull(String[] argv, String FlagName, String ExpectedStringForNoValException) throws Exception{
		String retVal = null;
		int flag_index = getStringIndex(argv, FlagName);
		if (flag_index != -1) {
			try{
				retVal = argv[flag_index+1];
				
				argv[flag_index] = "";
				argv[flag_index+1] = "";
			}catch(Exception e){
				throw new Exception("The use of \"" + FlagName + "\" is \"" + FlagName + "  \""+ ExpectedStringForNoValException+ "\"\n" + e, e);
			}
		}
		return retVal;				

	}

	/**
	 * @param argv
	 */
	private static void getStateInvokeGCBeforeEndStatistics(String[] argv) throws Exception{
		int invoke_gc_index = getStringIndex(argv,
				"-invokeGCBeforeEndStatistics");
		if (invoke_gc_index != -1) {
			Options.INVOKE_GC_BEFORE_END_STATISTICS = true;
			argv[invoke_gc_index] = "";
		}else{
			Options.INVOKE_GC_BEFORE_END_STATISTICS = false;
		}

	}

	
	/**
	 * @param argv
	 */
	private static void getStateSpaceTypeToUse(String[] argv) throws Exception{
		int state_system_type_index = getStringIndex(argv,
				"-stateSpace");
		if (state_system_type_index != -1) {
			
			String requested_state_system = argv[state_system_type_index + 1];
			argv[state_system_type_index] = "";
			argv[state_system_type_index + 1] = "";
			
			if(requested_state_system.equals("HashMap")){
				Options.USE_FOR_STATE_SPACE = Options.StateSpaceStructT.HASHMAP;
				return;
			}
			if(requested_state_system.equals("Trie")){
				Options.USE_FOR_STATE_SPACE = Options.StateSpaceStructT.TRIE;
				Integer optimization_level = getOptimizationLevel(argv, state_system_type_index);
				
				switch(optimization_level){
				case 0: {
					TrieMapStateSpace.OPTIMIZATION_LEVEL = TrieMapStateSpace.optimization_level_t.NONE;
					break;
				}
				case 1: {
					TrieMapStateSpace.OPTIMIZATION_LEVEL = TrieMapStateSpace.optimization_level_t.OPT1;
					break;
				}
				case 2: {
					TrieMapStateSpace.OPTIMIZATION_LEVEL = TrieMapStateSpace.optimization_level_t.OPT2;
					break;
				}
				case 3: {
					TrieMapStateSpace.OPTIMIZATION_LEVEL = TrieMapStateSpace.optimization_level_t.OPT3;
					break;
				}
				default: {
					throw new IllegalArgumentException("currenly the optimization level " + optimization_level 
							+ "is unavalable");
				}
				}//end of switch
					

					return;
			}//else: requested_state_system set to non handled value 
				
			throw new IllegalArgumentException("The proper use setting the state space type is \"-stateSpace (HashMap|Trie)\"\n"
					+"for trie you can add a numeric parameter\"-stateSystem Trie (no_opt|optm1|optm2|optm3)\" \n"
					+ "default is HashMap"
					+ "describing the desired level of optimization\n"
					);
			
		}
	}


	/**
	 * @param argv
	 * @param state_system_type_index
	 * @return
	 */
	private static Integer getOptimizationLevel(String[] argv, int state_system_type_index) {
		Integer try_optimization_level=3;
		try{
		if(argv.length > state_system_type_index + 2 ){
			try_optimization_level = Integer.parseInt(argv[state_system_type_index + 2]);
			argv[state_system_type_index + 2] = "";
		}
		}catch(NumberFormatException e){
			//doing nothing if no optimization level specified
		}catch(ArrayIndexOutOfBoundsException e){
			//doing nothing if no optimization level specified
		}
		return try_optimization_level;
	}

	/**
	 * @param argv
	 */
	private static void getSubsumptionLevel(String[] argv) {
		int subsumptionLevelIndex = getStringIndex(argv,
				"-subsLev");
		if (subsumptionLevelIndex != -1) {
			try{
			Options.USE_STATE_SUBSUMPTIUON_LEVEL = Integer.parseInt(argv[subsumptionLevelIndex + 1]);
			}catch(NumberFormatException e){
				throw new IllegalArgumentException("The proper use of set subsumption level is \"-subsLev (<Int>)\"\n"
						+"where -1 will turn the subsuption level off"
						+ "default is -1", e);
			}
			argv[subsumptionLevelIndex] = "";
			argv[subsumptionLevelIndex + 1] = "";
		}
	}

	/**
	 * @param argv
	 */
	private static void getKeepTransitionSystemParameterFormInput(String[] argv) {
		int keepTransitionSystemIndex = getStringIndex(argv,
				"-keepTransitionSystem");
		if (keepTransitionSystemIndex != -1) {

			String keepTransitionSystemString = argv[keepTransitionSystemIndex + 1];
			if (keepTransitionSystemString.equalsIgnoreCase("true")) {
				Options.USE_TS = true;
			} else if (keepTransitionSystemString
					.equalsIgnoreCase("false")) {
				Options.USE_TS = false;
			} else {

				throw new IllegalArgumentException("The proper use of keep transition system is \"-keepTransitionSystem (True|False)\"\n"
								+ "default is false");
			}
			argv[keepTransitionSystemIndex] = "";
			argv[keepTransitionSystemIndex + 1] = "";
		}
	}

	/**
	 * @param argv
	 */
	private static void getDeleteStatesInAtomicsFromInput(String[] argv) {
		int deleteStatesInAtomicsIndex = getStringIndex(argv,
				"-deleteStatesInAtomics");
		if (deleteStatesInAtomicsIndex != -1) {

			String deleteStatesInAtomicsString = argv[deleteStatesInAtomicsIndex + 1];
			if (deleteStatesInAtomicsString.equalsIgnoreCase("true")) {
				Options.DELETE_STATES_IN_ATOMICS = true;
			} else if (deleteStatesInAtomicsString
					.equalsIgnoreCase("false")) {
				Options.DELETE_STATES_IN_ATOMICS = false;
			} else {

				throw new IllegalArgumentException("The proper use of delete states in atomics is \"-deleteStatesInAtomics (True|False)\"\n"
								+ "default is true");
			}
			argv[deleteStatesInAtomicsIndex] = "";
			argv[deleteStatesInAtomicsIndex + 1] = "";
		}
	}

	/**
	 * @param argv
	 */
	private static void getCubeStatisticsPrintoutParameterFromInput(
			String[] argv) {
		int usedCubesIndex = getStringIndex(argv, "-printUsedCubes");
		if (usedCubesIndex != -1) {
			if (argv[usedCubesIndex + 1].contains("true")) {
				Options.PRINT_USED_CUBES = true;
				Options.PRINT_USED_CUBES_PER_STATEMENT = false;
			} else if (argv[usedCubesIndex + 1].contains("perStatement")) {
				Options.PRINT_USED_CUBES = true;
				Options.PRINT_USED_CUBES_PER_STATEMENT = true;
			} else if (argv[usedCubesIndex + 1].contains("off")) {
				Options.PRINT_USED_CUBES = false;
				Options.PRINT_USED_CUBES_PER_STATEMENT = false;
			} else {
				throw new IllegalArgumentException("The proper used cubes statistics is \"-printUsedCubes (true|perStatement|false)\"\n"
								+ "default is false ");
			}
			argv[usedCubesIndex] = "";
			argv[usedCubesIndex + 1] = "";

		}
	}

	/**
	 * @param argv
	 * @param mp
	 * @param sem
	 * @return
	 */
	private static Semantics getSemanticsLevelFromInput(String[] argv,
			MProgram mp, Semantics sem) {
		if (argv[1].equals("sc")) {
			sem = new SCSemantics(mp);
		} else if (argv[1].equals("pso")) {
			sem = new PSOSemantics(Integer.parseInt(argv[2]));
			argv[2] = "";
		} else if (argv[1].equals("set")) {
			sem = new NaiveAbsSemantics();
			argv[2] = "";
		} else if (argv[1].equals("must")) {
			sem = new EagerAbsSemantics(Integer.parseInt(argv[2]));
			argv[2] = "";
		} else if (argv[1].equals("may")) {
			sem = new EmpEagerAbsSemantics(Integer.parseInt(argv[2]));
			argv[2] = "";
		} else {
			throw new IllegalArgumentException("Invalid argument " + argv[1] + "!");
		}
		argv[1] = "";
		return sem;
	}

	/**
	 * @param argv
	 */
	private static void getTrackLabelParameterFromInput(String[] argv) {
		int trackLabelParametersIndex = getStringIndex(argv, "-trackLabel");
		if (trackLabelParametersIndex != -1) {
			java.util.regex.Pattern p = java.util.regex.Pattern
					.compile("pc\\[([0-9]*)\\]=([0-9]*)");
			java.util.regex.Matcher m = p
					.matcher(argv[trackLabelParametersIndex + 1]);
			if (m.matches()) {

				String gr1 = m.group(1);
				String gr2 = m.group(2);
				int desiredPID = Integer.parseInt(gr1);
				int desiredLabel = Integer.parseInt(gr2);
				if (Debug.DEBUG_LEVEL == 0) {
					Debug.DEBUG_LEVEL = 1;
				}
				Options.PRINT_STATE_AT_LABEL = desiredLabel;
				Options.PRINT_STATE_AT_PROCESS = desiredPID;

			} else {
				throw new IllegalArgumentException("The proper use of tracked label parameter is \"-trackLabel (pc[<DesiredPID>]=<DesiredLabel>)\"\n"
								+ "to print the program state each time the execution reaches label <DesiredLabel> at program id <DesiredPID>");
			}
			argv[trackLabelParametersIndex] = "";
			argv[trackLabelParametersIndex + 1] = "";
		}
	}

	/**
	 * @param argv
	 */
	private static void getDebugLevelIndexFromInput(String[] argv) {
		int debugLevelIndex = getStringIndex(argv, "-debugLevel");
		if (debugLevelIndex != -1) {
			try {
				String debugLevel = argv[debugLevelIndex + 1];
				Debug.DEBUG_LEVEL = Integer.parseInt(debugLevel);
			} catch (Exception e) {
				throw new IllegalArgumentException("The proper use of debug level is \"-debugLevel <an int indicating the debug level>\"\n"
								+ "default is 0", e);
			}
			argv[debugLevelIndex] = "";
			argv[debugLevelIndex + 1] = "";
		}
	}

	/**
	 * @param argv
	 */
	private static void getErrorPathParameterFromInput(String[] argv) {
		int errorPathParametersIndex = getStringIndex(argv, "-errorPath");
		if (errorPathParametersIndex != -1) {
			
			Options.PRINT_FIRST_ERROR_TRACE = true;
			if (argv[errorPathParametersIndex + 1].contains("succinct")) {
				Options.SUCCINT_ERROR_TRACE = true;
				Options.PRINT_ONLY_COMMENTS_IN_ERROR_TRACE = true;
			} else if (argv[errorPathParametersIndex + 1]
					.contains("onlyComments")) {
				Options.SUCCINT_ERROR_TRACE = false;
				Options.PRINT_ONLY_COMMENTS_IN_ERROR_TRACE = true;
			} else if (argv[errorPathParametersIndex + 1].contains("full")) {
				Options.SUCCINT_ERROR_TRACE = false;
				Options.PRINT_ONLY_COMMENTS_IN_ERROR_TRACE = false;
			}else if (argv[errorPathParametersIndex + 1].contains("none")) {
				Options.PRINT_FIRST_ERROR_TRACE = false;
			} else {
				throw new IllegalArgumentException("The proper use of error trace variablity level is \"-errorPath (succinct|onlyComments|full|none)\"\n"
								+ "default is full");
			}
			argv[errorPathParametersIndex] = "";
			argv[errorPathParametersIndex + 1] = "";
		}
	}

	private static void checkSetUpConsistency() {
//		if(Options.USE_STATE_SUBSUMPTIUON_LEVEL != 1){
//			throw new RuntimeException("currently cannot run without Options.USE_STATE_SUBSUMPTIUON_LEVEL = 1");
//		}
//		if (Options.DELETE_STATES_IN_ATOMICS == true) {
//			if (Options.USE_TS == true
//					|| Options.PRINT_FIRST_ERROR_TRACE == true) {
//				throw new RuntimeException(
//						"When deleting atomic section intermediate states \n"
//								+ "the transition system shouldn't be used and the first error trace shouldn't be printed!");
//			}
//		}

		if (Options.USE_TS == false && Options.PRINT_FIRST_ERROR_TRACE == true) {
			throw new RuntimeException("When not using a transition system"
					+ " the first error trace shouldn't be printed!");

		}

	}

	/**
	 * Checks that all the cells of argv contain "". Throws a runtime exception
	 * with the first string not equal to "" found.
	 * 
	 * @param argv
	 */
	private static void checkThatAllTheParametersWereRecognized(String[] argv) {
		for (String s : argv) {
			if (!s.equals("")) {
				throw new RuntimeException("The parameter " + s
						+ " was not recognized!\n" + argv);
			}
		}

	}

	private static int getStringIndex(String[] arr, String s) {

		for (int i = 0; i < arr.length; ++i) {
			if (arr[i].equals(s)) {
				return i;
			}
		}
		return -1;
	}

	private static Validator alwaysOk = new Validator() {
		public boolean isErr(State s) {
			return false;
		}
	};

	private static Validator dekkerCritSec = new Validator() {
		public boolean isErr(State s) {
			return ((SBState) s).getPC(1) == 13 && ((SBState) s).getPC(2) == 13;
		}
	};

	private static Validator peterCritSec = new Validator() {
		public boolean isErr(State s) {
			return ((SBState) s).getPC(1) == 10 && ((SBState) s).getPC(2) == 10;
		}
	};

	private static Validator lamportCritSec = new Validator() {
		public boolean isErr(State s) {
			return ((SBState) s).getPC(1) == 28 && ((SBState) s).getPC(2) == 28;
		}
	};

	private static Validator mcsCritSec = new Validator() {
		public boolean isErr(State s) {
			return ((SBState) s).getPC(1) == 8 && ((SBState) s).getPC(2) == 8;
		}
	};

	private static Validator clhCritSec = new Validator() {
		public boolean isErr(State s) {
			return ((SBState) s).getPC(1) == 9 && ((SBState) s).getPC(2) == 9;
		}
	};

	private static Validator fastCritSec = new Validator() {
		public boolean isErr(State s) {
			return ((SBState) s).getPC(1) == 22 && ((SBState) s).getPC(2) == 22;
		}
	};

	private static Validator senseCritSec = new Validator() {
		public boolean isErr(State s) {
			return ((SBState) s).getPC(1) == 2 && ((SBState) s).getPC(2) == 12
					|| ((SBState) s).getPC(1) == 12
					&& ((SBState) s).getPC(2) == 2;
		}
	};

	// private static Validator stupidValidator = new Validator() {
	// public boolean isErr(State s) {
	// return s.isFinal()
	// && (((SBState) s).getLocal(1, "r1") > ((SBState) s)
	// .getLocal(1, "r2") || ((SBState) s)
	// .getLocal(1, "r2") > ((SBState) s)
	// .getLocal(1, "r3"));
	// }
	// };
	//
	// private static Validator ey1Validator = new Validator() {
	// public boolean isErr(State s) {
	// return s.isFinal()
	// && (((SBState) s).getLocal(1, "r1") > ((SBState) s)
	// .getLocal(1, "r2")
	// || (((SBState) s).getLocal(1, "r2") > ((SBState) s)
	// .getLocal(1, "r3")) || (((SBState) s)
	// .getLocal(1, "r3") > ((SBState) s)
	// .getLocal(1, "r4")));
	// }
	// };
	//
	// private static Validator ey2Validator = new Validator() {
	// public boolean isErr(State s) {
	// return s.isFinal()
	// && (((SBState) s).getLocal(1, "r3") > ((SBState) s)
	// .getLocal(1, "r4"));
	// }
	// };
	//
	// private static Validator p0Validator = new Validator()
	// {
	// public boolean isErr(State s)
	// {
	// return s.isFinal() &&
	// ((SBState) s).getLocal(0, "r") == 2 &&
	// ((SBState) s).getLocal(1, "r") == 1 &&
	// ((SBState) s).getLocal(2, "r") == 1 &&
	// ((SBState) s).getShared("y") == 2;
	// }
	// };

}
