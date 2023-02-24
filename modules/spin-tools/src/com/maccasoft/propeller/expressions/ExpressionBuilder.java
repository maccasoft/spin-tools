package com.maccasoft.propeller.expressions;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Constructs an AST from the given expression tokens.
 *
 * It uses a shunting yard algorithm.
 */
public class ExpressionBuilder {

    private Deque<Expression> operands = new ArrayDeque<Expression>();
    private Deque<Operator> operators = new ArrayDeque<Operator>();
    private int groupCount = 0;

    public ExpressionBuilder() {
        operators.push(SENTINEL);
    }

    public void addValueToken(Expression value) {
        operands.push(value);
    }

    public void addOperatorToken(Operator operator) {
        evaluateNotYieldingTo(operator);

        if (operator == GROUP_OPEN) {
            groupCount++;
            operators.push(operator);
            operators.push(SENTINEL);
        }
        else if (operator == GROUP_CLOSE) {
            groupCount--;
            if (operators.pop() != SENTINEL) {
                throw new RuntimeException("Sentinel expected.");
            }
            if (operator == GROUP_CLOSE && operators.peek() != GROUP_OPEN) {
                throw new ExpressionError("Group open expected.");
            }
        }
        else {
            operators.push(operator);
        }
    }

    public Expression getExpression() {
        if (operands.isEmpty() || operators.isEmpty()) {
            throw new RuntimeException("Operands / operators is empty" + this);
        }

        // process remainder
        evaluateNotYieldingTo(SENTINEL);

        if (operators.size() > 1 && operators.peek() == SENTINEL) {
            throw new ExpressionError("Group close expected.");
        }
        if (operands.size() > 1 || operators.size() != 1) {
            throw new RuntimeException("Not all operands / operators were processed: " + this);
        }

        return operands.pop();
    }

    private void evaluateNotYieldingTo(Operator operator) {
        while (!operators.peek().yieldsTo(operator)) {
            operands.push(operators.pop().evaluate());
        }
    }

    public boolean hasOpenGroup() {
        return groupCount > 0;
    }

    @Override
    public String toString() {
        return "" + operands + " / " + operators;
    }

    private abstract class Operator {

        private Precedence precedence;
        private Associativity associativity;

        private Operator(Precedence precedence, Associativity associativity) {
            this.precedence = precedence;
            this.associativity = associativity;
        }

        public boolean yieldsTo(Operator other) {
            if (associativity == Associativity.LEFT_TO_RIGHT) {
                return precedence.ordinal() > other.precedence.ordinal();
            }
            else {
                return precedence.ordinal() >= other.precedence.ordinal();
            }
        }

        public abstract Expression evaluate();
    }

    public final Operator NOT = new Operator(Precedence.UNARY, Associativity.RIGHT_TO_LEFT) {
        @Override
        public Expression evaluate() {
            return new Not(operands.pop());
        };
    };

