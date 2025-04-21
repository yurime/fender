# Fender -- A Model Checker for Boolean Programs
Largely based on a model checker for relaxed memory models by Michael Kuperstein.
This version is only for boolean programs and assumes Sequential Consistency.

## Dependencies: 
- java 8
- libz3-java -- if predicate inference use is desired

## To run:
`java -jar ./lib/fender.jar {filename}.bl sc`


### The mandatory arguments to fender are:

 {the name of the program to analize} 
 
 (sc | pso | set | must | may) 

>For now, the only available option is: sc  
### Optional arguments are:
 `-errorPath (succinct|onlyComments|full|none)`

 `-synthesizePredicatesInto \<predicateFileName\>`

>Set this option for predicate inference. Requires also
	` -predicateFile \<predicateFileName\> ` flag and
    needs LD_LIBRARY_PATH to be set to the location of libz3java.so
    
`-predicateFile \<predicateFileName\>`

>For predicate inference from the error trace. Using this option one should specify the predicate file with which the boolean program was created. Can be an empty file.

 `-predicateSynthesisMethod (Interp|WP)`
 
>In what method to synthesize predicates. \"Interp\" for interpolation; \"WP\" for Weakest Precondition.
Default \"WP\"

 `-debugLevel \<an int indicating the debug level\>`
 
>Default is 0 

` -printUsedCubes (true|perStatement|false)`

>Default is false 

` -trackLabel (pc[\<DesiredPID\>]=\<DesiredLabel\>)`

>To print the program state each time the execution reaches label \<DesiredLabel\> at program id \<DesiredPID\>

 `-deleteStatesInAtomics (True|False)  `

>Delete states in atomics.
Default is true

 `-keepTransitionSystem (True|False)`
 
>To keep or not the transition system.
Default is false.

` -subsLev \<Int\>`

>Set subsumption level.
Where -1 will turn the subsumption level off.
Default is -1

` -stateSpace (HashMap|Trie)`

>Setting the state space structure type.
For trie you can add a parameter describing the desired level of optimization `-stateSystem Trie (no_opt|optm1|optm2|optm3)`.
Default is HashMap

 `-invokeGCBeforeEndStatistics`

>Requesting the garbage collector to be activated before the printout of the end statistics

 `-printIntermediateMemConsumption`

>Print intermediate memory consumption

 `-searchHeuristics (DFS|BFS)`

>Chooses if the exploration method will be DFS or BFS.
default is DFS;
