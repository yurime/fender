package fsb.utils;

import fsb.explore.statespace.StateQueue;
import fsb.explore.statespace.StateStack;
import fsb.parser.Main;
import fsb.trie.Trie;

public class Options {
	/**
	 * YM: have no idea what setting it to true does - I think it's deprecated (the written file is usually empty) 
	 */
	public static boolean WRITE_TS = false;
	/**
	 * if {@link #DELETE_STATES_IN_ATOMICS} is true
	 * will be set to false and not keep the TS system.
	 * 
	 */
	public static boolean USE_TS = false;
	
	public static enum StateSpaceStructT{
		TRIE,HASHMAP,BDD
	};
	public static StateSpaceStructT USE_FOR_STATE_SPACE = StateSpaceStructT.HASHMAP;
	
	/*  ***************************
	 * exploration methodology options
	 ******************************/
	public static boolean LAZY_COPY = true;
	/**
	 * sets if the system treats "nop" as local instruction,
	 * meaning via optimization - it is not an interesting 
	 * place for a context switch and checking of assertion breaches
	 */
	public static boolean LOCAL_NOP = false;	
	/**
	 * if the exploration reached and found an error trace,
	 * should it halt? or go on and find&collect all the error states?
	 */
	public static boolean AVOID_EXPLORING_FROM_ERR = true;
	/**
	 * various optimization for the case where all the variables save
	 * the program counter can contain 0,1(or *).<br>
	 * currently the system probably doesn't work when this var is not set to true.
	 */
	public static boolean IS_BOOLEAN_PROGRAM = true;
	/**
	 * an optimization attempt of one point where the long assume
	 * statements were broken down to subsequent short ones.
	 * this might increase the state space unless the connection
	 * of the small assumes is remembered and that the states can be joined again.
	 * 
	 */
	public static boolean BREAK_ASSUMES = false;

	/**
	 * should (*,1) and (1,1) be joined?<br>
	 * should (1,1) and (0,1) be joined?<br>
	 * should (1,1) and (0,0)?<br>
	 */
	public static int USE_STATE_SUBSUMPTIUON_LEVEL = -1;
	
	/**
	 * when set to true will delete states of atomics sections
	 * once those are no longer needed.
	 * Setting it to true should set {@link #USE_TS} 
	 * and {@link #PRINT_FIRST_ERROR_TRACE} to false.
	 * 
	 */
	public static boolean DELETE_STATES_IN_ATOMICS = true;
	
	/**
	 * currently used for the {@link Trie} only,
	 * probably will work together with {@link #USE_STATE_SUBSUMPTIUON_LEVEL}.
	 */
	public static int MAX_RETURNED_FOR_SUBSUME_IN_ELEM_SEARCH = 1;
	
	public static boolean RESET_LOCALS_AT_END_ATOMICS = false;
	
	/**
	 * chooses if the exploration method will be DFS or BFS. <br>
	 * If the states to explore stract will be {@link StateQueue} or {@link StateStack}
	 */
	public static boolean EXPLORATION_METHOD_DFS = true;
	
	/* ***************************
	 * Error trace options
	 *****************************/
	/**
	 * should several error traces be search (note it differs from {@link #AVOID_EXPLORING_FROM_ERR}
	 * in that even once an error state is found several paths might lead to it.
	 */
	public static boolean HALT_AT_FIRST_ERROR_TRACE = true;

	/**
	 * should be set to false if {@link #DELETE_STATES_IN_ATOMICS} is true.
	 * and if {@link #USE_TS} is set to false.
	 */
	public static boolean PRINT_FIRST_ERROR_TRACE = false;
	
	/**relevant only if PRINT_FIRST_ERROR_TRACE is true
	 * set to null if you want to print the error trace only to the screen
	 */
	public static String fileNameForErrorTrace = "errorTrace.tmp";
	
	/**relevant only if PRINT_FIRST_ERROR_TRACE is true
	 * set to true if you don't want to print the statements in error trace only the comments
	 */
	public static boolean PRINT_ONLY_COMMENTS_IN_ERROR_TRACE = true;
	