    public final Operator LOGICAL_NOT = new Operator(Precedence.UNARY, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            return new LogicalNot(operands.pop());
        };
    };

    public final Operator POSITIVE = new Operator(Precedence.UNARY, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            return new Positive(operands.pop());
        };
    };

    public final Operator NEGATIVE = new Operator(Precedence.UNARY, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            return new Negative(operands.pop());
        };
    };

    public final Operator COMPLEMENT = new Operator(Precedence.UNARY, Associativity.RIGHT_TO_LEFT) {
        @Override

        public Expression evaluate() {
            return new Complement(operands.pop());
        };
    };

    public final Operator ABS = new Operator(Precedence.UNARY, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            return new Abs(operands.pop());
        };
    };

    public final Operator ENCOD = new Operator(Precedence.UNARY, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            return new Encod(operands.pop());
        };
    };

    public final Operator DECOD = new Operator(Precedence.UNARY, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            return new Decod(operands.pop());
        };
    };

    public final Operator BMASK = new Operator(Precedence.UNARY, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            return new Bmask(operands.pop());
        };
    };

    public final Operator SQRT = new Operator(Precedence.UNARY, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            return new Sqrt(operands.pop());
        };
    };

    public final Operator MULTIPLY = new Operator(Precedence.MULTIPLICATION, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Multiply(operands.pop(), operandRight);
        };
    };

    public final Operator DIVIDE = new Operator(Precedence.MULTIPLICATION, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Divide(operands.pop(), operandRight);
        };
    };

    public final Operator MODULO = new Operator(Precedence.MULTIPLICATION, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Modulo(operands.pop(), operandRight);
        };
    };

    public final Operator UNSIGNED_DIVIDE = new Operator(Precedence.MULTIPLICATION, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new UnsignedDivide(operands.pop(), operandRight);
        };
    };

    public final Operator UNSIGNED_MODULO = new Operator(Precedence.MULTIPLICATION, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new UnsignedModulo(operands.pop(), operandRight);
        };
    };

    public final Operator ROUND = new Operator(Precedence.MULTIPLICATION, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            return new Round(operands.pop());
        };
    };

    public final Operator SCA = new Operator(Precedence.MULTIPLICATION, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Sca(operands.pop(), operandRight);
        };
    };

    public final Operator SCAS = new Operator(Precedence.MULTIPLICATION, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Scas(operands.pop(), operandRight);
        };
    };

    public final Operator FRAC = new Operator(Precedence.MULTIPLICATION, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Frac(operands.pop(), operandRight);
        };
    };

    public final Operator ADD = new Operator(Precedence.ADDITION, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Add(operands.pop(), operandRight);
        };
    };

    public final Operator SUBTRACT = new Operator(Precedence.ADDITION, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Subtract(operands.pop(), operandRight);
        };
    };

    public final Operator ADDBITS = new Operator(Precedence.ADDBITS, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Addbits(operands.pop(), operandRight);
        };
    };

    public final Operator ADDPINS = new Operator(Precedence.ADDBITS, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Addpins(operands.pop(), operandRight);
        };
    };

    public final Operator SHIFT_LEFT = new Operator(Precedence.SHIFT, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new ShiftLeft(operands.pop(), operandRight);
        };
    };

    public final Operator SHIFT_RIGHT = new Operator(Precedence.SHIFT, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new ShiftRight(operands.pop(), operandRight);
        };
    };

    public final Operator LESS_THAN = new Operator(Precedence.COMPARISON, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new LessThan(operands.pop(), operandRight);
        };
    };

    public final Operator LESS_OR_EQUALS = new Operator(Precedence.COMPARISON, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new LessOrEquals(operands.pop(), operandRight);
        };
    };

    public final Operator GREATER_THAN = new Operator(Precedence.COMPARISON, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new GreaterThan(operands.pop(), operandRight);
        };
    };

    public final Operator GREATER_OR_EQUALS = new Operator(Precedence.COMPARISON, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new GreaterOrEquals(operands.pop(), operandRight);
        };
    };

    public final Operator EQUALS = new Operator(Precedence.EQUALITY, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Equals(operands.pop(), operandRight);
        };
    };

    public final Operator NOT_EQUALS = new Operator(Precedence.EQUALITY, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new NotEquals(operands.pop(), operandRight);
        };
    };

    public final Operator AND = new Operator(Precedence.AND, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new And(operands.pop(), operandRight);
        };
    };

    public final Operator XOR = new Operator(Precedence.XOR, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Xor(operands.pop(), operandRight);
        };
    };

    public final Operator OR = new Operator(Precedence.OR, Associativity.LEFT_TO_RIGHT) {
        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new Or(operands.pop(), operandRight);
        };
    };

    public final Operator LOGICAL_AND = new Operator(Precedence.LOGICAL_AND, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new LogicalAnd(operands.pop(), operandRight);
        };
    };

    public final Operator LOGICAL_OR = new Operator(Precedence.LOGICAL_OR, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new LogicalOr(operands.pop(), operandRight);
        };
    };

    public final Operator LOGICAL_XOR = new Operator(Precedence.LOGICAL_XOR, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            return new LogicalXor(operands.pop(), operandRight);
        };
    };

    public final Operator TERNARYIF = new Operator(Precedence.TERNARYIFELSE, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            throw new ExpressionError("Ternary if (?) without else (:).");
        };
    };

    public final Operator TERNARYELSE = new Operator(Precedence.TERNARYIFELSE, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            Expression operandRight = operands.pop();
            while (operators.peek() == TERNARYELSE) {
                operands.push(operators.pop().evaluate());
            }
            if (operators.peek() == TERNARYIF) {
                operators.pop();
                Expression operandMiddle = operands.pop();
                return new IfElse(operands.pop(), operandMiddle, operandRight);
            }
            else {
                throw new ExpressionError("Ternary else (:) without if (?).");
            }
        };
    };

    public final Operator GROUP_OPEN = new Operator(Precedence.GROUPING, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            return new Group(operands.pop());
        };
    };

    public final Operator GROUP_CLOSE = new Operator(Precedence.NONE, Associativity.LEFT_TO_RIGHT) {

        @Override
        public Expression evaluate() {
            throw new RuntimeException("Can not evaluate group close.");
        };
    };

    public final Operator SENTINEL = new Operator(Precedence.NONE, Associativity.RIGHT_TO_LEFT) {

        @Override
        public Expression evaluate() {
            throw new RuntimeException("Can not evaluate sentinel.");
        };
    };

    private enum Precedence {
        GROUPING,
        UNARY,
        SHIFT,
        AND,
        XOR,
        OR,
        MULTIPLICATION,
        ADDITION,
        ADDBITS,
        LOGICAL_AND,
        LOGICAL_XOR,
        LOGICAL_OR,
        COMPARISON,
        EQUALITY,
        TERNARYIFELSE,
        NONE
    }

    private enum Associativity {
        LEFT_TO_RIGHT,
        RIGHT_TO_LEFT
    }

    public static class ExpressionError extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public ExpressionError(String message) {
            super("Expression error: " + message);
        }
    }

    public static void main(String[] args) {
        ExpressionBuilder builder = new ExpressionBuilder();
        builder.addValueToken(new NumberLiteral(1));
        builder.addOperatorToken(builder.ADD);
        builder.addOperatorToken(builder.GROUP_OPEN);
        builder.addOperatorToken(builder.GROUP_CLOSE);
        Expression expression = builder.getExpression();
        System.out.println(expression);
    }
}
