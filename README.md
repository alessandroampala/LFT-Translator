# LFT Project
Compilers course project.
The file `main.pdf` contains an exhaustive description of all the exercises.

## Content description
- `1.x` is a collection of the implementation of various DFA (deterministic finite automaton).
- `2.x` contains various versions of Lexer (lexical analyzer).
- `3.x` contains various versions of Parser (syntactic analyzer).
- `4.x` is a simple arithmetic expression evaluator (syntax guided translation).
- `5.x` contains various version  of Translator. The translator is able to translate custom `P` language (invented, matches the grammar described in `main.pdf`) to java mnemonic bytecode.

## How to use the Translator
- Put the program (in `P` language) you want to translate in  a file called `input.txt`.
- Compile the Translator with `javac Translator.java`
- Run the Translator with `java Translator`. If there are no errors, the output `Input OK` should appear and a file called `Output.j` will be generated in the same folder: this is the java mnemonic bytecode that must be assembled with jasmin.
- To assemble it, run `java -jar jasmin.jar Output.j`. A file called `Output.class` will be generated. This is our compiled bytecode file.
- Finally run the program with `java Output`.