	/**relevant only if PRINT_FIRST_ERROR_TRACE is true and PRINT_ONLY_COMMENTS_IN_ERROR_TRACE=true
	 * set to true if you want the comments to be printed without spaces and without the states.
	 */
	public static boolean SUCCINT_ERROR_TRACE = true;
	
	/* *****************************
	 * Statistics options
	 ******************************/
	public static boolean PRINT_USED_CUBES = false;
	
	public static boolean PRINT_FOCUS_STATISTICS = false;
	
	public static boolean INVOKE_GC_BEFORE_END_STATISTICS = false;
	
	/**
	 * A finer option of the above PRINT_USED_CUBES which will print cubes 
	 * per statement at the end of the execution
	 */
	public static boolean PRINT_USED_CUBES_PER_STATEMENT = false;
	
	public static boolean PRINT_INTERMEDIATE_MAX_MEM = false;

	/* *****************************
	 * debugging
	 ******************************/
	/**
	 * should be used with {@link #PRINT_STATE_AT_PROCESS}
	 * print the program state each time the execution reaches label 
	 * PRINT_STATE_AT_LABEL at program id PRINT_STATE_AT_PROCESS
	 * controlled from execution line with -trackLabel 
	 */
	public static int PRINT_STATE_AT_LABEL = -1;
	/**
	 * goes with {@link #PRINT_STATE_AT_LABEL}
	 * print the program state each time the execution reaches label 
	 * PRINT_STATE_AT_LABEL at program id PRINT_STATE_AT_PROCESS
	 * controlled from execution line with -trackLabel 
	 */
	public static int PRINT_STATE_AT_PROCESS = -1;
	
	
	/* ***************************
	 * fender fence synthesis
	 ************************** */
	/**
	set to false if want to synthsize a solution and not just verify
	 */
	public static boolean ONLY_MODEL_CHECKER = true;


	public static enum debugMechanismType {ATOMIC_SECTIONS,FENCES};
	public static debugMechanismType  DEBUG_MECHANISM = debugMechanismType.ATOMIC_SECTIONS;
	
	/* ***************************
	 * fender predicate synthesis
	 ************************** */
	
	
	/**
	set to true if want to synthsize predicates
	 */
	public static boolean SYNTHESIZE_PREDICATES = true;
	/**
		parse a predicate file after fender ran
		predicate file name will come from fender input parameters probably with a flag -predicateFile
		see {@link Main#getPredicateFile}
	 */
	public static boolean PARSE_PRED_FILE = true;
	
	/**
		in case {@link #IS_BOOLEAN_PROGRAM}, parse the original program 
		error trace from the boolean program error trace.
		reads from instructions encoded in the boolean program comments 
		and from assumes.
	 */
	public static boolean PARSE_ORIGPROG_TRACE_FROM_ERR_TRACE = true;

	/**
	for predicate synthesis with interpolants controls during synthesized predicate filtering whether to add or not cubed predicates (with and,or, iff) to output. 
		 */
	public static final boolean ADD_SYTHESIZED_CUBE_PRED = false;
	/**
	 * C2bp has an implementation issue where the program can jump outside an atomic section
	 * from within it, that causes the existence of two states in atomic one is not.
	 * Hopefully this is fixed, then remove all references to this (and hopefully this will not find Fender bugs)
	 * default true
	 */
	public static boolean C2BP_IMPLEMENTATION_ISSUE=true;
	
	/**
	 * the name of the predicate file into which to synthesize predicates
	 * see {@link Main#getSythesizePredicateParameter}
	 * default null
	 */
	public static String target_predicate_file = null;
	
	public static enum PredicateSynthesisMethodT{
		WEAKES_PRECONDITION,INTERPOLATION
	};
	/**
	 * the method by which to synthesize predicates
	 * see {@link Main#getPredicateSynthesisMethod}
	 * default WP
	 */
	public static PredicateSynthesisMethodT predicate_synthesis_method = PredicateSynthesisMethodT.WEAKES_PRECONDITION;
	
	/**
	 * the name of the predicate file into which to synthesize predicates
	 * see {@link Main#getPredicateFile}
	 * default null
	 */
	public static String predicate_file = null;
	
	

	
	
}
