package co.com.asulado.usecase.liquidation.liquidatebatch.util;

import co.com.asulado.usecase.liquidation.liquidatebatch.util.gateway.LiquidationHelper;
import co.com.asulado.usecase.liquidation.liquidatebatch.util.util.LiquidationsBatchConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LiquidationHelperImpl implements LiquidationHelper {

    private enum Type { NUMBER, VAR, OP, LPAREN, RPAREN }
    private record Token(Type type, String text) {}

    private static final Pattern NUM = Pattern.compile("\\G\\d+(?:\\.\\d+)?");
    private static final Pattern VAR = Pattern.compile("\\G[A-Za-z_][A-Za-z0-9_]*");

    private static final Map<String, Integer> PREC = Map.of(
            "+", 1, "-", 1,
            "*", 2, "/", 2,
            "^", 3
    );
    private static final Set<String> RIGHT_ASSOC = Set.of("^");

    public double evaluateFormula(String formula, Map<String, Number> values) {
        if (formula == null || formula.isBlank()) {
            throw new IllegalArgumentException(LiquidationsBatchConstants.ERR_FORMULA_NULL_OR_BLANK);
        }
        List<Token> tokens = tokenize(formula);
        List<Token> rpn = toReversePolishNotation(tokens);
        return evalReversePolishNotation(rpn, values);
    }

    private List<Token> tokenize(String s) {
        List<Token> out = new ArrayList<>();
        int i = 0;
        while (i < s.length()) {
            char c = s.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }

            switch (c) {
                case '+', '-', '*', '/', '^' -> { out.add(new Token(Type.OP, String.valueOf(c))); i++; continue; }
                case '(' -> { out.add(new Token(Type.LPAREN, "(")); i++; continue; }
                case ')' -> { out.add(new Token(Type.RPAREN, ")")); i++; continue; }
            }

            Matcher mNum = NUM.matcher(s);
            mNum.region(i, s.length());
            if (mNum.find()) { out.add(new Token(Type.NUMBER, mNum.group())); i = mNum.end(); continue; }

            Matcher mVar = VAR.matcher(s);
            mVar.region(i, s.length());
            if (mVar.find()) { out.add(new Token(Type.VAR, mVar.group())); i = mVar.end(); continue; }

            throw new IllegalArgumentException(String.format(
                    LiquidationsBatchConstants.ERR_INVALID_CHAR_AT_POS, i, c));
        }
        return handleUnarySigns(out);
    }

    private List<Token> handleUnarySigns(List<Token> in) {
        List<Token> out = new ArrayList<>();
        Token prev = null;
        for (Token t : in) {
            if (t.type == Type.OP && (t.text.equals("+") || t.text.equals("-"))) {
                boolean unary = (prev == null) ||
                        prev.type == Type.OP ||
                        prev.type == Type.LPAREN;
                if (unary) {
                    out.add(new Token(Type.NUMBER, "0"));
                }
            }
            out.add(t);
            prev = t;
        }
        return out;
    }

    private List<Token> toReversePolishNotation(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Deque<Token> stack = new ArrayDeque<>();
        for (Token t : tokens) {
            switch (t.type) {
                case NUMBER, VAR -> output.add(t);
                case OP -> {
                    while (!stack.isEmpty() && stack.peek().type == Type.OP &&
                            (higherPrec(stack.peek().text, t.text))) {
                        output.add(stack.pop());
                    }
                    stack.push(t);
                }
                case LPAREN -> stack.push(t);
                case RPAREN -> {
                    while (!stack.isEmpty() && stack.peek().type != Type.LPAREN) {
                        output.add(stack.pop());
                    }
                    if (stack.isEmpty() || stack.peek().type != Type.LPAREN) {
                        throw new IllegalArgumentException(LiquidationsBatchConstants.ERR_MISMATCHED_PARENTHESES);
                    }
                    stack.pop();
                }
            }
        }
        while (!stack.isEmpty()) {
            Token t = stack.pop();
            if (t.type == Type.LPAREN || t.type == Type.RPAREN) {
                throw new IllegalArgumentException(LiquidationsBatchConstants.ERR_MISMATCHED_PARENTHESES);
            }
            output.add(t);
        }
        return output;
    }

    private boolean higherPrec(String opOnStack, String current) {
        int ps = PREC.getOrDefault(opOnStack, -1);
        int pc = PREC.getOrDefault(current, -1);
        if (ps > pc) return true;
        if (ps < pc) return false;
        return !RIGHT_ASSOC.contains(opOnStack);
    }

    private double evalReversePolishNotation(List<Token> rpn, Map<String, Number> values) {
        Deque<Double> st = new ArrayDeque<>();
        for (Token t : rpn) {
            switch (t.type) {
                case NUMBER -> st.push(Double.parseDouble(t.text));
                case VAR -> {
                    Number v = values.get(t.text);
                    if (v == null) {
                        throw new IllegalArgumentException(String.format(
                                LiquidationsBatchConstants.ERR_MISSING_VALUE_FOR_VAR, t.text));
                    }
                    st.push(v.doubleValue());
                }
                case OP -> {
                    double b = pop(st, String.format(LiquidationsBatchConstants.ERR_RIGHT_OPERAND_FOR, t.text));
                    double a = pop(st, String.format(LiquidationsBatchConstants.ERR_LEFT_OPERAND_FOR, t.text));
                    st.push(apply(t.text, a, b));
                }
                default -> throw new IllegalStateException(LiquidationsBatchConstants.ERR_MALFORMED_EXPRESSION);
            }
        }
        if (st.size() != 1) {
            throw new IllegalStateException(LiquidationsBatchConstants.ERR_MALFORMED_EXPRESSION);
        }
        return st.pop();
    }

    private double pop(Deque<Double> st, String message) {
        Double v = st.poll();
        if (v == null) {
            throw new IllegalStateException(String.format(
                    LiquidationsBatchConstants.ERR_MISSING_PREFIX, message));
        }
        return v;
    }

    private double apply(String op, double a, double b) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> {
                if (b == 0.0) {
                    throw new ArithmeticException(LiquidationsBatchConstants.ERR_DIVISION_BY_ZERO);
                }
                yield a / b;
            }
            case "^" -> Math.pow(a, b);
            default -> throw new IllegalArgumentException(String.format(
                    LiquidationsBatchConstants.ERR_UNKNOWN_OP, op));
        };
    }
}
