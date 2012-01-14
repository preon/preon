tree grammar LimboWalker;

options {
	tokenVocab=Limbo;
	ASTLabelType=CommonTree;
}

@header {
package org.codehaus.preon.el;

import org.codehaus.preon.el.ast.RelationalNode.Relation;
import org.codehaus.preon.el.ast.ArithmeticNode.Operator;
import org.codehaus.preon.el.ast.BooleanOperatorNode.BooleanOperator;
import org.codehaus.preon.el.ast.*;
}

@members {

private ReferenceContext context;

public LimboWalker(TreeNodeStream input, ReferenceContext context) {
    super(input);
    this.context = context;
}
        
protected void mismatch(IntStream input, int ttype, BitSet follow)
    throws RecognitionException
{
    throw new MismatchedTokenException(ttype, input);
}

public Object recoverFromMismatchedSet(IntStream input,
        RecognitionException e,
        BitSet follow)
    throws RecognitionException
{
    throw e;
}

}

// Alter code generation so catch-clauses get replace with
// this action.
@rulecatch {
    catch (RecognitionException e) {
        throw e;
    }
}

fexpr returns [Node node]
	: a=bexpr { $node = $a.node; }
	| a=vexpr { $node = $a.node; }
	;
	
bexpr returns [Node node]
    :   ^('&&' c=bexpr d=bexpr) { $node = BooleanOperatorNode.create(BooleanOperator.AND, $c.node, $d.node); }
    |   ^('||' c=bexpr d=bexpr) { $node = BooleanOperatorNode.create(BooleanOperator.OR, $c.node, $d.node); }
	|	^('<=' a=vexpr b=vexpr) { $node = RelationalNode.create(Relation.LTE, $a.node, $b.node); }
	|	^('>=' a=vexpr b=vexpr) { $node = RelationalNode.create(Relation.GTE, $a.node, $b.node); }
	|	^('<' a=vexpr b=vexpr) { $node = RelationalNode.create(Relation.LT, $a.node, $b.node); }
	|	^('>' a=vexpr b=vexpr) { $node = RelationalNode.create(Relation.GT, $a.node, $b.node); }
	|	^('==' a=vexpr b=vexpr) { $node = RelationalNode.create(Relation.EQ, $a.node, $b.node); }
	;

vexpr returns [Node node]
	:	^('+' a=vexpr b=vexpr) { $node = ArithmeticNode.create(Operator.plus, $a.node, $b.node); }
	|	^('-' a=vexpr b=vexpr) { $node = ArithmeticNode.create(Operator.minus, $a.node, $b.node); }
	|	^('*' a=vexpr b=vexpr) { $node = ArithmeticNode.create(Operator.mult, $a.node, $b.node); }
	|	^('/' a=vexpr b=vexpr) { $node = ArithmeticNode.create(Operator.div, $a.node, $b.node); }
	|   ^('^' a=vexpr b=vexpr) { $node = ArithmeticNode.create(Operator.pow, $a.node, $b.node); }
	|	INT { $node = new IntegerNode(Integer.parseInt($INT.text)); }
	|   BININT { $node = IntegerNode.fromBin($BININT.text); }
	|   HEXINT { $node = IntegerNode.fromHex($HEXINT.text); }
	|   STRING { $node = new StringNode($STRING.text); }
	|   { java.util.List selectors = new java.util.ArrayList(); }	
	
	^(REFERENCE var=ID (selector { selectors.add($selector.node); })* ) {
			Reference ref = context.selectAttribute($var.text);
			for (int i = 0; i < selectors.size(); i++) {
			    ref = ((Selector) selectors.get(i)).select(ref);
			}
			$node = new ReferenceNode(ref);
		}
	;

zexpr returns [Node node]
    :   a=bexpr { $node = $a.node; }
	|   { java.util.List selectors = new java.util.ArrayList(); }
	^(REFERENCE var=ID (selector { selectors.add($selector.node); })* ) {
			Reference ref = context.selectAttribute($var.text);
			for (int i = 0; i < selectors.size(); i++) {
			    ref = ((Selector) selectors.get(i)).select(ref);
			}
			$node = new ReferenceNode(ref);
		}
	;

selector returns [Selector node]
    :   ^(PROP ID) {
            $node = new PropertySelector($ID.text);
        }
    |   ^(INDEX i=vexpr) {
            $node = new IndexSelector($i.node);
        }
    ; 