# Fender -- a model checker for boolean programs
## dependencies: 
- java 8
- libz3-java -- if predicate inference use is desired

## To run:
java -jar ./lib/fender_experimental.jar {filename}.bl sc


### The mandatory arguments to fender are:

 <the name of the program to analize> 
 (sc ~~|pso|set|must|may~~) 

For now, the only available option is: sc  
### optional arguments are:are
 -errorPath (succinct|onlyComments|full|none)

 -synthesizePredicatesInto <predicateFileName>
Set this option for predicate inference
	and  -predicateFile <predicateFileName>
    needs LD_LIBRARY_PATH be set to the location of libz3java.so
    
 -predicateFile <predicateFileName>
for predicate inference from the error trace using this option one should specify the predicate file with which the boolean program was created". Can be empty.

 -predicateSynthesisMethod <Interp|WP>
in what method to synthesize predicates. \"Interp\" for interpolation; \"WP\" for Weakest Precondition.
default \"WP\"

 -debugLevel <an int indicating the debug level>
default is 0 

 -printUsedCubes (true|perStatement|false)\"
default is false 

 -trackLabel (pc[<DesiredPID>]=<DesiredLabel>)\"
to print the program state each time the execution reaches label <DesiredLabel> at program id <DesiredPID>

 -deleteStatesInAtomics (True|False)  
delete states in atomics
default is true

 -keepTransitionSystem (True|False)
keep or not the transition system 
default is false

 -subsLev (<Int>)
set subsumption level
where -1 will turn the subsumption level off
default is -1

 -stateSpace (HashMap|Trie)
setting the state space structure type 
for trie you can add a numeric parameter
\"-stateSystem Trie (no_opt|optm1|optm2|optm3)\" describing the desired level of optimization
default is HashMap

 -invokeGCBeforeEndStatistics
requesting the garbage collector to be activated before the printout of the end statistics

 -printIntermediateMemConsumption
 print intermediate memory consumption"

 -searchHeuristics (DFS|BFS)
chooses if the exploration method will be DFS or BFS.
default is DFS;
